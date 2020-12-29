package com.pmacademy.razvii_pt9

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput


private const val BUTTON_ACTION = "com.pmacademy.razvii_pt9.RECEIVER_BUTTON"

class MainActivity : AppCompatActivity() {

    companion object {
        const val NOTIFICATION_REPLY_TEXT_ACTION = "com.pmacademy.razvii_pt9.key_text_reply"
        const val NOTIFICATION_REPLY_ACTION = "com.pmacademy.razvii_pt9.NOTIFICATION_REPLY_ACTION"

    }

    private val TAG = MainActivity::class.java.simpleName
    private val CHANNEL_ID = "com.pmacademy.razvii_pt9.CHANEL_ID"

    private var randNumber: Int = 0
    private var randText: String = ""

    private lateinit var receiverWifi: BroadcastReceiver
    private lateinit var receiverBtn: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupButtonsListeners() //bind onClickListeners on each button

        //start two local BroadcastReceiver
        createBroadcastReceiverWifi()
        createBroadcastReceiverBtn()

    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiverBtn)
    }

    override fun onResume() {
        super.onResume()
        registerBroadcastReceiverBtn()
    }

    override fun onDestroy() {
        unregisterReceiver(receiverWifi)
        super.onDestroy()
    }

    private fun createNotificationManager(): NotificationManager {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "PM Academy Channel"
            val channel = NotificationChannel(
                CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        return notificationManager
    }

    private fun createSimpleNotification() {
        val notificationManager = createNotificationManager()
        val notificationId = 2755
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.star)
            .setContentTitle("Simple notification")
            .setContentText("Hello, I'm simple notification")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        notificationManager.notify(notificationId, builder.build())
    }

    private fun createNotificationWithButton() {
        val notificationManager = createNotificationManager()
        val notificationId = 2756
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            0
        )
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.star)
            .setContentTitle("Notification with button")
            .setContentText("Hello, I'm notification with button")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.drawable.star, "Push me", pendingIntent)
        notificationManager.notify(notificationId, builder.build())
    }

    private fun createNotificationWithReply() {
        val notificationManager = createNotificationManager()
        val notificationId = 2757
        val replyLabel = "Enter your reply here"
        val remoteInput: RemoteInput = RemoteInput.Builder(NOTIFICATION_REPLY_TEXT_ACTION).run {
            setLabel(replyLabel)
            build()
        }
        // Create intent for ReplyNotificationBroadcastReceiver
        val intent = Intent(this, ReplyNotificationBroadcastReceiver::class.java)
        intent.action = NOTIFICATION_REPLY_ACTION
        intent.putExtra("KEY_NOTIFICATION_ID", notificationId)

        // Create pending intent for the reply button
        val replyPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            this,
            101,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Create reply action and add the remote input
        val action: NotificationCompat.Action = NotificationCompat.Action.Builder(
            R.drawable.star,
            "Reply",
            replyPendingIntent
        ).addRemoteInput(remoteInput)
            .setAllowGeneratedReplies(true)
            .build()

        // Build a notification and add the action
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.star)
            .setContentTitle("Notification with reply")
            .setContentText("Hello, I'm notification with reply. Please, enter the text")
            .addAction(action)

        notificationManager.notify(notificationId, builder.build())
    }

    private fun createNotificationWithProgress() {
        val notificationId = 2758
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.star)
            .setContentTitle("Notification with progress")
            .setContentText("Hello, I'm notification with progress")
            .setPriority(NotificationCompat.PRIORITY_LOW)
        val progressMax = 100
        var progressCurrent = 0

        NotificationManagerCompat.from(this).apply {
            builder.setProgress(progressMax, progressCurrent, false)
            notify(notificationId, builder.build())
            val runnable = Runnable {
                while (progressMax >= progressCurrent) {
                    progressCurrent++
                    builder.setProgress(progressMax, progressCurrent, false)
                    notify(notificationId, builder.build())
                    Thread.sleep(100)
                    if (progressMax == progressCurrent) {
                        builder.setContentText("Progress complete")
                            .setProgress(0, 0, false)
                        notify(notificationId, builder.build())
                    }
                }
            }
            Thread(runnable).start()
        }
    }

    private fun createBroadcastReceiverWifi() {
        receiverWifi = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d(TAG, "RECEIVER_WIFI - " + intent?.action.toString())
                Toast.makeText(
                    this@MainActivity,
                    "Network state changed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        registerBroadcastReceiverWifi()
    }

    private fun registerBroadcastReceiverWifi() {
        val intentFilter = IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        registerReceiver(receiverWifi, intentFilter)
    }

    private fun createBroadcastReceiverBtn() {
        receiverBtn = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d(TAG, "RECEIVER_BUTTON - user tap on button")
            }
        }
        registerBroadcastReceiverBtn()
    }

    private fun registerBroadcastReceiverBtn() {
        val intentFilter = IntentFilter(BUTTON_ACTION)
        registerReceiver(receiverBtn, intentFilter)
    }


    private fun sendToBroadcastReceiverBtn() {
        val intent = Intent()
        intent.action = BUTTON_ACTION
        sendBroadcast(intent)
    }

    private fun startMapActivity() {
        val uri =
            Uri.parse("https://www.google.com/maps/@?api=1&map_action=map&center=50.449602,30.524547&zoom=15")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun prepareDataToSecondctivity() { //generate variable for second activity
        randNumber = (Math.random() * 10000).toInt()
        randText = ""
        val source = "abcdefghijklmnopqrstuvwxyz"
        for (i in 1..10) {
            randText += source[(Math.random() * 26).toInt()]
        }
    }

    private fun setupButtonsListeners() {
        findViewById<Button>(R.id.btnSimpleNotify).setOnClickListener {
            createSimpleNotification()
        }
        findViewById<Button>(R.id.btnButtonNotify).setOnClickListener {
            createNotificationWithButton()
        }
        findViewById<Button>(R.id.btnReplyNotify).setOnClickListener {
            createNotificationWithReply()
        }
        findViewById<Button>(R.id.btnProgressNotify).setOnClickListener {
            createNotificationWithProgress()
        }
        findViewById<Button>(R.id.btnOpenNewActivity).setOnClickListener {
            prepareDataToSecondctivity()
            SecondActivity.start(this, randNumber, randText)
        }
        findViewById<Button>(R.id.btnOpenMaps).setOnClickListener {
            startMapActivity()
        }
        findViewById<Button>(R.id.btnSendBroadcast).setOnClickListener {
            sendToBroadcastReceiverBtn()
        }
    }
}