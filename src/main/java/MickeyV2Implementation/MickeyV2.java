package MickeyV2Implementation;

import java.lang.management.OperatingSystemMXBean;
import java.util.*;

import static java.lang.management.ManagementFactory.getOperatingSystemMXBean;

public class MickeyV2 {

    private int  availableProcessors = getOperatingSystemMXBean().getAvailableProcessors();
    private long lastSystemTime      = 0;
    private double lastProcessCpuTime  = 0;
    public static int xorr(int a, int b) { //xor operation
        if (a == b)
            return 0;
        else
            return 1;
    }

    public static int andd(int a, int b) { //and operation
        if (a == 1 && b == 1)
            return 1;
        else
            return 0;
    }

    public static int[] clock_r(int[] r, int input_bit_r, int control_bit_r) {
        int[] tap = {0, 1, 3, 4, 5, 6, 9, 12, 13, 16, 19, 20, 21, 22, 25, 28, 37,
                38, 41, 42, 45, 46, 50, 52, 54, 56, 58, 60, 61, 63, 64, 65, 66, 67,
                71, 72, 79, 80, 81, 82, 87, 88, 89, 90, 91, 92, 94, 95, 96, 97};
        int[] r_clocked = new int[100];
        int feedback_bit;
        int k = 0; //tap counter

        feedback_bit = xorr(r[99], input_bit_r);

        for (int i = 0; i < 100; i++) {
            if (i == 0)
                r_clocked[i] = 0;
            else
                r_clocked[i] = r[i - 1];

            if (k < tap.length && i == tap[k]) {
                r_clocked[i] = xorr(r_clocked[i], feedback_bit);
                k++;
            }

            if (control_bit_r == 1)
                r_clocked[i] = xorr(r_clocked[i], r[i]);
        }

        return r_clocked;
    }

    public static int[] clock_s(int[] s, int input_bit_s, int control_bit_s) {
        int comp0[] = {'\0', 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1,
                0, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1,
                1, 1, 1, 0, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, '\0'};
        int comp1[] = {'\0', 1, 0, 1, 1, 0, 0, 1, 0, 1, 1, 1, 1, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 0, 1, 0, 1,
                1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 1,
                1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1,
                1, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, '\0'};
        int fb0[] = {1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 1,
                1, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 1, 1, 0, 1, 1, 1,
                0, 0, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0};
        int fb1[] = {1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 1, 1,
                0, 0, 1, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1,
                0, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1,
                1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};
        int[] s_i = new int[100];
        int[] s_clocked = new int[100];
        int feedback_bit = xorr(s[99], input_bit_s);

        s_i[0] = 0;
        s_i[99] = s[98];

        for (int i = 1; i < 99; i++) {
            s_i[i] = xorr(s[i - 1], andd(xorr(s[i], comp0[i]), xorr(s[i + 1], comp1[i])));
        }

        if (control_bit_s == 0) {
            for (int i = 0; i < 100; i++) {
                s_clocked[i] = xorr(s_i[i], andd(fb0[i], feedback_bit));
            }
        } else {
            for (int i = 0; i < 100; i++) {
                s_clocked[i] = xorr(s_i[i], andd(fb1[i], feedback_bit));
            }
        }

        return s_clocked;
    }

    public static void clock_kg(int[] r, int[] s, int mixing, int input_bit) {
        int input_bit_r;
        int control_bit_r = xorr(s[34], r[67]);
        int control_bit_s = xorr(s[67], r[33]);
        if (mixing == 1)
            input_bit_r = xorr(input_bit, s[50]);
        else
            input_bit_r = input_bit;
        int input_bit_s = input_bit;
        int[] r_cl = clock_r(r, input_bit_r, control_bit_r);
        int[] s_cl = clock_s(s, input_bit_s, control_bit_s);
        System.arraycopy(r_cl, 0, r, 0, r.length);
        System.arraycopy(s_cl, 0, s, 0, s.length);
    }

    public static byte[] encrypt(byte[] plaintext, int[] keystreambits) {
        byte[] ciphertext = new byte[plaintext.length];
        for (int i = 0; i < plaintext.length; i++) {
            ciphertext[i] = (byte) (plaintext[i] ^ keystreambits[i]);
        }
        return ciphertext;
    }

    public synchronized double getCpuUsage()
    {
        if ( lastSystemTime == 0 )
        {
            baselineCounters();

        }

        long systemTime     = System.nanoTime();
        double processCpuTime = 0;

        if ( getOperatingSystemMXBean() instanceof OperatingSystemMXBean)
        {
            processCpuTime = ((com.sun.management.OperatingSystemMXBean) getOperatingSystemMXBean()).getProcessCpuLoad();
        }

        double cpuUsage = (double) ( processCpuTime - lastProcessCpuTime ) / ( systemTime - lastSystemTime );

        lastSystemTime     = systemTime;
        lastProcessCpuTime = processCpuTime;

        return cpuUsage / availableProcessors;
    }

    private void baselineCounters()
    {
        lastSystemTime = System.nanoTime();

        if ( getOperatingSystemMXBean() instanceof OperatingSystemMXBean )
        {
            lastProcessCpuTime = ((com.sun.management.OperatingSystemMXBean) getOperatingSystemMXBean()).getProcessCpuLoad();
        }
    }

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        // to calculate throughput, mention the number of iterations
        System.out.println("Enter number of iterations ");
        int numIterations = input.nextInt();

        System.out.println("Enter the plaintext ");
        String plaintext = input.next();

        byte[] ciphertext = new byte[plaintext.getBytes().length];

        long startTimeTh = System.currentTimeMillis();
        for(int k=0;k<numIterations;k++) {

            long startTime = System.nanoTime();
            int[] key = new int[80];
            int[] iv = new int[80];
            int[] z = new int[100];
            int[] r = new int[100];
            int[] s = new int[100];

            byte[] plainText = plaintext.getBytes();


            //System.out.println("Enter length of keystream to be generated:");
            int l = 80;

            //System.out.println("Enter the 80-bit key:");
            for (int j = 0; j < 80; j++) {
                key[j] = (int) (Math.random() * 2);
            }

            //System.out.println("Enter the value for iv (40-bit):");
            for (int j = 0; j < 80; j++) {
                iv[j] = (int) (Math.random() * 2);
            }

            System.out.println("--------- the key is");
            for (int j = 0; j < 80; j++) {
                System.out.print(key[j]);
            }
            System.out.println();

            // initializing register R and S value as 0
            for (int m = 0; m < 100; m++) {
                r[m] = 0;
                s[m] = 0;
            }

            for (int b = 0; b < 80; b++) { // loading in IV
                clock_kg(r, s, 1, iv[b]);
            }

            for (int n = 0; n < 80; n++) { // loading in key
                clock_kg(r, s, 1, key[n]);
            }

            for (int p = 0; p < 100; p++) { // prelock
                clock_kg(r, s, 1, 0);
            }

            for (int q = 0; q< l; q++) { // generating keystream
                z[q] = xorr(r[0], s[0]);
                clock_kg(r, s, 0, 0);
            }

            System.out.println("The generated keystream is:");
            for (int g = 0; g < l; g++) {
                System.out.print(z[g]);
            }
            System.out.println();

            ciphertext = encrypt(plainText, z);
            MickeyV2 ob = new MickeyV2();


        }
        long endTime = System.currentTimeMillis(); // End time
        long totalTime = endTime - startTimeTh; // Total time taken in milliseconds
        double throughput = (double) numIterations / totalTime * 1000; // Throughput in operations per second
        System.out.println("Throughput: " + throughput + " ops/sec");

        com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean) getOperatingSystemMXBean();

        long physicalMemorySize = os.getTotalPhysicalMemorySize();
        long freePhysicalMemory = os.getFreePhysicalMemorySize();
        long freeSwapSize = os.getFreeSwapSpaceSize();
        long commitedVirtualMemorySize = os.getCommittedVirtualMemorySize();
        System.out.println("Plaintext: " + new String(plaintext));
        System.out.println("Ciphertext: " + new String(ciphertext));
        System.out.println("CPU Utilization by the process --> " + os.getProcessCpuLoad());
        System.out.println("CPU Utilization by the system --> " + os.getSystemCpuLoad());
        System.out.println("Available Processors --> " + os.getAvailableProcessors());


        // memory usage
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        System.out.println("Memory usage --> " + usedMemory + " bytes");

        //frequency


        // Execute some code here
        long elapsedTime = endTime - startTimeTh;
        double frequency = (1.0 * numIterations)/ (elapsedTime / 1000000000.0);
        System.out.println("Frequency -->  " + frequency + " Hz");


    }
}