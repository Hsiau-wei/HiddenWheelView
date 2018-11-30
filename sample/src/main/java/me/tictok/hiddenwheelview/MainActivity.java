package me.tictok.hiddenwheelview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.tictok.hiddenwheelviewlibrary.HiddenWheelView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            stringList.add("item " + i);
        }
        final TextView textView = findViewById(R.id.textView);
        HiddenWheelView wheelView = findViewById(R.id.wheelView);
        WheelAdapter adapter = new WheelAdapter(stringList, R.layout.sample_wheel_item);
        wheelView.setAdapter(adapter);
        wheelView.setOnSnapListener(new HiddenWheelView.OnSnapListener() {
            int currentPosition;

            @Override
            public void onSnap(int position) {
                currentPosition = position;
                textView.setText("Snap: " + currentPosition);
            }

            @Override
            public void onStopScrolling() {
                textView.setText("Stop scrolling, current: " + currentPosition);
            }
        });
    }
}
