package jp.techacademy.matsuo.yuuta.ra_mentimer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.LinearLayout
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    //Timer関連
    private var mTimer: Timer?=null
    private var mTimerSec = 0
    private var mHandler = Handler()
    //レイアウト
    private lateinit var view: LinearLayout
    //通知関連
    private lateinit var notificationManager: NotificationManager
    private lateinit var builder : NotificationCompat.Builder

    private var timerTask = object : TimerTask() {
        override fun run() {
            mTimerSec += 1

            //分と秒に分解
            var min = mTimerSec / 60
            var sec: Int = mTimerSec % 60

            mHandler.post {
                timeText.text = min.toString() + ":" + "%0,2d".format(sec)
                if (min == 0 && sec == 1) {
                    view.background = getDrawable(R.color.color_0)
                    image.setImageResource(R.drawable.noodle_negate)
                }
                if(min==3 && sec==0) {
                    view.background = getDrawable(R.color.color_3)
                    image.setImageResource(R.drawable.noodle_eat_negate)
                    showNotification("ラーメン（３分のやつ）ができあがりました！")
                }
                if(min==5 && sec==0) {
                    view.background = getDrawable(R.color.color_5)
                    image.setImageResource(R.drawable.udon_eat_negate)
                    showNotification("うどん（５分のやつ）ができあがりました！")
                    mTimer!!.cancel()
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view = findViewById<LinearLayout>(R.id.layout)
        button.text = "CANCEL"
        
        //通知関連の設定
        notificationManager = this!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // SDKバージョンが26以上の場合チャネルを設定
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel("default",
                "Channel name",
                NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "Channel description"
            notificationManager.createNotificationChannel(channel)
        }

        // 通知の設定を行う
        builder = NotificationCompat.Builder(this, "default")
        builder.setSmallIcon(R.drawable.noodle_eat_negate)

        // 通知をタップしたらアプリを起動するようにする
        val startAppIntent = Intent(this, MainActivity::class.java)
        startAppIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
        val pendingIntent = PendingIntent.getActivity(this, 0, startAppIntent, 0)
        builder.setContentIntent(pendingIntent)

        //タイマースタート
        mTimer = Timer()
        mTimer!!.schedule(timerTask,0,1000)

        //ボタンのリスナー
        button.setOnClickListener {
            if(mTimer != null) {
                mTimer!!.cancel()
                this.finish()
            }
        }
    }

    //通知を表示する関数
    fun showNotification(mes: String){
        builder.setContentText(mes)
        notificationManager.notify(100, builder.build()) //通知を送信
    }
}