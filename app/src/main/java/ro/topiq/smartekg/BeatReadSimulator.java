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
    private int m_nSampleRate = -1;

    /*
     * load the file with recorded data
     * */
    public BeatReadSimulator ( String ekgFile ){
        InputStream inStr = null;
        try {
            inStr = new BufferedInputStream(
                    new FileInputStream(
                            Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_DOWNLOADS) +
                                    "/SmartEKG/" +
                                    ekgFile));
        InputStreamReader isr = new InputStreamReader(inStr);

        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder strBuild = new StringBuilder();
        String lastVal;

        try {
            // read bitrate from file (the first line)
            m_nSampleRate = Integer.parseInt(bufferedReader.readLine());
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
