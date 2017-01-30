package victor.learn.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import victor.learn.R;
import victor.learn.services.ServiceWithResult;

public class ResultActivity extends AppCompatActivity {

    ServiceConnection sConn;
    Intent intent;
    ServiceWithResult myService;
    //Переменную bound мы используем для того, чтобы знать – подключены мы в данный момент к сервису или нет.
    boolean bound = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        intent = new Intent(this, ServiceWithResult.class);

        sConn = new ServiceConnection() {
            //При подключении к сервису сработает метод onServiceConnected
            public void onServiceConnected(ComponentName name, IBinder binder) {
                Log.d("LOG_TAG", "MainActivity onServiceConnected");
                myService = ((ServiceWithResult.MyBinder) binder).getService();
                myService.helloWorld();
                bound = true;
            }
//Метод onServiceDisconnected не сработает при явном отключении.
            //Только когда связь с сервисом потеряна (если сервис был убит системой при нехватке памяти)
            public void onServiceDisconnected(ComponentName name) {
                Log.d("LOG_TAG", "MainActivity onServiceDisconnected");
                bound = false;
            }
        };
    }

    //BIND_AUTO_CREATE, означающий, что, если сервис, к которому мы пытаемся подключиться, не работает, то он будет запущен.
    //Если не использовать флан BIND_AUTO_CREATE то потом Сервис создался и приложение подключилось к нему. Т.е. попыткой биндинга мы оставили некую «заявку» на подключение,
    // и когда сервис был запущен методом startService, он эту заявку увидел и принял подключение
    public void onClickBind(View v) {
        bindService(intent, sConn, BIND_AUTO_CREATE);
    }

    //Жмем UnBind. Если не был запущен во время бандинга
    //MyService onUnbind
    //Отключились от сервиса. Но сервис продолжает жить, потому что он был запущен не биндингом, а методом startService. А там уже свои правила закрытия сервиса. Это мы проходили в прошлых уроках.
    public void onClickUnBind(View v) {
        if (!bound) return;
        unbindService(sConn);
        bound = false;
    }

    public void setResult(View view) {
        setResult(RESULT_OK,new Intent().putExtra("name","activity result"));
    }


}
