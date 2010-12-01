import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.LinkedList;

import javax.sound.sampled.*;

public class AudioSampleReader {
    
    private AudioInputStream audioInputStream;
    private AudioFormat format;
    
    LinkedList<byte[]> bufferList = new LinkedList<byte[]>();
    
    public AudioSampleReader(File file)
            throws UnsupportedAudioFileException, IOException {
    	//bb = new FileInputStream(file).getChannel().map(MapMode.READ_ONLY, 0, file.length());
    	//bb.load();
        audioInputStream = AudioSystem.getAudioInputStream(file);
        format = audioInputStream.getFormat();
    }

    private void copyFromBuffer(byte[] cp, long start) {
    	
    	
    }
    
    public AudioFormat getFormat() {
        return format;
    }
    

    public long getSampleCount() {
        long total = (audioInputStream.getFrameLength() *
            format.getFrameSize() * 8) / format.getSampleSizeInBits();
        return total / format.getChannels();
    }

    public void getInterleavedSamples(long begin, long end,
            double[] samples) throws IOException,
                IllegalArgumentException {
        long nbSamples = end - begin;

        long nbBytes = nbSamples * (format.getSampleSizeInBits() / 8) *
            format.getChannels();
        if (nbBytes > Integer.MAX_VALUE)
            throw new IllegalArgumentException("too many samples");

        byte[] inBuffer = new byte[(int)nbBytes];
        
        long bufstart = begin * (format.getSampleSizeInBits() / 8) * format.getChannels();
        long bufend = end * (format.getSampleSizeInBits() / 8) * format.getChannels();
        
        audioInputStream.read(inBuffer, 0, inBuffer.length);
        //System.err.println(bufstart);
        //bb.get(inBuffer, (int) bufstart, inBuffer.length);

        decodeBytes(inBuffer, samples);
    }
    
    public void getChannelSamples(int channel,
            double[] interleavedSamples, double[] channelSamples) {
        int nbChannels = format.getChannels();
        for (int i = 0; i < channelSamples.length; i++) {
            channelSamples[i] = interleavedSamples[nbChannels*i + channel];
        }
    }
    public void getStereoSamples(double[] leftSamples, double[] rightSamples)
            throws IOException {
        long sampleCount = getSampleCount();
        double[] interleavedSamples = new double[(int)sampleCount*2];
        getInterleavedSamples(0, sampleCount, interleavedSamples);
        for (int i = 0; i < leftSamples.length; i++) {
            leftSamples[i] = interleavedSamples[2*i];
            rightSamples[i] = interleavedSamples[2*i+1];
        }        
    }


    private void decodeBytes(byte[] audioBytes, double[] audioSamples) {
        int sampleSizeInBytes = format.getSampleSizeInBits() / 8;
        int[] sampleBytes = new int[sampleSizeInBytes];
        int k = 0; // index in audioBytes
        for (int i = 0; i < audioSamples.length; i++) {
            // collect sample byte in big-endian order
            if (format.isBigEndian()) {
                // bytes start with MSB
                for (int j = 0; j < sampleSizeInBytes; j++) {
                    sampleBytes[j] = audioBytes[k++];
                }
            } else {
                // bytes start with LSB
                for (int j = sampleSizeInBytes - 1; j >= 0; j--) {
                    sampleBytes[j] = audioBytes[k++];
                    if (sampleBytes[j] != 0)
                        j = j + 0;
                }
            }
            // get integer value from bytes
            int ival = 0;
            for (int j = 0; j < sampleSizeInBytes; j++) {
                ival += sampleBytes[j];
                if (j < sampleSizeInBytes - 1) ival <<= 8;
            }
            // decode value
            double ratio = Math.pow(2., format.getSampleSizeInBits() - 1);
            double val = ((double) ival) / ratio;
            audioSamples[i] = val;
        }
    }
}