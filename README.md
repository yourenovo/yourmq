## 项目简介

本项目是一个基于Netty实现的的消息中间件，主要用于消息的发布和订阅。

## 功能简介

| 角色  | 功能                                                     | 
|-----|--------------------------------------------------------|
| 生产者（客户端） | 发布普通消息、Qos0消息、定时消息、顺序消息、可过期消息、事务消息、广播消息       |
|     |                                                        |  
| 消费者（客户端） | 订阅、取消订阅。消费-ACK（自动、手动）                             |    
|     |                                                        |    
| 服务端 | 发布-Confirm、订阅-Confirm、取消订阅-Confirm、派发-Retry、派发-Delayed | 
| 服务端 | 单线程架构、快照持久化、集群热伸缩 | 
