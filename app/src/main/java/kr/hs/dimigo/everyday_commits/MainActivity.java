package kr.hs.dimigo.everyday_commits;

import android.graphics.Color;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        result = (TextView) findViewById(R.id.tvResult);
        LinearLayout bgElement = (LinearLayout) findViewById(R.id.container);

        try {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Document document = Jsoup.connect("https://www.github.com/JunhoYeo").get();
            Element rect = document.select("rect").last();

            int commits = Integer.parseInt(rect.attr("data-count"));
            System.out.println(commits);

            if (commits == 0) {
                result.setText("오늘 커밋 없음");
                result.setTextColor(Color.parseColor("#FFF2F2"));
                bgElement.setBackgroundColor(Color.parseColor("#FF4F40"));
            } else {
                result.setText("오늘 커밋 " + commits + "개");
                result.setTextColor(Color.parseColor("#FFF2F2"));
                bgElement.setBackgroundColor(Color.parseColor("#04102A"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
