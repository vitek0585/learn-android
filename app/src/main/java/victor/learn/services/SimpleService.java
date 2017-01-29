package victor.learn.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//А метод stopSelfResult(startId) аналогичен методу stopSelf(startId),
//        но при этом еще возвращает boolean значение – остановил он сервис или нет.
public class SimpleService extends Service {
    private static final String LOG_TAG = "LOG";
    ExecutorService es;
    Object someRes;

    @Override
    public void onCreate() {
        super.onCreate();
        es = Executors.newFixedThreadPool(1);
        someRes = new Object();
    }

    //    START_NOT_STICKY – сервис не будет перезапущен после того, как был убит системой
//
//    START_STICKY – сервис будет перезапущен после того, как был убит системой
//
//    START_REDELIVER_INTENT – сервис будет перезапущен после того, как был убит системой. Кроме этого,
//    сервис снова получит все вызовы startService, которые не были завершены методом stopSelf(startId).

    //А второй параметр flags метода onStartCommand дает нам понять, что это повторная попытка вызова onStartCommand.
    //А вот на флаг START_FLAG_REDELIVERY можно положиться. Если он пришел вам в методе onStartCommand, значит,
    // прошлый вызов этого метода вернул START_REDELIVER_INTENT, но не был завершен успешно методом stopSelf(startId).
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int index = intent.getIntExtra("index", 0);
        es.execute(new MyRun(index, startId));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    class MyRun implements Runnable {

        int time;
        int startId;

        public MyRun(int time, int startId) {
            this.time = time;
            this.startId = startId;
            Log.d(LOG_TAG, "MyRun#" + startId + " create");
        }

        public void run() {
            Log.d(LOG_TAG, "MyRun#" + startId + " start, time = " + time);
            try {
                TimeUnit.SECONDS.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Log.d(LOG_TAG, "MyRun#" + startId + " someRes = " + someRes.getClass());
            } catch (NullPointerException e) {
                Log.d(LOG_TAG, "MyRun#" + startId + " error, null pointer");
            }
            stop();
        }

        void stop() {
            Log.d(LOG_TAG, "MyRun#" + startId + " end, stopSelf(" + startId + ")");
            stopSelf(startId);
        }
    }
}
