package com.test.app

import java.text.SimpleDateFormat
import java.util.{Date, Properties, Random}

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

/**
  * @author Created by Jeff Yang on 2020-01-13 13:15.
  * Update date:
  * Project: flink-learning
  * Package: com.test.app
  * Describe :   模拟生产采集的设备内存、cpu等 json 结构的数据
  *              举例：
  *                  {"collect_timestamp":1578899351631,"device_id":10003,"device_name":"cdh-kafka-dw-ha-103","device_ip":"172.16.2.103","cpu": 0.98,"memory":0.86,"space":0.88}
  * Dependency :
  * Frequency: Calculate once a day.
  * Result of Test: test ok
  * Command: 在 idea 中，右键执行
  *
  * Email:  highfei2011@126.com
  * Status：Using online
  *
  * Please note:
  *    Must be checked once every time you submit a configuration file is correct!
  *    Data is priceless! Accidentally deleted the consequences!
  *
  */
object TestProduceDeviceData {

  def main(args: Array[String]): Unit = {

    val topic = "device_alert_beijing"

    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("acks", "all")
    props.put("retries", "0")
    props.put("batch.size", "16384")
    props.put("linger.ms", "1")
    props.put("buffer.memory", "33554432")
    props.put(
      "key.serializer",
      "org.apache.kafka.common.serialization.StringSerializer"
    )
    props.put(
      "value.serializer",
      "org.apache.kafka.common.serialization.StringSerializer"
    )
    // 设备信息
    val deviceArray =
      Array(
        (10001,"cdh-kafka-dw-ha-100","172.16.2.100"),
        (10003,"cdh-kafka-dw-ha-103","172.16.2.103"),
        (20006,"cdh-kafka-dw-ha-106","172.16.2.106")
      )

    // 拼接 Json 数据
    val messageStart = "{\"collect_timestamp\":"

    val producer = new KafkaProducer[String, String](props)
    for (i <- 1 to 20) {
      Thread.sleep(500)

      val device=deviceArray(new Random().nextInt(3))

      val cpuRate=Math.random().formatted("%.2f")

      val messageTemp = ",\"device_id\":"+device._1+",\"device_name\":\""+device._2+"\",\"device_ip\":\""+device._3+"\","

      val num=if (i<10)  s"0$i" else i

      val currentTime=System.currentTimeMillis()
      val times=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(currentTime))

      val message = messageStart+currentTime+messageTemp+"\"cpu\":"+cpuRate+",\"memory\":0.86,\"space\":0.88}"
      producer.send(
      new ProducerRecord[String, String](
          topic,
          Integer.toString(i),
          message
      )
      )
      System.out.println(s"发送第 [$num] 条数据,时间: [$times]，设备id：[${device._1}] ，内存占用率 $cpuRate")
    }
    producer.close()

  }
}
