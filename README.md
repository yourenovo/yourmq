## 项目简介

本项目是一个基于Netty实现的的消息中间件，主要用于消息的发布和订阅,用于学习使用。

## 功能简介

### 生产者（客户端）

- 发布多种类型消息：普通消息、QoS 0 消息、定时消息、顺序消息、可过期消息、事务消息、广播消息

### 消费者（客户端）

- 订阅和取消订阅消息
- 支持消费确认（ACK）机制，包含自动和手动两种方式

### 服务端

- 提供确认功能：发布确认（Publish-Confirm）、订阅确认（Subscribe-Confirm）、取消订阅确认（Unsubscribe-Confirm）
- 支持消息派发策略：消息派发重试（Dispatch-Retry）、延迟派发（Dispatch-Delayed）
- 采用单线程架构
- 具备快照持久化能力
- 支持集群热伸缩特性