package ro.topiq.smartekg;

import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class BeatReadSimulator {

    private ArrayList<Integer> m_nEcgSamples = new ArrayList<>();
    private int m_nIndex = -1;
    private int m_nSampleRate = 51; //HARDCODED! For simulation purpose

    /*
     * load the file with recorded data
     * */ {
        InputStream inStr = null;
        try {
            inStr = new BufferedInputStream(
                    new FileInputStream(
                            Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_DOWNLOADS) +
                                    "/SmartEKG" +
                                    "/sample.ekg"));
        InputStreamReader isr = new InputStreamReader(inStr);
//                InputStreamReader isr = new InputStreamReader(data);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder strBuild = new StringBuilder();
        String lastVal;

        try {
            while ((lastVal = bufferedReader.readLine()) != null) {
                strBuild.append(lastVal);

                // get value from file
                m_nEcgSamples.add(Integer.parseInt(lastVal));
            }
            m_nIndex = -1;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getNextSample() {
        m_nIndex++; //increment
        m_nIndex %= m_nEcgSamples.size(); //reset index if we are on the last element

        return m_nEcgSamples.get(m_nIndex);
    }

    public int getUniqueElementsSize() {
        return m_nEcgSamples.size();
    }

    public int getSampleRate() {
        return m_nSampleRate;
    }
}
