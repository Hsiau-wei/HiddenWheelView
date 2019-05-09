package me.tictok.hiddenwheelview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import me.tictok.library.HiddenWheelView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val stringArray = Array(30) { i -> "item $i" }
        val adapter = WheelAdapter(stringArray, R.layout.sample_wheel_item)
        wheelView.setAdapter(adapter)
        wheelView.onSnapListener = object : HiddenWheelView.OnSnapListener {
            var currentPosition: Int = 0

            override fun onStopScrolling() {
                textView.text = "Stop scrolling, current: $currentPosition"
            }

            override fun onSnap(position: Int) {
                currentPosition = position
                textView.text = "Snap: $currentPosition"
            }

        }

    }
}