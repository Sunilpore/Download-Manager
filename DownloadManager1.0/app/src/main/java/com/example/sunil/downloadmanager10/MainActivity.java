package com.example.sunil.downloadmanager10;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import io.reactivex.Observable;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.sunil.downloadmanager10.api.ServiceCall;
import com.example.sunil.downloadmanager10.api.ServiceClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static String Mytag="myTag";
    private static final int MY_PERMISSION_REQUEST =100 ;

    Context mContext;
    Toolbar myToolbar;
    ProgressBar progBar;
    CoordinatorLayout cdlay;
    //private RippleDrawable ripple;
    EditText setURL;
    Button download,clear,test;
    boolean permission=false;
    boolean downloadStart = true;
    long ref;
    int perc = 0,newperc=0,download_bytes,total_bytes;
    DownloadManager downloadManager;

    NotificationCompat.Builder mBuilder;
    Handler handler;
    String sdkName;
    int sdkNo,fileSize,fileTimeout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setURL= (EditText) findViewById(R.id.tv_url);
        download= (Button) findViewById(R.id.download_button);
        test = (Button) findViewById(R.id.test_button);
        cdlay= (CoordinatorLayout) findViewById(R.id.cord_lay);
        myToolbar= (Toolbar) findViewById(R.id.toolbar_lay);
        clear= (Button) findViewById(R.id.clear_url);
        progBar = (ProgressBar) findViewById(R.id.progress_bar);
        // ripple=(RippleDrawable) download.getBackground();

        setSupportActionBar(myToolbar);

        handler = new Handler();
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

                mBuilder= new NotificationCompat.Builder(mContext);
                final String url = setURL.getText().toString();
                //String url = "https://www.theplanningcenter.com/wp-content/uploads/2016/10/qtq80-MXfZgt.jpeg";

                ConnectivityManager cm= (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo nwinfo=cm.getActiveNetworkInfo();

                boolean isConnected= nwinfo!=null && nwinfo.isConnectedOrConnecting();

                if(isConnected){

                    if(url.equals("")){

                        //Toast.makeText(MainActivity.this,"Android Version:"+ Build.VERSION.SDK_INT,Toast.LENGTH_LONG).show();
                        Snackbar sn = Snackbar.make(cdlay, "Please,Enter the URL", Snackbar.LENGTH_INDEFINITE);
                        sn.getView().setBackgroundColor(ContextCompat.getColor(mContext,R.color.snackbar));
                        sn.show();

                        /*ViewGroup contentLay = (ViewGroup) sn.getView().findViewById(android.support.design.R.id.snackbar_text).getParent();
                        ProgressBar item = new ProgressBar(mContext);
                        item.setProgress(20);
                        item.setMax(100);
                        contentLay.addView(item,0);*/

                        //disableSwipeToDismiss(sn);

                    }
                    else if(Patterns.WEB_URL.matcher(url).matches()) {




                        //This will work to display filesize to user before download a file


                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Looper.prepare();

                                try {
                                    URL myUrl=new URL(url);
                                    URLConnection urlConnection=myUrl.openConnection();
                                    urlConnection.setRequestProperty("Accept-Encoding", "identity");
                                    urlConnection.connect();
                                    fileSize=urlConnection.getContentLength();
                                    int fileTimeout=urlConnection.getConnectTimeout();

                                    String fileName = URLUtil.guessFileName(url, null,
                                            MimeTypeMap.getFileExtensionFromUrl(url));
                                     DisplayFileSize(urlConnection.getContentLength(),url);


                                    Log.d("myTag","filesize:"+fileSize+"\tTimeout:"+fileTimeout+" \nfileName: "+fileName+"\tmime type: "+MimeTypeMap.getFileExtensionFromUrl(url));
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                    Log.d("myTag","error:"+e);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Log.d("myTag","error:"+e);
                                }
                                Looper.loop();
                            }

                        }).start();



                        /*if(DisplayFileSize()){
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


                        }*/

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


        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String url = setURL.getText().toString();

                ServiceCall apiCall = ServiceClient.getClient().create(ServiceCall.class);

                Observable <ResponseBody> mObservable =  apiCall.getFileDownloadData(url);

                mObservable.subscribeOn(Schedulers.io())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<ResponseBody>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(ResponseBody responseBody) {
                             long size = responseBody.contentLength();


                             Log.d("FileSizeRx","size: "+size);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });


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


    private boolean DisplayFileSize(int size, final String url){

        String fileName = URLUtil.guessFileName(url, null,
                MimeTypeMap.getFileExtensionFromUrl(url));
        MimeTypeMap.getFileExtensionFromUrl(url);

        String msg = "File Name: "+fileName+" \nFile Type: "+MimeTypeMap.getFileExtensionFromUrl(url)+
                " \nFile size: "+filesizeDifferentiation(size);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(msg+" \nDo you want to download...")
        .setCancelable(false)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                permission=true;
                dialogInterface.cancel();
                startDownload(url);

            }
        })

        .setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                permission=false;
            }
        });

        AlertDialog alert = builder.create();
        alert.setTitle("Download a File");
        alert.show();
    return permission;
    }

    private void startDownload(String url) {

        try{
            Log.d("Mortal","Download initiated: ") ;

            String service= Context.DOWNLOAD_SERVICE;

            downloadManager= (DownloadManager) getSystemService(service);

            //Uri uri=Uri.parse("http://www.gadgetsaint.com/wp-content/uploads/2016/11/cropped-web_hi_res_512.png");
            Uri uri=Uri.parse(url);

            String fileName = URLUtil.guessFileName(url, null,
                    MimeTypeMap.getFileExtensionFromUrl(url));

            DownloadManager.Request request=new DownloadManager.Request(uri);

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName);
            mBuilder.setProgress(0,0,true);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            ref=downloadManager.enqueue(request);







            while (downloadStart) {
                DownloadManager.Query downloadquery = new DownloadManager.Query();
                downloadquery.setFilterById(ref);

                Cursor cursor = downloadManager.query(downloadquery);
                cursor.moveToFirst();
                download_bytes = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                total_bytes = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    downloadStart=false;
                    //break;
                }
                perc = download_bytes*100/total_bytes;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                if(perc!=newperc){
                    progBar.setBackgroundColor(getResources().getColor(R.color.progress));
                    progBar.setVisibility(View.VISIBLE);
                    progBar.setProgress(perc);
                    progBar.setMax(100);
                    if(perc==100)
                        progBar.setVisibility(View.GONE);

                Log.d("Mortal","total bytes: "+total_bytes+"\tdownload so far:"+download_bytes+" \n perc: "+perc+"\tnew per: "+newperc) ;
                newperc=perc;
                }
            }
                });
            }

            Log.d("Mortal","Download finished: ") ;



        }catch(Exception e)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progBar.setVisibility(View.GONE);
                }
            });

            Log.d("Mortal","ERROR: "+e.getMessage()) ;
            //Toast.makeText(MainActivity.this,"ERROR:"+e,Toast.LENGTH_LONG).show();
           // Toast.makeText(MainActivity.this,"Allow storage permission for app",Toast.LENGTH_LONG).show();
        }

    }


    private String filesizeDifferentiation(int size){

        DecimalFormat dec = new DecimalFormat("#.00");

        String fsize = null;
        if(size<1000){          //Kb
            fsize = size+"B";
        } else if(size <1000000){  //Mb
            fsize = dec.format((float) size/1000)+"Kb";
        } else if(size <1000000000){  //Gb
            fsize = dec.format((float) size/1000000)+"Mb";
        } else if (size > 1000000000){
            fsize = dec.format((float) size/1000000000)+"Gb";
        }


        Log.d("Marvel","file size: "+size+"\t new: "+fsize );

        return fsize;
    }

    private void disableSwipeToDismiss(Snackbar sn) {
        final View view = sn.getView();
        sn.show();

        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                ((CoordinatorLayout.LayoutParams) view.getLayoutParams()).setBehavior(null);
                return true;
            }
        });

    }


    private void displayAndroidVersion(){
     /*   if(Build.VERSION.SDK_INT>=4){
            int versioID=0;
            switch ( versioID){

                case 4: sdkName="Ice Cream Sandwich";
                        sdkNo=4;
            }

        }*/
    }

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
