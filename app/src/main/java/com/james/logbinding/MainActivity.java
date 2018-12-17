package com.james.logbinding;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.james.logbinding_annotations.BindView;
import com.james.logbinding_core.LogBinding;

public class MainActivity extends AppCompatActivity {

    @BindView(id = R.id.textview, value = "TextViewTest")
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogBinding.bind(this);
    }


}
