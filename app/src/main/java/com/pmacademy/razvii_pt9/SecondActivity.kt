package com.pmacademy.razvii_pt9

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class SecondActivity : AppCompatActivity() {

    companion object {

        private const val NUMBER_KEY = "com.pmacademy.razvii_pt9_NUMBER_KEY"
        private const val TEXT_KEY = "com.pmacademy.razvii_pt9_TEXT_KEY"

        fun start(context: Context, number: Int, text: String) {
            val intent = Intent(context, SecondActivity::class.java)
            intent.putExtra(NUMBER_KEY, number)
            intent.putExtra(TEXT_KEY, text)
            context.startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        setValueInTextView()
    }

    private fun setValueInTextView() {
        val number = intent.getIntExtra(NUMBER_KEY, 0)
        val text = intent.getStringExtra(TEXT_KEY)

        findViewById<TextView>(R.id.tvNumber).text = number.toString()
        findViewById<TextView>(R.id.tvText).text = text
    }

}