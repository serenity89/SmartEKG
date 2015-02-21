package ro.topiq.smartekg;

import java.util.Vector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.View;

public class EGKGraphicalView extends View
{

    Paint m_paint = new Paint();
    DisplayMetrics m_metrics = new DisplayMetrics();

    final int m_nDistance = 30; // HARDCODED - configurable
    final int m_nEraseOldShapeDistance = 30; // HARDCODED - configurable
    final int m_nMaxAmplitude = 5000;

    Vector<Integer> m_vectSignalDots = new Vector<Integer>();
    Vector<Integer> m_vectDetectedHeartBeats = new Vector<Integer>();

    String m_status = new String();

    public EGKGraphicalView(Context context, DisplayMetrics metrics) {

        super(context);

        m_metrics = metrics;
    }

    @Override
    public void onDraw(Canvas canvas) {
        setBackgroundColor(Color.GRAY);

        m_paint.setColor(Color.BLACK);
        m_paint.setStrokeWidth(2);
        canvas.drawRect(m_nDistance, m_nDistance, m_metrics.widthPixels - m_nDistance, m_metrics.heightPixels/2, m_paint);

        m_paint.setColor(Color.GRAY);
        m_paint.setStrokeWidth(1);


        canvas.drawLine(
                m_nDistance,
                m_metrics.heightPixels / 4 + m_nDistance / 2,
                m_metrics.widthPixels - m_nDistance,
                m_metrics.heightPixels / 4 + m_nDistance / 2,
                m_paint);


        for(int j = 0; j < m_vectDetectedHeartBeats.size(); j++) {
            try {
                canvas.drawLine(
                        m_nDistance + m_vectDetectedHeartBeats.elementAt(j),
                        m_nDistance,
                        m_nDistance + m_vectDetectedHeartBeats.elementAt(j),
                        m_metrics.heightPixels/2,
                        m_paint);

            }
            catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }


        // Draw the dots with the signal received
        m_paint.setColor(Color.GREEN);
        m_paint.setStrokeWidth(2);

        int nLastIndex = -1;
        int nLastValue = -1;

        for(int i = 0; i < m_vectSignalDots.size(); i++) {
            try {
                DrawSignalDot(canvas, nLastIndex, nLastValue, i, m_vectSignalDots.elementAt(i));

                nLastIndex = i;
                nLastValue = m_vectSignalDots.elementAt(i);

                // lowlight for old values
                if(nLastValue == -1) {
                    m_paint.setColor(Color.DKGRAY);
                    m_paint.setStrokeWidth(1);
                }
            }
            catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        if(m_status.length() != 0) {
            // text color
            m_paint.setColor(Color.BLUE);
            // text size
            m_paint.setTextSize(25);
            canvas.drawText(m_status, m_nDistance, m_metrics.heightPixels / 2 + m_nDistance, m_paint);
        }
    }

    public void DrawSignalDot(Canvas canvas, int nLastIndex, int nLastValue, int nIndex, int nValue) {
        if(nLastIndex != -1 && nLastValue != -1 && nIndex != -1 && nValue != -1) {
            int nComputedLastPositionX = m_nDistance + nLastIndex;
            int nComputedLastPositionY = m_metrics.heightPixels/2 - (nLastValue * ((m_metrics.heightPixels/2) - m_nDistance) / m_nMaxAmplitude);

            int nComputedPositionX = m_nDistance + nIndex;
            int nComputedPositionY = m_metrics.heightPixels/2 - (nValue * ((m_metrics.heightPixels/2) - m_nDistance) / m_nMaxAmplitude);

            if( nComputedPositionX > m_metrics.widthPixels ||
                nComputedPositionY > m_metrics.heightPixels ) {
                System.out.println("Drawing logical error!");
            }
            else {
//                System.out.println("Line: " + nComputedLastPositionX + ", " + nComputedLastPositionY + " - " +
//                        nComputedPositionX + ", " + nComputedPositionY);

                canvas.drawLine(
                        nComputedLastPositionX,
                        nComputedLastPositionY,
                        nComputedPositionX,
                        nComputedPositionY,
                        m_paint);
            }
        }
    }

    public void drawSignal(int nIndex, int nValue) {
        int nMaxNumberOfDotsVisible = m_metrics.widthPixels - 2 * m_nDistance;

        if(nIndex >= nMaxNumberOfDotsVisible)
        {
            m_vectSignalDots.remove(nIndex % nMaxNumberOfDotsVisible);
        }

        m_vectSignalDots.add(nIndex % nMaxNumberOfDotsVisible, nValue);

        if(nIndex >= nMaxNumberOfDotsVisible)
        {
            for(int i = 1; i < m_nEraseOldShapeDistance; i++)
            {
                m_vectSignalDots.remove((nIndex + i) % nMaxNumberOfDotsVisible);
                m_vectSignalDots.add((nIndex + i) % nMaxNumberOfDotsVisible, -1);
            }
        }

        //clean all visible heart beats (vertical lines)
        if(nIndex % nMaxNumberOfDotsVisible == 0)
            m_vectDetectedHeartBeats.clear();
    }

    public void drawStatus(String status)
    {
        m_status = status;
    }

    public void drawDetectHeartBeat(int nIndex) {
        int nMaxNumberOfDotsVisible = m_metrics.widthPixels - 2 * m_nDistance;
        m_vectDetectedHeartBeats.add(nIndex % nMaxNumberOfDotsVisible);
    }

}
