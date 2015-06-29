package com.lipengwei.myalarm;

import java.util.Calendar;

import com.lipengwei.myalarm.helper.AlarmUtils;
import com.lipengwei.myalarm.provider.Alarm;
import com.lipengwei.myalarm.provider.AlarmInstance;
import com.lipengwei.myalarm.ui.TextTime;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class AlarmActivity extends Activity{
    
    private TextTime clock;
    private int hour;
    private int minute;
    private Calendar c;
    private Handler handler;
    private long alarmInstanceId;
    private AlarmInstance instance;
    private Alarm alarm;
    private Calendar timeOut;
    private TextView alarmInfo;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_alarm);
        init();
        initAlarm();
    }
    
    private void init() {
        clock = (TextTime)findViewById(R.id.alarm_activity_texttime);
        alarmInfo = (TextView)findViewById(R.id.alarm_info);
        handler = new Handler();
        handler.removeCallbacks(thread);
        handler.postDelayed(thread, 1000);
        Intent intent = getIntent();
        alarmInstanceId = intent.getLongExtra(MyAlarmReceiver.INSTANCE_ID, -1);
        instance = AlarmInstance.getInstance(this.getContentResolver(), alarmInstanceId);
        timeOut = instance.getTimeout(getApplicationContext());
        long alarmId = instance.mAlarmId;
        alarm = Alarm.getAlarm(this.getContentResolver(), alarmId);
        alarmInfo.setText(alarm.label);
        AlarmRing.start(this, instance, false);
    }
    
    private void initAlarm() {
        ContentResolver cr = this.getContentResolver();
        if(!alarm.daysOfWeek.isRepeating()){
            alarm.enabled = false;
            Alarm.updateAlarm(cr, alarm);
        } else {
            updateAlarm(alarm);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
    }
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
            clock.setTime(hour, minute);
            if(c.after(timeOut)) {
                AlarmActivity.this.finish();
            }
            handler.postDelayed(thread, 60*1000);
        }
    });
    
    public void onClick(View view) {
        AlarmRing.stop(this);
        this.finish();
    }
    
    private void updateAlarm(final Alarm alarm) {
        final Context context = AlarmActivity.this.getApplicationContext();
        ContentResolver cr = context.getContentResolver();
        AlarmInstance instance;
                // Update alarm
                AlarmUtils.deleteAllInstances(context, alarm.id);
                Alarm.updateAlarm(cr, alarm);
                instance = setupAlarmInstance(context, alarm);
                if(alarm.enabled) {
                    AlarmUtils.startAlarmOnQuarterHour(context, instance);
                }
    }
    
    private static AlarmInstance setupAlarmInstance(Context context, Alarm alarm) {
        ContentResolver cr = context.getContentResolver();
        AlarmInstance newInstance = alarm.createInstanceAfter(Calendar.getInstance());
        newInstance = AlarmInstance.addInstance(cr, newInstance);
        return newInstance;
    }

}
