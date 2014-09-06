package vsp.processing;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * A helper for handling processes.
 * @author adam
 */
public class ProcessHelper {

    /**
     * Starts the supplied process and gobbles up all of the output.
     * @param p the process to start.
     */
    public static void consumeOutput(final Process p) {
        // Build Runnable.
        Runnable ffmpegRunnable = new Runnable() {
            @Override
            public void run() {
                final InputStream procOutput = p.getInputStream();
                final InputStream procError = p.getErrorStream();

                final BufferedInputStream bufProcOutput = new BufferedInputStream(procOutput);
                final BufferedInputStream bufProcError = new BufferedInputStream(procError);

                final InputStreamReader procOutputReader = new InputStreamReader(bufProcOutput);
                final InputStreamReader procErrorReader = new InputStreamReader(bufProcError);

                final BufferedReader outputBufReader = new BufferedReader(procOutputReader);
                final BufferedReader errorBufReader = new BufferedReader(procErrorReader);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String line;
                            while((line = outputBufReader.readLine()) != null);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String line;
                            while((line = errorBufReader.readLine()) != null) {
                                System.out.println(line);
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }).start();
            }
        };
        //new Thread(ffmpegRunnable).start();
    }
}