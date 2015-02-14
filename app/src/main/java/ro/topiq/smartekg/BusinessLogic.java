package ro.topiq.smartekg;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.util.Vector;

import eplimited.osea.classification.ECGCODES;

public class BusinessLogic implements Runnable {
    EGKGraphicalView m_drawView = null;
    Context m_context = null;

    public BusinessLogic(EGKGraphicalView drawView, Context context) {
        m_drawView = drawView;
        m_context = context;
    }

    public void run() {
        BluetoothProxy proxy = new BluetoothProxy("RNBT-2632");
        boolean bResult = false;

        int nIterations = 0;

//        while (!(bResult = proxy.FindToEKGDevice())) {
//            nIterations++;
//
//            m_drawView.drawStatus(proxy.GetBluetoothStatus() + " (" + String.valueOf(nIterations) + "/10)");
//            m_drawView.postInvalidate();
//
//            SafeSleep(1000);
//
//            if (nIterations >= 10)
//                break; //aborting, switching to simulation
//        }

        if (!bResult) {
            m_drawView.drawStatus("Unable to get data via Bluetooth, going into Simulation mode...");
            m_drawView.postInvalidate();
            SafeSleep(3000);
            RunSimulation();
            return;
        }

        //getting data from device
        m_drawView.drawStatus(proxy.GetBluetoothStatus());
        m_drawView.postInvalidate();
        SafeSleep(3000);

        int nAbsolutIndex = 0;
        int nRetriesConnection = 1;

        while (nRetriesConnection <= 10) {
            if (!proxy.ConnectToEKGDevice()) {
                m_drawView.drawStatus("EKG Device is not online. Will try again in 2 seconds..." + String.valueOf(nRetriesConnection) + "/10)");
                m_drawView.postInvalidate();
                nRetriesConnection++;
                SafeSleep(2000);
                continue;
            }

            m_drawView.drawStatus("EKG Device online! Receiving data...");
            m_drawView.postInvalidate();
            SafeSleep(1000);

            while (true) {
                if (proxy.SendData(0x61)) {
                    SafeSleep(50);
                    int nCountValues = proxy.ReceiveData();
                    int nValue = 0;

                    if (nCountValues != -1) {
                        Vector<Integer> vectValues = proxy.GetReceivedSignalValues();

                        for (int i = 0; i < vectValues.size(); i++) {
//                            if((i > 0) && (vectValues.elementAt(i-1) > vectValues.elementAt(i))) {
//                                nValue = vectValues.elementAt(i) + vectValues.elementAt(i)/3;
//                            }
//                            if((i > 0) && (vectValues.elementAt(i-1) < vectValues.elementAt(i))) {
//                                nValue = vectValues.elementAt(i) - vectValues.elementAt(i)/3;
//                            }
                            nValue = vectValues.elementAt(i);
                            ProcessValue(nAbsolutIndex, nValue);
                            ++nAbsolutIndex;
                            SafeSleep(1000 / 50); //hardcoded
                        }
                    }
                } else {
                    SafeSleep(1000);
                }

            }
        }
    }

    private void RunSimulation() {
        int nValue = 0;

        BeatReadSimulator simulator = new BeatReadSimulator();
        HeartBeatClassifier.getInstance().setSampleRate(simulator.getSampleRate());

        m_drawView.drawStatus("Analysing heart beat signal, please wait...");

        for (int i = 0; i < 10 * simulator.getUniqueElementsSize(); i++) {
            nValue = simulator.getNextSample();

            ProcessValue(i, nValue);

            SafeSleep(1000 / simulator.getSampleRate());
        }

        System.out.println("All ok");
    }

// Usage:
//    AudioTrack tone = generateTone(440, 250);
//    tone.play();
//
    private AudioTrack generateTone(double freqHz, int durationMs) {
        int count = (int)(44100.0 * 2.0 * (durationMs / 1000.0)) & ~1;
        short[] samples = new short[count];
        for(int i = 0; i < count; i += 2){
            short sample = (short)(Math.sin(2 * Math.PI * i / (44100.0 / freqHz)) * 0x7FFF);
            samples[i + 0] = sample;
            samples[i + 1] = sample;
        }
        AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
                count * (Short.SIZE / 8), AudioTrack.MODE_STATIC);
        track.write(samples, 0, count);
        return track;
    }


    private void ProcessValue(int nIndex, int nValue) {
        m_drawView.drawSignal(nIndex, nValue);

        int code = HeartBeatClassifier.getInstance().DetectAndClassify(nValue);

        if (code != ECGCODES.NOTQRS) {
            if (code == ECGCODES.UNKNOWN) {
                m_drawView.drawStatus("A unknown beat type was detected!");
            } else if (code == ECGCODES.NORMAL) {
                m_drawView.drawStatus("A normal beat type was detected!");
            } else if (code == ECGCODES.PVC) {
                m_drawView.drawStatus("A premature ventricular contraction was detected!");
            }
        }

        if (HeartBeatClassifier.getInstance().HeartBeatDetected(nValue)) {
            //A QRS-Complex was detected! Let's play a beep

            try
            {
                AudioTrack tone = generateTone(280, 180);
                tone.play();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            m_drawView.drawDetectHeartBeat(nIndex);
        }


        //force refresh in order to redraw screen
        m_drawView.postInvalidate();
    }

    private void SafeSleep(int nMiliseconds) {
        try {
            Thread.sleep(nMiliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
