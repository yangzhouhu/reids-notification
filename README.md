# reids-notification

基于redis和springboot实现的延时业务处理，将redis的配置文件中开启notify-keyspace-events Ex
然后在项目中配置对应的space或者event事件监听即可，主要利用的是pub/sub的方式来实现事件触发，可用于延时任务等场景
