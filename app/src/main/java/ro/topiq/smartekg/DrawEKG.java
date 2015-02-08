package ro.topiq.smartekg;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;


public class DrawEKG extends Activity {

    private EGKGraphicalView m_drawView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_ekg);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        m_drawView = new EGKGraphicalView(this, metrics);
        setContentView(m_drawView);

        //Simulate heart beat signal
        new Thread(new BusinessLogic(m_drawView, this.getApplicationContext())).start();
    }

}
