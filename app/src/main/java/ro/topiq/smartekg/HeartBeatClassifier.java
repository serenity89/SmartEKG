package ro.topiq.smartekg;

import eplimited.osea.OSEAFactory;
import eplimited.osea.classification.BeatDetectionAndClassification;
import eplimited.osea.classification.ECGCODES;
import eplimited.osea.classification.BeatDetectionAndClassification.BeatDetectAndClassifyResult;
import eplimited.osea.detection.QRSDetector2;

public class HeartBeatClassifier {
    static private HeartBeatClassifier m_instance = null;

    private int m_nSampleRate = -1;
    private QRSDetector2 m_qrsDetector = null;
    private BeatDetectionAndClassification m_bdac = null;

    private HeartBeatClassifier() {
        setSampleRate(50); //default sample rate
    }

    static public HeartBeatClassifier getInstance() {
        if (null == m_instance)
            m_instance = new HeartBeatClassifier();

        return m_instance;
    }

    public void setSampleRate(int nSampleRate) {
        m_nSampleRate = nSampleRate;
        m_qrsDetector = OSEAFactory.createQRSDetector2(nSampleRate);
        m_bdac = OSEAFactory.createBDAC(nSampleRate, nSampleRate / 2);
    }

    public int getSampleRate() {
        return m_nSampleRate;
    }

    public boolean HeartBeatDetected(int nSample) {
        if (null != m_qrsDetector)
            return 0 != m_qrsDetector.QRSDet(nSample);

        return false;
    }

    public int DetectAndClassify(int nSample) {
        BeatDetectAndClassifyResult result = m_bdac.BeatDetectAndClassify(nSample);
        if (result.samplesSinceRWaveIfSuccess != 0)
            return result.beatType;

        return ECGCODES.NOTQRS;
//		{
//            int qrsPosition =  i - result.samplesSinceRWaveIfSuccess;
//			if (result.beatType == ECGCODES.UNKNOWN)
//			{
//				stateText.setText("A unknown beat type was detected at sample: " + qrsPosition);
//			}
//			else if (result.beatType == ECGCODES.NORMAL)
//			{
//				stateText.setText("A normal beat type was detected at sample: " + qrsPosition);
//			}
//			else if (result.beatType == ECGCODES.PVC)
//			{
//				stateText.setText("A premature ventricular contraction was detected at sample: " + qrsPosition);
//			}
//		}

    }
}
