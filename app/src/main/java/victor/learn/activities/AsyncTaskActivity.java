package victor.learn.activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import victor.learn.R;

//doInBackground – будет выполнен в новом потоке, здесь решаем все свои тяжелые задачи. Т.к. поток не основной - не имеет доступа к UI.
//
//onPreExecute – выполняется перед doInBackground, имеет доступ к UI
//
//onPostExecute – выполняется после doInBackground (не срабатывает в случае, если AsyncTask был отменен), имеет доступ к UI
public class AsyncTaskActivity extends AppCompatActivity {

    TextView tvInfo;
    MyTask myTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_task);
        tvInfo = (TextView) findViewById(R.id.textView);
        myTask = (MyTask) getLastCustomNonConfigurationInstance();
        if (myTask != null) {
            myTask.setTarget(this);
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return myTask;
    }

    public void start(View view) {
        if (myTask.getStatus() == AsyncTask.Status.PENDING) {
            myTask.execute("text", "test", "figure");
        }

        if (myTask.getStatus() == AsyncTask.Status.FINISHED) {
            myTask = new MyTask();
            myTask.execute("text", "test", "figure");
        }
        //основной поток блокирован методом get. Метод get ждет завершения AsyncTask.
        //Integer res = myTask.get(); //will be 100 OR mt.get(1, TimeUnit.SECONDS); with timeout

        //Метод cancel возвращает boolean. Мы получим false, если задача уже завершена или отменена.
        //Если в метод cancel передать true, то он попытается сам остановить поток. И сгенерирует InterruptedException
        //Log.d(LOG_TAG, "cancel result: " + myTask.cancel(false));
    }


    //Официальный хелп дает 4 правила использования AsyncTask, я также укажу их здесь:
//
//- объект AsyncTask должен быть создан в UI-потоке
//
//- метод execute должен быть вызван в UI-потоке
//
//- не вызывайте напрямую методы onPreExecute, doInBackground, onPostExecute и onProgressUpdate (последний мы пока не проходили)
//
//- AsyncTask может быть запущен (execute) только один раз, иначе будет exception
    class MyTask extends AsyncTask<String, Integer, Integer> {

        AsyncTaskActivity activity;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            activity.tvInfo.setText("Begin");
        }

        public void setTarget(AsyncTaskActivity activity) {
            this.activity = activity;
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                int cnt = 0;
                for (String url : params) {
                    // загружаем файл
                    downloadFile(url);
                    // выводим промежуточные результаты
                    //вызываем метод publishProgress и передаем туда данные, срабатывает метод onProgressUpdate и получает эти данные
                    publishProgress(++cnt);
//Мы просто добавили проверку isCancelled. Если он возвращает true, то выходим (return).
                    if (isCancelled()) return null;
                }
                // разъединяемся
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //finished result f method get
            return 100;
        }

        private void downloadFile(String url) throws InterruptedException {
            TimeUnit.SECONDS.sleep(2);

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            activity.tvInfo.setText("Downloaded " + values[0] + " files");
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            activity.tvInfo.setText("End");
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            activity.tvInfo.setText("Cancel");
        }
    }
}
