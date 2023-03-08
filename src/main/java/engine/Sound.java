package engine;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.libc.LibCStdlib.free;

public class Sound {

    private int bufferId, sourceId;
    private String filepath;

    private boolean isPlaying = false;

    public Sound(String filepath, boolean loops) {
        this.filepath = filepath;

        //Allocate space to store return information from stb
        stackPush();
        IntBuffer channelsBuffer = stackMallocInt(1);
        stackPush();
        IntBuffer sampleRateBuffer = stackMallocInt(1);

        ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(filepath,channelsBuffer,sampleRateBuffer);

        //check is succeeded
        if (rawAudioBuffer == null) {
            System.out.println("Could not load sound: "+ filepath);
            stackPop();
            stackPop();
            return;
        }
        //retrieve extra information stored in the buffers
        int channels = channelsBuffer.get();
        int sampleRate = sampleRateBuffer.get();
        //Free memory
        stackPop();
        stackPop();

        //find correct OpenAl format
        int format = -1;
        if (channels == 1){
            format = AL_FORMAT_MONO16;
        } else if (channels == 2){
            format = AL_FORMAT_STEREO16;
        } else {
            System.out.println("Unsupported format for sound: "+filepath+" with channel size of "+channels);
            return;
        }

        bufferId = alGenBuffers();
        alBufferData(bufferId, format, rawAudioBuffer, sampleRate);

        //generate the source
        sourceId = alGenSources();

        alSourcei(sourceId, AL_BUFFER, bufferId);
        alSourcei(sourceId, AL_LOOPING, loops ? AL_TRUE : AL_FALSE);
        alSourcei(sourceId, AL_POSITION, 0);
        alSourcef(sourceId, AL_GAIN, 0.3f);

        //free stb raw and audio data
        free(rawAudioBuffer);
    }

    public void delete() {
        alDeleteBuffers(bufferId);
        alDeleteSources(sourceId);
    }
    //TODO: no NullPointerException for play
    public void play() {
        int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
        if (state == AL_STOPPED) {
            isPlaying = false;
            alSourcei(sourceId, AL_POSITION, 0);
        }

        if (!isPlaying) {
            alSourcePlay(sourceId);
            isPlaying = true;
        }
    }    public void playWithOverlap() {
            alSourcePlay(sourceId);
            isPlaying = true;
    }

    //TODO: clear NullPointerException
    public void stop() {
        if (this.isPlaying) {
            alSourceStop(sourceId);
            isPlaying = false;
        }
    }

    public String getFilepath() {
        return this.filepath;
    }

    //TODO: cleat NullPointerException
    public boolean isPlaying() {
        int state = alGetSourcei(sourceId,AL_SOURCE_STATE);
        if (state == AL_STOPPED) {
            isPlaying = false;
        }
        return isPlaying;
    }

}
