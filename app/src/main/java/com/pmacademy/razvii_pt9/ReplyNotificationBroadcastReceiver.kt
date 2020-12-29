package com.pmacademy.razvii_pt9

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.RemoteInput

class ReplyNotificationBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.apply {
            if (MainActivity.NOTIFICATION_REPLY_ACTION == action) {
                val message = getReplyMessageText(this)
                Toast.makeText(context, "Message from notification: $message", Toast.LENGTH_LONG)
                    .show()
            }

            context?.apply {
                val notificationId = getIntExtra("KEY_NOTIFICATION_ID", 0)
                if (notificationId != 0) {
                    val notificationManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancel(notificationId)
                }
            }
        }
    }

    private fun getReplyMessageText(intent: Intent): CharSequence? {
        return RemoteInput.getResultsFromIntent(intent)
            ?.getCharSequence(MainActivity.NOTIFICATION_REPLY_TEXT_ACTION)
    }
}