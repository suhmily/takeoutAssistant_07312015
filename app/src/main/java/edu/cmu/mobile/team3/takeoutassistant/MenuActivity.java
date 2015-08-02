package edu.cmu.mobile.team3.takeoutassistant;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


public class MenuActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Bundle bundle = getIntent().getExtras();
        Restaurant pm = (Restaurant) bundle.get("menu");

        // set restaurant name
        this.setTitle(pm.getName());

        // set address
        ((TextView) findViewById(R.id.addressTextView)).setText(pm.getAddress());

        // set phone number
        Button callButton = (Button) findViewById(R.id.callButton);
        callButton.setText(pm.getPhone());
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                        + ((Button) view).getText())));
            }
        });

        // set head image

        try {
            InputStream inputStream = getImageViewInputStream(pm.getImage());
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ((ImageView) findViewById(R.id.headPhoto)).setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // set review
        ((TextView) findViewById(R.id.reviewTextView)).setText("Review: " + pm.getReview());

        // set category
        ((TextView) findViewById(R.id.catagoryTextView)).setText("Category: " + pm.getCategory());

        // set snippet summary
        ((TextView) findViewById(R.id.snippetTextView)).setText("Summary: " + pm.getSnippet());

        // set rating
        try {
            InputStream inputStream = getImageViewInputStream(pm.getRating());
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ((ImageView) findViewById(R.id.rating)).setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // set url
        Button openBtn = (Button) findViewById(R.id.openYelpURL);
        openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://www.google.com";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    private float getRawSize(int unit, float value) {
        Resources res = this.getResources();
        return TypedValue.applyDimension(unit, value, res.getDisplayMetrics());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_of_menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_back) {
            Intent intent = new Intent();
            intent.setClass(MenuActivity.this, MainActivity.class);
            startActivity(intent);
            MenuActivity.this.finish();
            return true;
        } else if (id == R.id.share_twit) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Share to Twitter");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ConfigurationBuilder cb = new ConfigurationBuilder();
                    cb.setDebugEnabled(true)
                            .setOAuthConsumerKey("9Uf77tLBxWHnaVADJkRQk5fqY")
                            .setOAuthConsumerSecret("TfUimzpTL2QiKPeFPfPiAWUtz1O57BYNEPwaqPomy0WFOhQzCR")
                            .setOAuthAccessToken("571302755-YMt4k5NemET9ZNBR60YqInVg9m1kdfNd1d9Hflvm")
                            .setOAuthAccessTokenSecret("IT6TAzT3lN6c1oSG16IlfnciTSQSJJd81cVOArlw6nlf2");
                    TwitterFactory tf = new TwitterFactory(cb.build());
                    Twitter twitter = tf.getInstance();

                    String statusMessage = input.getText().toString();
                    StatusUpdate status = new StatusUpdate(statusMessage);

                    try {
                        twitter.updateStatus(status);
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 从网络中获取图片，以流的形式返回
     *
     * @return
     */
    public static InputStream getImageViewInputStream(String path) throws IOException {
        InputStream inputStream = null;
        URL url = new URL(path);                    //服务器地址
        if (url != null) {
            //打开连接
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(3000);//设置网络连接超时的时间为3秒
            httpURLConnection.setRequestMethod("GET");        //设置请求方法为GET
            httpURLConnection.setDoInput(true);                //打开输入流
            int responseCode = httpURLConnection.getResponseCode();    // 获取服务器响应值
            if (responseCode == HttpURLConnection.HTTP_OK) {        //正常连接
                inputStream = httpURLConnection.getInputStream();        //获取输入流
            }
        }
        return inputStream;
    }
}
