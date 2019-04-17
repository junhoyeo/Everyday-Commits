package kr.hs.dimigo.everyday_commits;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    LinearLayout bgElement;
    TextView result;
    Button refresh;

    public int getNotificationId() {
        return (int) SystemClock.uptimeMillis();
    }

    public int getCommits() {
        try {
            Document document = Jsoup.connect("https://www.github.com/JunhoYeo").get();
            Element rect = document.select("rect").last();

            int commits = Integer.parseInt(rect.attr("data-count"));
            System.out.println(commits);
            return commits;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void updateStatus() {
        refresh.setVisibility(View.GONE);
        int commits = getCommits();
        if (commits == 0) { // none
            result.setText("오늘 커밋 없음");
            result.setTextColor(Color.parseColor("#FFF2F2"));
            bgElement.setBackgroundColor(Color.parseColor("#FF4F40"));
        } else if (commits == -1) { // error
            result.setText("에러");
            result.setTextColor(Color.parseColor("#F3F1BC"));
            bgElement.setBackgroundColor(Color.parseColor("#040E27"));
        } else {
            result.setText("오늘 기여 " + commits + "개");
            result.setTextColor(Color.parseColor("#FFF2F2"));
            bgElement.setBackgroundColor(Color.parseColor("#04102A"));
        }
        refresh.setVisibility(View.VISIBLE);
    }

    public void sendNotification(String text) {
        int NOTIFICATION_ID = getNotificationId();
        String CHANNEL_ID = "EC_channel_01";

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "EC_channel";
            String Description = "This is everyday_commits channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("매일매일 커밋")
                .setContentText(text);

        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this)
                .addParentStack(MainActivity.class)
                .addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    Handler handler = new Handler();
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            int commits = getCommits();
            if (commits == 0)
                sendNotification("오늘 깃허브 커밋을 하지 않았어요!");
            else
                sendNotification("오늘 " + commits + "개의 커밋을 했어요!");
            handler.postDelayed(runnableCode, 1000 * 60 * 60);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        bgElement = (LinearLayout) findViewById(R.id.container);
        result = (TextView) findViewById(R.id.tvResult);
        refresh = (Button) findViewById(R.id.btnRefresh);

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        updateStatus();

        handler.post(runnableCode);

        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateStatus();
            }
        });
    }
}
