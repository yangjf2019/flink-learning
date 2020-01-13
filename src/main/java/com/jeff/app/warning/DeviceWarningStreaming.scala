package com.jeff.app.warning

import java.util.Properties

import com.alibaba.fastjson.JSON
import org.apache.commons.lang3.StringUtils
import org.apache.flink.api.common.functions.MapFunction
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.api.java.functions.KeySelector
import org.apache.flink.streaming.api.functions.{AssignerWithPunctuatedWatermarks, KeyedProcessFunction}
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.watermark.Watermark
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.util.Collector

/**
  * @author Created by Jeff Yang on 2020-01-12 17:42.
  * Update date:
  * Project: flink-learning-record
  * Package: com.jeff.learning.flink
  * Describe :   如果某台设备的内存占用，在1秒内持续升高则发出告警
  * Dependency :
  * Frequency: Calculate once a day.
  * Result of Test: test ok
  * Command:
  *
  * Email:  highfei2011@126.com
  * Status：Using online
  *
  * Please note:
  *    Must be checked once every time you submit a configuration file is correct!
  *    Data is priceless! Accidentally deleted the consequences!
  *
  */
object DeviceWarningStreaming {
  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val properties = new Properties()
    val topic="device_alert_beijing"
    properties.setProperty("bootstrap.servers", "localhost:9092")
    properties.setProperty("group.id", "test-device")

    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env
      .addSource(
        new FlinkKafkaConsumer[String](
          topic,
          new DySimpleStringSchema(),
          properties
        )
      )
      .filter(value => !StringUtils.isEmpty(value))
      .map(new ParseMap())
      // 按 DeviceId 分区
      .keyBy(new KeySelector[DeviceInfo, String]() {
        override def getKey(value: DeviceInfo): String =
          value.getDeviceId.toString
      })
      .process(new DeviceAlertFunction)
      .print()
    env.execute("test-device-alert")
  }


  /**
    * 解析 json 数据
    */
  class ParseMap extends MapFunction[String, DeviceInfo] {
    override def map(value: String): DeviceInfo = {
      val jsonObject = JSON.parseObject(value)

      val deviceInfo = new DeviceInfo(
        jsonObject.getLong("collect_timestamp"),
        jsonObject.getInteger("device_id"),
        jsonObject.getString("device_name"),
        jsonObject.getString("device_ip"),
        jsonObject.getDouble("cpu"),
        jsonObject.getDouble("memory"),
        jsonObject.getDouble("space")
      )

      deviceInfo
    }
  }

  /**
    * 如果1秒内，设备内存占用持续增加，则告警
    */
  class DeviceAlertFunction
      extends KeyedProcessFunction[String, DeviceInfo, String] {

    // 存储最近一次设备内存占用率
    lazy val lastRate: ValueState[Double] = getRuntimeContext.getState(
      new ValueStateDescriptor[Double]("cpu", classOf[Double])
    )

    // 存储当前活动计数器的时间戳
    lazy val currentTimer: ValueState[Long] = getRuntimeContext.getState(
      new ValueStateDescriptor[Long]("collect_timestamp", classOf[Long])
    )

    override def processElement(
      d: DeviceInfo,
      ctx: KeyedProcessFunction[String, DeviceInfo, String]#Context,
      out: Collector[String]
    ): Unit = {
      // 获取前一个内存占用率
      val prevRate = lastRate.value()

      //更新最近一次的内存占用率
      lastRate.update(d.getCpu)

      val curTimerTimestamp = currentTimer.value()
      // 处理逻辑
      if (prevRate == 0.0 || d.getCpu < prevRate) {
        // cpu 占用率下降，则删除当前计时器
        ctx.timerService().deleteProcessingTimeTimer(curTimerTimestamp)
        currentTimer.clear()
      } else if (d.getCpu > prevRate && curTimerTimestamp == 0) {
        // cpu 占用率升高且未设置计时器
        // 以当前时间 +1 秒设置处理时间计时器
        val timerTs = ctx.timerService().currentProcessingTime() + 1000
        ctx.timerService().registerProcessingTimeTimer(timerTs)
        // 记住当前的计时器
        currentTimer.update(timerTs)
      }
    }

    override def onTimer(
      timestamp: Long,
      ctx: KeyedProcessFunction[String, DeviceInfo, String]#OnTimerContext,
      out: Collector[String]
    ): Unit = {
      out.collect(
        "The memory usage of the device #" + ctx.getCurrentKey + "# continues to increase within 1 second!"
      )

    }
  }

}
