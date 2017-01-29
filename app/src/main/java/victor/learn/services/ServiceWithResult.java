package victor.learn.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import victor.learn.activities.MainActivity;

public class ServiceWithResult extends Service {
    public ServiceWithResult() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final PendingIntent pi = intent.getParcelableExtra("intent");
        final Context context = this;
            new Thread(){
                @Override
                public void run() {
                    try {
                        pi.send(context, 100, null);
                        Thread.sleep(1000);
                        pi.send(context, 100, null);
                        Thread.sleep(1000);
                        pi.send(context, 100, null);

                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("destroy","service");
    }
}
