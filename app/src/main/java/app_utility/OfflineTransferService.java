package app_utility;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.autochip.golecha.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import static android.content.ContentValues.TAG;
import static androidx.core.app.NotificationCompat.PRIORITY_MAX;

public class OfflineTransferService extends Service implements OnAsyncTaskInterface {

    String channelId = "app_utility.OfflineTransferService";
    String channelName = "offline_transfer";

    public static OfflineTransferService refOfService;
    /*startService(new Intent(MyService.ServiceIntent));
    stopService(new Intent((MyService.ServiceIntent));*/

    public static OnAsyncTaskInterface onAsyncInterfaceListener;
    //SharedPreferencesClass sharedPreferencesClass;

    NotificationManager notifyMgr;
    NotificationCompat.Builder nBuilder;
    NotificationCompat.InboxStyle inboxStyle;

    //AsyncInterface asyncInterface;

    Timer timer = new Timer();
    Handler handler = new Handler();
    public String VOLLEY_STATUS = "NOT_RUNNING";

    long startTime = 0;
    long endTime = 0;
    long totalTime = 0;

    //BELAsyncTask belAsyncTask;
    DatabaseHandler db;

    private int nDBID;
    ArrayList<DataBaseHelper> alDBTemporaryData;
    LinkedHashMap<String, ArrayList<String>> lhmData = new LinkedHashMap<>();
    private LinkedHashMap<Integer, ArrayList<String>> lhmSavedData = new LinkedHashMap<>();


    public OfflineTransferService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /*
        this will make sure service will run on background in oreo and above
        service wont run without a notification from oreo version.
        After the system has created the service, the app has five seconds to call the service's startForeground() method
        to show the new service's user-visible notification. If the app does not call startForeground() within the time limit,
        the system stops the service and declares the app to be ANR.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground();
        }

        refOfService = this;
        onAsyncInterfaceListener = this;
        db = new DatabaseHandler(getApplicationContext());

        //sharedPreferencesClass = new SharedPreferencesClass(getApplicationContext());
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        ArrayList<DataBaseHelper> alDBData = new ArrayList<>(db.getProductsByrStatusFilter("PLACE"));
                        if(alDBData.size()>0 && VOLLEY_STATUS.equals("RUNNING")){
                            for (int i=0;i<alDBData.size(); i++){
                                nDBID = alDBData.get(i).get_id();
                                ArrayList<String> alProductID = new ArrayList<>(Arrays.asList(alDBData.get(i).get_product_id_string().split(",")));
                                ArrayList<String> alProductName = new ArrayList<>(Arrays.asList(alDBData.get(i).get_product_name().split(",")));
                                ArrayList<String> alProductQuantity = new ArrayList<>(Arrays.asList(alDBData.get(i).get_product_quantity_string().split(",")));
                                ArrayList<String> alUnitPrice = new ArrayList<>(Arrays.asList(alDBData.get(i).get_unit_price_string().split(",")));
                                ArrayList<String> alSubTotal = new ArrayList<>(Arrays.asList(alDBData.get(i).get_sub_total_string().split(",")));

                                for (int j=0;j<alProductID.size();j++){
                                    ArrayList<String> alData = new ArrayList<>();
                                    alData.add(alProductID.get(j));
                                    alData.add(alProductQuantity.get(j));
                                    alData.add(alUnitPrice.get(j));
                                    alData.add(alSubTotal.get(j));
                                    lhmData.put(alProductName.get(j), alData);
                                }

                                /*GolechaAsyncTask golechaAsyncTask = new GolechaAsyncTask(getApplicationContext(), onAsyncInterfaceListener, lhmData);
                                golechaAsyncTask.execute(String.valueOf(2), "");*/
                                VOLLEY_STATUS = "RUNNING";
                            }

                                /*nDBID = alDBData.get(0).get_id();
                                String sProductID = alDBData.get(0).get_product_id_string().split(",")[0];
                                String sProductName = alDBData.get(0).get_product_name().split(",")[0];
                                String sProductQuantity = alDBData.get(0).get_product_quantity_string().split(",")[0];
                                String sProductUnitPrice = alDBData.get(0).get_unit_price_string().split(",")[0];
                                String sSubTotal = alDBData.get(0).get_sub_total_string().split(",")[0];

                                ArrayList<String> alData = new ArrayList<>();
                                alData.add(sProductID);
                                alData.add(sProductQuantity);
                                alData.add(sProductUnitPrice);
                                alData.add(sSubTotal);

                                lhmData.put(sProductName, alData);
                                VivanFloraAsyncTask vivanFloraAsyncTask = new VivanFloraAsyncTask(getApplicationContext(), onAsyncInterfaceListener, lhmData);
                                vivanFloraAsyncTask.execute(String.valueOf(2), "");
                                VOLLEY_STATUS = "RUNNING";*/

                        }
                        /*if (sharedPreferencesClass.getUserOdooID() == StaticReferenceClass.DEFAULT_ODOO_ID) {
                            belAsyncTask = new BELAsyncTask(getApplicationContext());
                            belAsyncTask.execute(String.valueOf(5), sharedPreferencesClass.getUserName());
                        } else {
                            alDBTemporaryData = new ArrayList<>(db.getAllTemporaryData());
                            if (alDBTemporaryData.size() >= 1 && VOLLEY_STATUS.equals("NOT_RUNNING")) {
                                belAsyncTask = new BELAsyncTask(getApplicationContext(), alDBTemporaryData, db);
                                belAsyncTask.execute(String.valueOf(6), String.valueOf(sharedPreferencesClass.getUserOdooID()));
                                VOLLEY_STATUS = "RUNNING";
                            } else {
                                int count = alDBTemporaryData.size();
                                if (count >= 1)
                                    notifyUser(String.valueOf(count));
                            }
                        }*/
                        //Toast.makeText(getApplicationContext(), "I am still running", Toast.LENGTH_LONG).show();
                        Log.e("Service status: ", "RUNNING");
                    }
                });
            }
        };
        //Starts after 20 sec and will repeat on every 20 sec of time interval.
        timer.schedule(doAsynchronousTask, 0, 10000);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startForeground() {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), createNotificationChannel());
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(PRIORITY_MAX)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(101, notification);
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel() {
        NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notifyMgr.createNotificationChannel(chan);
        return channelId;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent in) {
        Log.e("Service is killed", "");
        super.onTaskRemoved(in);
        /*if (sharedPreferenceClass.getUserTraceableInfo()) {
            Intent intent = new Intent("app_utility.TrackingService.ServiceStopped");
            sendBroadcast(intent);
        }*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //TrackingService.this.stopSelf();
        timer.cancel();
        timer.purge();

        //refOfService.stopForeground(true);
        refOfService.stopSelf();
        /*if (sharedPreferenceClass.getUserTraceableInfo()) {
            Intent intent = new Intent("app_utility.TrackingService.ServiceStopped");
            sendBroadcast(intent);
        }*/

        Log.i(TAG, "Service destroyed ...");
    }

    /*@Override
    public void onAsyncComplete(String sMSG, int type, String sResult) {
        switch (sMSG) {
            case "ODOO_ID_RETRIEVED":
                if (type != StaticReferenceClass.DEFAULT_ODOO_ID) {
                    sharedPreferencesClass.setUserOdooID(type);
                }
                break;
        }
        VOLLEY_STATUS = "NOT_RUNNING";
    }*/


    private void notifyUser(String sCount) {
        inboxStyle = new NotificationCompat.InboxStyle();
        notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        /*Intent acceptIntent = new Intent(TrackingService.this, TrackingBroadCastReceiver.class);
        acceptIntent.setAction("android.intent.action.ac.user.accept");
        PendingIntent acceptPI = PendingIntent.getBroadcast(TrackingService.this, 0, acceptIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        Intent declineIntent = new Intent(TrackingService.this, TrackingBroadCastReceiver.class);
        declineIntent.setAction("android.intent.action.ac.user.decline");
        PendingIntent declinePI = PendingIntent.getBroadcast(TrackingService.this, 0, declineIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);*/

        nBuilder = new NotificationCompat.Builder(OfflineTransferService.
                this, channelId)
                .setSmallIcon(R.drawable.vivan_flora_logo)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(sCount)
                .setSubText(sCount)
                //.addAction(R.drawable.download, "Accept", acceptPI)
                //.addAction(R.drawable.download, "Decline", declinePI)
                //.setContentIntent(acceptPI)
                //.setContentIntent(declinePI)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX);
        // Allows notification to be cancelled when user clicks it
        nBuilder.setAutoCancel(true);
        nBuilder.setStyle(inboxStyle);
        int notificationId = 515;
        notifyMgr.notify(notificationId, nBuilder.build());
    }


    @Override
    public void onAsyncTaskComplete(String sCase, int nFlag, LinkedHashMap<String, ArrayList<String>> lhmData, ArrayList<Integer> alImagePosition) {
        switch (sCase) {
            case "SUBMITTED_PLACED_DATA":
                db.deleteData(nDBID);
                VOLLEY_STATUS= "NOT_RUNNING";
                /*if (type != StaticReferenceClass.DEFAULT_ODOO_ID) {
                    sharedPreferencesClass.setUserOdooID(type);
                }*/
                break;
        }
        VOLLEY_STATUS = "NOT_RUNNING";
    }
}
