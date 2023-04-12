package GrainImplementation;


import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Scanner;

import static java.lang.management.ManagementFactory.getOperatingSystemMXBean;
import static java.lang.management.ManagementFactory.getPlatformMXBean;

public class Grain {

    // LFSR and NLFSR sizes in bits
    private static final int LFSR_SIZE = 80;
    private static final int NLFSR_SIZE = 80;

    private int  availableProcessors = getOperatingSystemMXBean().getAvailableProcessors();
    private long lastSystemTime      = 0;
    private double lastProcessCpuTime  = 0;

    // number of clock cycles for initialization - 160 cycles
    private static final int INIT_CYCLES = LFSR_SIZE + NLFSR_SIZE;

    // LFSR and NLFSR tap positions
    private static final int[] LFSR_TAPS = {79, 78, 76, 70};
    private static final int[] NLFSR_TAPS = {63, 62, 61, 60};

    // LFSR and NLFSR state arrays
    private int[] lfsr;
    private int[] nlfsr;

    public Grain(byte[] key, byte[] iv) {
        if (key.length != 10 || iv.length != 8) {
            System.out.println("key length------"+ key.length + " iv length ---"+ iv.length);
            throw new IllegalArgumentException("Invalid key or IV length.");
        }

        // initialize LFSR and NLFSR with key and IV
        this.lfsr = new int[LFSR_SIZE];
        this.nlfsr = new int[NLFSR_SIZE];

        int count = 0;
        for (int i = 0; i < LFSR_SIZE / 8; i++) {
            for (int j = 0; j < 8; j++) {
                int bit = (key[i] >> (7 - j)) & 0x01;
                this.lfsr[i * 8 + j] = bit;
            }
        }

        for (int i = 0; i < 64 / 8; i++) {
            for (int j = 0; j < 8; j++) {
                int bit = (iv[i] >> (7 - j)) & 0x01;
                nlfsr[i * 8 + j] = bit;
            }
        }
        for (int i = key.length * 8; i < LFSR_SIZE; i++) {
            lfsr[i] = 1;
        }

        for (int i = iv.length * 8; i < NLFSR_SIZE; i++) {
            nlfsr[i] = 1;
        }

        // perform initialization
        for (int i = 0; i < INIT_CYCLES; i++) {
            generateKeystreamByte();
        }
    }

    public byte[] encrypt(byte[] plaintext) {
        byte[] ciphertext = new byte[plaintext.length];
        for (int i = 0; i < plaintext.length; i++) {
            ciphertext[i] = (byte) (plaintext[i] ^ generateKeystreamByte());
        }
        return ciphertext;
    }

    public byte[] decrypt(byte[] ciphertext) {
        return encrypt(ciphertext); // decryption is the same as encryption
    }

    private int generateKeystreamByte() {
        // generate output bit
        int lfsrOutput = lfsr[0] ^ lfsr[13] ^ lfsr[23] ^ lfsr[38] ^ lfsr[51] ^ lfsr[62];
        int nlfsrOutput = nlfsr[0] ^ nlfsr[12] ^ nlfsr[25] ^ nlfsr[33] ^ nlfsr[46] ^ nlfsr[64]
                ^ (nlfsr[63] & nlfsr[60]) ^ (nlfsr[37] & nlfsr[33] & nlfsr[24]) ^ (lfsr[15] &
                lfsr[25]) ^ (lfsr[46] & lfsr[64]) ^ (nlfsr[63] & lfsr[64] & lfsr[79]);

        // shift LFSR and NLFSR
        System.arraycopy(lfsr, 1, lfsr, 0, LFSR_SIZE - 1);
        lfsr[LFSR_SIZE - 1] = lfsrOutput;

        System.arraycopy(nlfsr, 1, nlfsr, 0, NLFSR_SIZE - 1);
        nlfsr[NLFSR_SIZE - 1] = nlfsrOutput;

        // return output bit
        return lfsrOutput ^ nlfsrOutput;
    }

    public synchronized double getCpuUsage()
    {
        if ( lastSystemTime == 0 )
        {
            baselineCounters();

        }

        long systemTime     = System.nanoTime();
        double processCpuTime = 0;

        if ( getOperatingSystemMXBean() instanceof OperatingSystemMXBean )
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
        String plainText = input.next();

        long startTimeTh = System.currentTimeMillis();

        for(int k=0;k<numIterations;k++) {
        long startTime = System.nanoTime();
        byte[] key = new byte[] {0x01, 0x23, 0x45, 0x67, 0x79, 0x41, 0x14, 0x31, 0x14, 0x65};
        byte[] iv = new byte[] {0x65, 0x44, 0x65, 0x28, 0x76, 0x54, 0x32, 0x10};
        byte[] plaintext = plainText.getBytes();

        Grain grain = new Grain(key, iv);
        grain.baselineCounters();
        byte[] ciphertext = grain.encrypt(plaintext);

        com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean) getOperatingSystemMXBean();

        long physicalMemorySize = os.getTotalPhysicalMemorySize();
        long freePhysicalMemory = os.getFreePhysicalMemorySize();
        long freeSwapSize = os.getFreeSwapSpaceSize();
        long commitedVirtualMemorySize = os.getCommittedVirtualMemorySize();
        System.out.println("cipher bytes size " + ciphertext.length);
        System.out.println("Plaintext: " + new String(plaintext));
        System.out.println("Ciphertext: " + new String(ciphertext));
        System.out.println("CPU Utilization for the process --> " + os.getProcessCpuLoad());
        System.out.println("CPU Utilization for the system --> " + os.getSystemCpuLoad());
        System.out.println("Available Processors --> " + os.getAvailableProcessors());


        // memory usage
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        System.out.println("Memory usage by the algorithm -->  " + usedMemory + " bytes");

        //frequency


        // Execute some code here
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        double frequency = 1.0 / (elapsedTime / 1000000000.0);
        System.out.println("Frequency --> " + frequency + " Hz");
    }
    long endTime = System.currentTimeMillis(); // End time
    long totalTime = endTime - startTimeTh; // Total time taken in milliseconds
    double throughput = (double) numIterations / totalTime * 1000; // Throughput in operations per second
        System.out.println("Throughput: " + throughput + " ops/sec");
    }

}

