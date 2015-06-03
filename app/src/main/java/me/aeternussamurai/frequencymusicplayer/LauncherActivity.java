package me.aeternussamurai.frequencymusicplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;


public class LauncherActivity extends Activity {

    private AnimationDrawable fmpIconAnimation;
    private boolean taskInBackGround = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        ImageView animatedImage = (ImageView) findViewById(R.id.fmp_icon_image_animation);
        animatedImage.setBackgroundResource(R.drawable.fmp_icon_animation);
        fmpIconAnimation = (AnimationDrawable) animatedImage.getBackground();

    }

    @Override
    public void onStart(){

        super.onStart();
    }

    @Override
    public void onResume(){
        //findViewById(R.id.fmp_icon_image_animation).setVisibility(View.VISIBLE);
        AsyncAnimationTask task = new AsyncAnimationTask();
        task.execute();
        AsyncStartTask startTask = new AsyncStartTask(this);
        startTask.execute();
//        while(fmpIconAnimation.isRunning() || taskRunning);
//        startActivity(new Intent(this, MainActivity.class));
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_launcher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class AsyncAnimationTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try{
                Thread.sleep(2000);
            }catch (Exception e){
                Log.e("ERROR", "Something happened");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            findViewById(R.id.fmp_icon_image_animation).setVisibility(View.VISIBLE);
            fmpIconAnimation.start();
            taskInBackGround = true;
        }
    }

    private class AsyncStartTask extends AsyncTask<Void, Void, Void>{

        private Context context;

        public AsyncStartTask(Context c){
            context = c;
        }

        @Override
        protected Void doInBackground(Void... params) {
            //while(fmpIconAnimation.isRunning() || !taskInBackGround);
            try {
                Thread.sleep(2000 + fmpIconAnimation.getNumberOfFrames()*125);
            }catch(Exception e){

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            startActivity(new Intent(context, MainActivity.class));
            finish();
        }
    }


}
