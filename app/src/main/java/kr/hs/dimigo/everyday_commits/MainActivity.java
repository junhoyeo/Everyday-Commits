package kr.hs.dimigo.everyday_commits;

import android.graphics.Color;
import android.os.StrictMode;
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
            result.setText("오늘 커밋 " + commits + "개");
            result.setTextColor(Color.parseColor("#FFF2F2"));
            bgElement.setBackgroundColor(Color.parseColor("#04102A"));
        }
        refresh.setVisibility(View.VISIBLE);
    }

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

        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateStatus();
            }
        });
    }
}
