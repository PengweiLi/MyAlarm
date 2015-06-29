package com.lipengwei.myalarm;

import com.lipengwei.myalarm.provider.AlarmInstance;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyAlarmReceiver extends BroadcastReceiver {
	
	public final static String INSTANCE_ID = "com.lipengwei.myalarm.instanceid";
	
    @Override
    public void onReceive(Context context, Intent intent) {
        
    	if(intent.getAction().equals("com.lipengwei.myalarm.alarmon")) {
            Log.i("Lipengwei","alarm ring");
            final long instanceId = AlarmInstance.getId(intent.getData());
            Log.i("Lipengwei","instanceId =" + instanceId);
            startAlarmActivity(context,instanceId);
    	}
    }
    
    private void startAlarmActivity(Context context,long instanceId) {
        Intent intent = new Intent(context, AlarmActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(INSTANCE_ID, instanceId);
        context.startActivity(intent);
    }

}
