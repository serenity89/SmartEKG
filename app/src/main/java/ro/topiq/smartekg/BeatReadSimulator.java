package ro.topiq.smartekg;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class BeatReadSimulator {

    private static final String LOG_TAG = BeatReadSimulator.class.getSimpleName();
    private ArrayList<Integer> m_nEcgSamples = new ArrayList<>();
    private int m_nIndex = -1;
    private int m_nSampleRate = -1;

    /*
     * load the file with recorded data
     * */
    public BeatReadSimulator ( String ekgFile ){
        InputStream inStream = null;
        String fullEkgFilePath = Environment.getExternalStoragePublicDirectory(
                                 Environment.DIRECTORY_DOWNLOADS) +
                                "/SmartEKG/" + ekgFile;
        Log.i(LOG_TAG, "Path to selected EKG file: " + fullEkgFilePath);

        try {
            inStream = new BufferedInputStream(new FileInputStream(fullEkgFilePath.substring(0, fullEkgFilePath.length()-1))); // o aberatie
            InputStreamReader isr = new InputStreamReader(inStream);

            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder strBuild = new StringBuilder();
            String lastVal = null;

            try {
                // read bitrate from file (the first line)
                m_nSampleRate = Integer.parseInt(bufferedReader.readLine());
                Log.i(LOG_TAG, "Bitrate: " + lastVal);

                while ((lastVal = bufferedReader.readLine()) != null) {
                    strBuild.append(lastVal);

                    // get value from EKG ekgFile
                    m_nEcgSamples.add(Integer.parseInt(lastVal));
                }
                m_nIndex = -1;
            } catch (IOException e) {
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
