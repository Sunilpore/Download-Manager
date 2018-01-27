package com.example.sunil.downloadmanager10;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    private static String Mytag="myTag";
    private static final int MY_PERMISSION_REQUEST =100 ;

    Context mContext;
    Toolbar myToolbar;
    CoordinatorLayout cdlay;
    //private RippleDrawable ripple;
    EditText setURL;
    Button download,clear;

    String sdkName;
    int sdkNo,fileSize,fileTimeout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setURL= (EditText) findViewById(R.id.tv_url);
        download= (Button) findViewById(R.id.download_button);
        cdlay= (CoordinatorLayout) findViewById(R.id.cord_lay);
        myToolbar= (Toolbar) findViewById(R.id.toolbar_lay);
        clear= (Button) findViewById(R.id.clear_url);

        // ripple=(RippleDrawable) download.getBackground();

        setSupportActionBar(myToolbar);

        this.mContext=this;

        //displayAndroidVersion();

        //defineRippleDynamically();

        //This will good practice to allow app permission at run time to user which defined in a .class file
        //If you are define permission in Manifest then use your write access method inside try-catch to avoid app crash for Above KitKat Version
        //This method not create problem for below Lollipop version.Hence it is good practice to use it.
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
        }


        //Using this method you can implement Ripple via Manifest
        //This is safest mode as it is take care of lower and above version of Loolipop and prevent app from crashing.
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NotificationCompat.Builder mBuilder= new NotificationCompat.Builder(mContext);
                String url = setURL.getText().toString();
                //String url = "https://www.theplanningcenter.com/wp-content/uploads/2016/10/qtq80-MXfZgt.jpeg";

                ConnectivityManager cm= (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo nwinfo=cm.getActiveNetworkInfo();

                boolean isConnected= nwinfo!=null && nwinfo.isConnectedOrConnecting();

                if(isConnected){

                    if(url.equals("")){

                        //Toast.makeText(MainActivity.this,"Android Version:"+ Build.VERSION.SDK_INT,Toast.LENGTH_LONG).show();
                        Snackbar sn = Snackbar.make(cdlay, "Please,Enter the URL", Snackbar.LENGTH_LONG);
                        sn.getView().setBackgroundColor(ContextCompat.getColor(mContext,R.color.snackbar));
                        sn.show();

                    }
                    else if(Patterns.WEB_URL.matcher(url).matches()){

                        //This will work to display filesize to user before download a file
                        /*new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    URL myUrl=new URL(url);
                                    URLConnection urlConnection=myUrl.openConnection();
                                    urlConnection.connect();
                                    int fileSize=urlConnection.getContentLength();
                                    int fileTimeout=urlConnection.getConnectTimeout();
                                    Log.d("myTag","filesize:"+fileSize+"\nTimeout:"+fileTimeout);
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                    Log.d("myTag","error:"+e);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Log.d("myTag","error:"+e);
                                }

                            }
                        }).start();*/

                        Log.d("myTag","URL:"+url);

                        try{

                            String service= Context.DOWNLOAD_SERVICE;
                            DownloadManager downloadManager;

                            downloadManager= (DownloadManager) getSystemService(service);

                            //Uri uri=Uri.parse("http://www.gadgetsaint.com/wp-content/uploads/2016/11/cropped-web_hi_res_512.png");
                            Uri uri=Uri.parse(url);

                            String fileName = URLUtil.guessFileName(url, null,
                                    MimeTypeMap.getFileExtensionFromUrl(url));

                            DownloadManager.Request request=new DownloadManager.Request(uri);

                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName);
                            mBuilder.setProgress(0,0,true);
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            Long ref=downloadManager.enqueue(request);

                        }catch(Exception e)
                        {
                            //Toast.makeText(MainActivity.this,"ERROR:"+e,Toast.LENGTH_LONG).show();
                            Toast.makeText(MainActivity.this,"Allow storage permission for app",Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Snackbar sn = Snackbar.make(cdlay, "Invalid URL", Snackbar.LENGTH_LONG);
                        sn.getView().setBackgroundColor(Color.GRAY);
                        sn.show();
                    }

                }
                else
                    Toast.makeText(mContext, "Network is Disconnected", Toast.LENGTH_LONG).show();


            }
        });



        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setURL.setText("");
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){

            case R.id.about_detials:

                StringBuffer buf=new StringBuffer();

                buf.append("Version,1.1\nLast update:26/1/18\n\n");
                buf.append("Developed By,\nSunil Pore\n\n");
                buf.append("Contact US:sunilpore95@yahoo.com");
                aboutusMsg("About",buf.toString());
                //Toast.makeText(mContext, "\t\t\t\t\t\t\t\tv1.0\n Last modified:1/13/18 ", Toast.LENGTH_SHORT).show();
                break;
            

        }

        return super.onOptionsItemSelected(item);
    }

    private void aboutusMsg(String title, String msg){
        AlertDialog.Builder build=new AlertDialog.Builder(this);
        build.setTitle(title);
        build.setMessage(msg);
        build.show();
        build.setCancelable(true);

    }

    /*private void displayAndroidVersion(){
        if(Build.VERSION.SDK_INT>=4){
            int versioID=0;
            switch ( versioID){

                case 4: sdkName="Ice Cream Sandwich";
                        sdkNo=4;
            }

        }
    }*/

    public void defineRippleDynamically(){

        /*//This method is used when you define Ripple through .class file not through manifest
          //This unsafe method to use Ripple as it will crash for below Lollipop version app.
          //Because ripple is support from API21 and above.
        download.setOnTouchListener (new View.OnTouchListener(){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                //We use this if clause to prevent setOnTouchListener() method to run twice and it should run only once on Button click
                if(event.getAction()== MotionEvent.ACTION_DOWN){

                    //ripple.setHotspotBounds((int) event.getX(),10, (int)event.getY(),10);
                    ripple.setHotspot(event.getX(),event.getY());
                    ripple.setColor(ColorStateList.valueOf(getResources().getColor(R.color.color)));
                    NotificationCompat.Builder mBuilder= new NotificationCompat.Builder(mContext);
                    String url = setURL.getText().toString();

                    if(!url.equals("")){

                        Log.d("myTag","URL:"+url);

                        try{

                            String service= Context.DOWNLOAD_SERVICE;
                            DownloadManager downloadManager;

                            downloadManager= (DownloadManager) getSystemService(service);

                            Uri uri=Uri.parse(url);

                            String fileName = URLUtil.guessFileName(url, null,
                                    MimeTypeMap.getFileExtensionFromUrl(url));

                            DownloadManager.Request request=new DownloadManager.Request(uri);

                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName);
                            mBuilder.setProgress(0,0,true);
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            Long ref=downloadManager.enqueue(request);
                        }
                        catch (Exception e){
                            Toast.makeText(MainActivity.this,"Allow storage permission for app",Toast.LENGTH_LONG).show();
                        }



                    }
                    else {
                        Snackbar sn = Snackbar.make(cdlay, "Please,Enter the URL", Snackbar.LENGTH_LONG);
                        sn.getView().setBackgroundColor(Color.GRAY);
                        sn.show();
                        //Toast.makeText(MainActivity.this, "Please,Enter the URL", Toast.LENGTH_SHORT).show();
                    }

                }

                return false;
            }
        });*/
    }

}
