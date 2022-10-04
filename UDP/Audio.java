package UDP;

import java.io.InputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
/**
 *
 * @author Darkassesine
 */
public class Audio
{
    private AudioFormat audioFormat;
    private DataLine.Info inputDataLineInfo;
    private DataLine.Info outputDataLineInfo;
    private TargetDataLine targetDataLine;
    private SourceDataLine sourceDataLine;

    public Audio()
    {
        audioFormat = new AudioFormat(16000, 8, 2, true, true);
        inputDataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
        outputDataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
        
        if (!AudioSystem.isLineSupported(inputDataLineInfo))
        {
            System.err.println("Hubo un problema al intentar obtener el audio del micrófono");
        }
        try
        {
            targetDataLine = (TargetDataLine) AudioSystem.getLine(inputDataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();
            
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(outputDataLineInfo);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();
        }
        catch (LineUnavailableException ex)
        {
            System.err.println("Hubo un problema al intentar obtener el audio del micrófono");
        }
    }

    public TargetDataLine getTargetDataLine()
    {
        return targetDataLine;
    }

    public SourceDataLine getSourceDataLine()
    {
        return sourceDataLine;
    }
}
