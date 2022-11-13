package org.whitneyrobotics.ftc.teamcode.BetterTelemetry;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DigitalChannelImpl;

import org.opencv.core.Mat;
import org.whitneyrobotics.ftc.teamcode.framework.Matrix;
import org.whitneyrobotics.ftc.teamcode.framework.opmodes.OpModeEx;



@TeleOp(name = "LEDOp")
public class MAX7219Matrix extends OpModeEx {
    DigitalChannelImpl SPI_MOSI;
    DigitalChannelImpl SPI_CLK;
    DigitalChannelImpl SPI_CS;

    public enum Addresses {
        NOOP(0),
        C1(1),
        C2(2),
        C3(3),
        C4(4),
        C5(5),
        C6(6),
        C7(7),
        C8(8),
        INTENSITY(10),
        SCAN_LIMIT(11),
        SHUTDOWN(12),
        DISPLAY_TEST(15);

        private final int address;
        Addresses(int addr) { this.address = addr; }
        public int getAddress() { return address; }
    }

    public enum MatrixNumerals {
        SMILE(new int[]{ // Smiles :)
                0b00000000,
                0b00110000,
                0b01000100,
                0b01000000,
                0b01000000,
                0b01000100,
                0b00110000,
                0b00000000
        }),

        ONE(new int[]{
                0b00000000,
                0b00000000,
                0b00000000,
                0b00000000,
                0b00000000,
                0b11111111,
                0b00000000,
                0b00000000
        }),

        TWO(new int[]{
                0b00000000,
                0b00000000,
                0b11111001,
                0b10001001,
                0b10001001,
                0b10001111,
                0b00000000,
                0b00000000
        }),
        THREE(new int[]{
                0b00000000,
                0b00000000,
                0b10001001,
                0b10001001,
                0b10001001,
                0b11111111,
                0b00000000,
                0b00000000
        });

        public final int[] data;
        MatrixNumerals(int[] data){
            this.data = data;
        }
    }
    @Override
    public void initInternal() {
        SPI_MOSI = hardwareMap.get(DigitalChannelImpl.class, "matrixMOSI");
        SPI_CLK = hardwareMap.get(DigitalChannelImpl.class, "matrixCLK");
        SPI_CS = hardwareMap.get(DigitalChannelImpl.class, "matrixCS");

        SPI_MOSI.setMode(DigitalChannel.Mode.OUTPUT);
        SPI_CLK.setMode(DigitalChannel.Mode.OUTPUT);
        SPI_CS.setMode(DigitalChannel.Mode.OUTPUT);

        SPI_CS.setState(true);
        SPI_CLK.setState(false);
        SPI_MOSI.setState(true);

        setScanLimitRegister(7);
        startDisplay();
        setIntensity(0); // About medium brightness
        clearDisplay();
        setDisplay(MatrixNumerals.THREE.data);
    }

    public static int[] generateErrorStatus(int statusCode){
        return new int[] {
                0b00100100,
                0b00100100,
                0b00100100,
                0b00000000,
                0b00100100,
                0b00000000,
                statusCode,
                0b00000000,
        };
    }

    @Override
    public void loopInternal() {
        if (getRuntime() % 4 < 1) {
            setDisplay(MatrixNumerals.ONE.data);
        } else if (getRuntime() % 4 < 2) {
            setDisplay(MatrixNumerals.TWO.data);
        } else if (getRuntime() % 4 < 3) {
            setDisplay(MatrixNumerals.THREE.data);
        } else {
            setDisplay(generateErrorStatus(69));
        }
    }

    // Column data format
    // ---------------------------------------------------
    // Col N --> | D7 | D6 | D5 | D4 | D3 | D2 | D1 | D0 |
    // ---------------------------------------------------
    // For Col N between 1-8, the above will address each led in the column.
    // The Most Significant Bit (left-most) will correspond to the bottom-most
    // led in the column (Only when MOSI is connected to chip-side pins)
    // Ex. For column 1, 11110000 will light the bottom 4 LEDs of the 1st column
    public void setDisplay(int[] columns) {
        clearDisplay();
        for (int i=0; i<8; i++) {
            spiMAX7219Write(i+1, columns[i]);
        }

    }

    // The MAX7219 has an internal register that controls how many columns are actuall
    // "scanned" or read from. For example, if the register contains 00000011 (decimal 3),
    // even if columns 4-7 have data, they will not be displayed. Only columns 0-3 will be.
    private void setScanLimitRegister(int limit) {
        if (limit < 0 || limit > 7) { return; }
        spiMAX7219Write(Addresses.SCAN_LIMIT, limit);
    }

    // Control display brightness
    private void setIntensity(int intensity) {
        if (intensity < 0 || intensity > 15) { return; }

        spiMAX7219Write(Addresses.INTENSITY, intensity);

    }

    // The MAX7219 Starts in Shutdown mode
    private void startDisplay() {
        spiMAX7219Write(Addresses.SHUTDOWN, 0b00000001);
    }

    private void shutdownDisplay() {
        spiMAX7219Write(Addresses.SHUTDOWN, 0b00000000);
    }

    // The MAX7219 has two display modes, Display Test mode will turn on all LEDs
    // to test functional status
    private void setDisplayTest() {
        spiMAX7219Write(Addresses.DISPLAY_TEST, 0b00000001);
    }

    private void setDisplayNormal() {
        spiMAX7219Write(Addresses.DISPLAY_TEST, 0b00000000);
    }

    private void clearDisplay() {
        for (int i=0; i<8; i++) {
            spiMAX7219Write(i+1, 0b00000000);
        }
    }


    private void shiftOut(int data) {
        SPI_CLK.setState(false); // The MAX7219 samples data on the rising edge of the clock, so CPOL should be 0
        for (int i=7; i>=0; i--) {
            SPI_MOSI.setState(((data>>>i) & 1) == 1);
            SPI_CLK.setState(true);
            SPI_CLK.setState(false);
        }
    }

    // The MAX7219 takes a 16-bit shift register at a time. The lower nibble of the upper octet
    // contains the address of the shift register on the MAX7219 to put data in. The lower octet
    // contains the data to be put into the register. When we pull chip select back up to high, the
    // data is latched onto the MAX7219 for it to then read.

    private void spiMAX7219Write(Addresses address, int data) {
        SPI_CS.setState(false);

        shiftOut(address.getAddress());
        shiftOut(data);

        SPI_CS.setState(true);
    }

    private void spiMAX7219Write(int address, int data) {
        SPI_CS.setState(false);

        shiftOut(address);
        shiftOut(data);

        SPI_CS.setState(true);
    }
}
