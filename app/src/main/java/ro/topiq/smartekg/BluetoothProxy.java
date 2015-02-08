package ro.topiq.smartekg;

import android.bluetooth.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

/**
 * Created by Eliza on 02.02.2015.
 */
public class BluetoothProxy {
    private BluetoothAdapter m_bluetoothAdapter = null;
    private BluetoothSocket m_BSocket = null;
    private BluetoothDevice m_BDevice = null;

    private String m_sDeviceName = new String();
    private String m_sBluetoothStatus = new String();

    private OutputStream m_OutputStream = null;
    private InputStream m_InputStream = null;

    Vector<Integer> m_vectSignalValues = new Vector<Integer>();

    public BluetoothProxy(String sDeviceName) {
        m_sDeviceName = sDeviceName;
    }

    public Vector<Integer> GetReceivedSignalValues() {
        return m_vectSignalValues;
    }

    boolean FindToEKGDevice() {
        if (null == m_bluetoothAdapter)
            m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        try {

            if (m_bluetoothAdapter == null) {
                m_sBluetoothStatus = "Bluetooth NOT available! Please enable Bluetooth...";
            } else {
                if (m_bluetoothAdapter.isEnabled()) {
                    if (m_bluetoothAdapter.isDiscovering()) {
                        m_sBluetoothStatus = "Bluetooth is currently in device discovery process...";
                    } else {

                        Set<BluetoothDevice> pairedDevices = m_bluetoothAdapter.getBondedDevices();
                        // If there are paired devices
                        if (pairedDevices.size() > 0) {
                            // Loop through paired devices
                            for (BluetoothDevice device : pairedDevices) {
                                // Add the name and address to an array adapter to show in a ListView
                                if (device.getName().equals(m_sDeviceName)) {
                                    m_BDevice = device;
                                    break;
                                }
                            }
                        }

                        if (null != m_BDevice)
                            m_sBluetoothStatus = "Bluetooth Adapter Found: " + m_sDeviceName;
                        else
                            m_sBluetoothStatus = "Bluetooth is Enabled, but Device " + m_sDeviceName + " is not paired!";
                    }
                } else {
                    m_sBluetoothStatus = "Bluetooth is NOT Enabled! Please enable Bluetooth...";
                }
            }
        } catch (Exception ex) {
            m_sBluetoothStatus = "Error: " + ex.getMessage();
        }

        return null != m_BDevice;
    }

    boolean ConnectToEKGDevice() {
        if (null != m_BDevice) {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
            try {
                m_BSocket = m_BDevice.createRfcommSocketToServiceRecord(uuid);

                if (m_BSocket != null) {
                    try {
                        m_BSocket.connect();

                        try {
                            m_OutputStream = m_BSocket.getOutputStream();
                            m_InputStream = m_BSocket.getInputStream();
                            return true;

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public String GetBluetoothStatus() {
        return m_sBluetoothStatus;
    }

    public boolean SendData(int nValue) {
        boolean bResult = true;

        try {
            m_OutputStream.write(nValue);
        } catch (IOException e) {
            e.printStackTrace();
            bResult = false;
        }

        return bResult;
    }

    public int ReceiveData() {
        m_vectSignalValues.clear();
        byte[] ReceivedBytes = null; // release old data

        boolean bResult = true;
        final byte delimiter = '#'; //This is the ASCII code for a newline character

        byte[] readBuffer = new byte[10];
        int readBufferPosition = 0;

        int bytesAvailable = 0;

        try {
            bytesAvailable = m_InputStream.available();

            if (bytesAvailable > 0) {
                ReceivedBytes = new byte[bytesAvailable];
                m_InputStream.read(ReceivedBytes);

                for (int i = 0; i < bytesAvailable && bResult; i++) {
                    byte b = ReceivedBytes[i];

                    if (b == delimiter) {
                        if (readBufferPosition >= 3) {
                            byte[] encodedBytes = new byte[readBufferPosition - 2];
                            System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                            final String data = new String(encodedBytes, "US-ASCII");
                            readBufferPosition = 0;
                            m_vectSignalValues.add(Integer.decode(data));
                        } else {
                            bResult = false;
                            break;
                        }
                    } else {
                        readBuffer[readBufferPosition++] = b;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            bResult = false;
        } catch (Exception e) {
            e.printStackTrace();
            bResult = false;
        }

        if (bResult) {
            return m_vectSignalValues.size();
        } else {
            return -1;
        }
    }

}
