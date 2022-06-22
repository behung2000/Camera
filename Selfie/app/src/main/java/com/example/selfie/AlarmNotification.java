package com.example.selfie;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmNotification extends Activity {

    TextView tv_timer;
    int tvHour,tvMinute;
    ImageButton back, cancel;
    Button notify;
    String final_time="";
    AlarmManager alarmManager;
    Intent intent;
    PendingIntent pendingIntent;
    boolean status=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_main);
        createNotificationChannel();
        back = findViewById(R.id.return_from_notification);
        tv_timer = findViewById(R.id.timer);
        notify = findViewById(R.id.confirm_notification);
        cancel = findViewById(R.id.cancel_notification);

        ButtonSetup();
    }

    private void ButtonSetup(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AlarmNotification.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                tvHour= hourOfDay;
                                tvMinute = minute;
                                String time = tvHour + ":" + tvMinute;
                                final_time = time;
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat f24hours = new SimpleDateFormat(
                                        "HH:mm"
                                );
                                try {
                                    Date date = f24hours.parse(time);
                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat f12hours = new SimpleDateFormat("hh:mm aa");
                                    tv_timer.setText(f12hours.format(date));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        },12,0,false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(tvHour,tvMinute);
                timePickerDialog.show();

            }
        });

        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (final_time.equals(""))
                {
                    Toast.makeText(AlarmNotification.this,"You must select a correct time to turn on notification",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    status=true;
                    Toast.makeText(AlarmNotification.this,"This app will notify you at "+tv_timer.getText()+" everyday",Toast.LENGTH_SHORT).show();

                    intent = new Intent(AlarmNotification.this,NotificationBroadcast.class);
                    pendingIntent = PendingIntent.getBroadcast(AlarmNotification.this, 0, intent, 0);
                    alarmManager =(AlarmManager) AlarmNotification.this.getSystemService(Context.ALARM_SERVICE);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, tvHour);
                    calendar.set(Calendar.MINUTE, tvMinute);

                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!status)
                {
                    Toast.makeText(AlarmNotification.this, "You haven't set up notification yet",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(AlarmNotification.this, "Notification will now be turn off", Toast.LENGTH_SHORT).show();
                    alarmManager.cancel(pendingIntent);
                }
            }
        });
    }

    private void createNotificationChannel()
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            CharSequence name = "Selfie App Notification Channel";
            String description = "Channel for selfie app notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("nofificationChannelID",name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
