/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usbplotter;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Robert Pyke
 */
public class USBInterface {

    private final InputStream inputStream;

    /**
     * Open a serial connection to the USB.
     * Create an input stream to read from the USB.
     * 
     * Baud rate: 57600
     * 
     * @param portName The name of the port you want to connect to.
     * @throws NoSuchPortException
     * @throws PortInUseException
     * @throws IOException
     * @throws InterruptedException
     * @throws UnsupportedCommOperationException 
     */
    public USBInterface(String portName) throws NoSuchPortException, PortInUseException, IOException, InterruptedException, UnsupportedCommOperationException {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
        SerialPort serialPort = (SerialPort) commPort;
        serialPort.setSerialPortParams(57600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

        this.inputStream = serialPort.getInputStream();
    }

    /**
     * Provides access to the input stream of the USB.
     * 
     * @return the input stream
     */
    public InputStream getInputStream() {
        return this.inputStream;
    }

    /**
     * Read the input stream as a string.
     * 
     * i.e. convert the bytes read to a String.
     * 
     * If no bytes were available on the input stream,
     * null is returned.
     * 
     * @return the contents of the input stream as a String
     * @throws IOException 
     */
    public String readAsString() throws IOException {
        String returnValue = null;

        byte[] buffer = new byte[1024];
        int len = this.inputStream.read(buffer);

        if (len > 0) {
            returnValue = new String(buffer, 0, len);
        }

        return returnValue;
    }

    /**
     * Read from the input stream, and try to interpret the contents
     * as a set of doubles, each separated by a space character.
     * 
     * This is very custom/clumsy. 
     * 
     * @TODO This will need to be resilient to
     * the split character being found between reads. i.e. the buffer will
     * need to exist across multiple stream reads
     * 
     * @return
     * @throws IOException 
     */
    public List<Double> readAsDoubleArray() throws IOException {
        List<Double> returnList = new ArrayList<>();
        String readString = readAsString();
        String[] split = readString.split("\\s");
        
        for (String valueAsStr : split) {

            if (valueAsStr.length() > 0) {
                Double valueAsDouble = new Double(valueAsStr);
                returnList.add(valueAsDouble);
            }
        }

        return returnList;
    }

    static void listPorts() {
        java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier portIdentifier = portEnum.nextElement();
            System.out.println(portIdentifier.getName() + " - " + getPortTypeName(portIdentifier.getPortType()));
        }
    }

    static String getPortTypeName(int portType) {
        switch (portType) {
            case CommPortIdentifier.PORT_I2C:
                return "I2C";
            case CommPortIdentifier.PORT_PARALLEL:
                return "Parallel";
            case CommPortIdentifier.PORT_RAW:
                return "Raw";
            case CommPortIdentifier.PORT_RS485:
                return "RS485";
            case CommPortIdentifier.PORT_SERIAL:
                return "Serial";
            default:
                return "unknown type";
        }
    }

    public static void main(String args[]) throws UnsupportedCommOperationException, NoSuchPortException, PortInUseException, IOException, InterruptedException {
        int count = 0;

        USBInterface myUSB = new USBInterface("/dev/ttyUSB0");

        while (true) {
            Thread.sleep(1000);
            List<Double> values = myUSB.readAsDoubleArray();

            for (Double val : values) {
                System.out.println(val.toString());
            }
        }
    }
}
