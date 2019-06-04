WakefulBroadcastReceiver 是一种特殊的广播接收器. 它可以自动创建和管理唤醒锁 PARTIAL_WAKE_LOCK 来执行任务. 确保耗时任务执行完毕之前设备不会休眠.

使用：
	通过 startWakefulService(Context, Intent) 启动 Service 而不是 startService(). WakefulBroadcastReceiver 启动 Service 的时候会自动创建唤醒锁, 并在 Intent 附上唤醒锁的 ID 来判断这个唤醒锁.最后必须在 Service 中调用 completeWakefulIntent(intent) 释放唤醒锁