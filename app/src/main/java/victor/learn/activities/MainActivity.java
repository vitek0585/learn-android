package victor.learn.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.sql.Date;

import victor.learn.R;
import victor.learn.services.ServiceWithResult;
import victor.learn.services.SimpleService;

public class MainActivity extends AppCompatActivity {

    TextView tv;
    TextView statistics;
    Runnable runnableStatistic;

    static myHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
        statistics = (TextView) findViewById(R.id.statistics);

        if (handler == null)
            handler = new myHandler(this);
        else
            handler.setTarget(this);

        runnableStatistic = new Runnable() {
            @Override
            public void run() {
                statistics.setText(new Date(System.currentTimeMillis()).toString());
            }
        };
    }

    //getresult from other activity
//    В onActivityResult мы видим следующие параметры:
//    requestCode – тот же идентификатор, что и в startActivityForResult. По нему определяем, с какого Activity пришел результат.
//            resultCode – код возврата. Определяет успешно прошел вызов или нет.
//    data – Intent, в котором возвращаются данные
//    И requestCode используется, чтобы отличать друг от друга пришедшие результаты.
//    А resultCode – позволяет определить успешно прошел вызов или нет.
    // request is come when activity is closed
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            String name = data.getStringExtra("name");
            tv.setText(name + " " + requestCode);
        }

        if(requestCode ==SERVICE_PENDING_INTENT_RESULT){
            tv.setText("SERVICE" + new Date(System.currentTimeMillis()).getTime());
        }
    }

    public void start(View view) {

        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    try {
                        Thread.sleep(3000);
                        handler.sendEmptyMessage(i);
                        handler.post(runnableStatistic);
                        //Delayed
                        //handler.sendEmptyMessageDelayed(1, 1000);

                        //Remove from callback by 'what'
                        //handler.removeMessages(1);

                        //With additional parameters
                        //Message m = handler.obtainMessage(int,object);
                        //handler.sendMessage(m);

                        Log.i("HASH", String.valueOf(handler.hashCode()));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void goToAsyncTask(View view) {
        Intent intent = new Intent(this, AsyncTaskActivity.class);
        startActivity(intent);
    }

    public void startService(View view) {
        Intent intent = new Intent(this, SimpleService.class);
        intent.putExtra("index", 1);
        startService(intent);
    }

    private int SERVICE_PENDING_INTENT_RESULT = 20001;
    public void startServiceResult(View view) {
        PendingIntent pi;
        Intent intent;

        // Создаем PendingIntent для Task1
        pi = createPendingResult(SERVICE_PENDING_INTENT_RESULT, new Intent(), 0);
        // Создаем Intent для вызова сервиса, кладем туда параметр времени
        // и созданный PendingIntent
        intent = new Intent(this, ServiceWithResult.class).putExtra("intent", pi);
        // стартуем сервис
        startService(intent);
    }

    private int RESULT = 10001;

    public void startActivityResult(View view) {
        Intent intent = new Intent(this, ResultActivity.class);
        super.startActivityForResult(intent, RESULT);
    }

    private class myHandler extends Handler {
        private WeakReference<MainActivity> mTarget;

        myHandler(MainActivity target) {
            mTarget = new WeakReference<MainActivity>(target);
        }

        public void setTarget(MainActivity target) {
            mTarget.clear();
            mTarget = new WeakReference<MainActivity>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mTarget.get();

            //msg.arg1
            //msg.arg2
            //msg.obj

            // обновляем TextView
            activity.tv.setText("Закачано файлов: " + msg.what);
        }
    }
}
