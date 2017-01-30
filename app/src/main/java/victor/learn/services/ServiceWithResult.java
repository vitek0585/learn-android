package victor.learn.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import victor.learn.activities.MainActivity;

public class ServiceWithResult extends Service {
    public ServiceWithResult() {
    }

    public IBinder onBind(Intent intent) {
        Log.d("LOG_TAG", "MyService onBind");
        return new Binder();
    }

    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d("LOG_TAG", "MyService onRebind");
    }

//    Попробуем запустить сервис методом startService и, пока он работает, несколько раз подключимся и отключимся. Жмем Start.
//    MyService onCreate
//    Сервис запущен.
//    Подключаемся и отключаемся, т.е. жмем Bind
//    MyService onBind
//    MainActivity onServiceConnected
//    а затем Unbind.
//            MyService onUnbind
//    Сработали методы onBind и onUnbind в сервисе, и onServiceConnected в ServiceConnection.
//    Еще раз подключаемся и отключаемся - жмем Bind, а затем Unbind
//    MainActivity onServiceConnected
//    При повторном подключении к сервису методы onBind и onUnbind не сработали. Только onServiceConnected.
//    И далее, сколько бы мы не подключались, так и будет.
//    Остановим сервис – нажмем Stop.

//    Это поведение можно скорректировать.
//    Для этого необходимо возвращать true в методе onUnbind. Сейчас мы там вызываем метод супер-класса, а он возвращает false.
// Последующие подключения и отключения сопровождаются вызовами методов onRebind и onUnbind.
// Таким образом, у нас есть возможность обработать в сервисе каждое повторное подключение/отключение.
    public boolean onUnbind(Intent intent) {
        Log.d("LOG_TAG", "MyService onUnbind");
         super.onUnbind(intent);
        return true;//return TRUE
    }

    MyBinder binder = new MyBinder();

    public void helloWorld() {

    }

    public class MyBinder extends Binder {
        public ServiceWithResult getService() {
            return ServiceWithResult.this;
        }
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
