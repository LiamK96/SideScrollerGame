package renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

public class Texture {

    private String filepath;
    private int texID;


    public Texture(String filePath){
        this.filepath = filePath;

        //Generate texture on GPU
        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D,texID);

        //Set texture parameters
        //repeat image in both directions
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_REPEAT);

        //When stretching image, pixelate
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
        //When shrinking an image, pixelate
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);

        //Get RGB data from Image
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        ByteBuffer image = stbi_load(filePath,width,height,channels,0);

        if (image!= null){
            if (channels.get(0)==4) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0),
                        0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            } else if (channels.get(0)==3){
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0),
                        0, GL_RGB, GL_UNSIGNED_BYTE, image);
            } else {
                assert false : "Error (Texture): image channels is not compatible. Channels = "+channels.get(0) +
                        "\n Filepath: " + this.filepath;
            }
        } else {
            assert false : "Error (Texture): Could not load image: "+ filepath;
        }

        stbi_image_free(image);

    }

    public void bind(){
        glBindTexture(GL_TEXTURE_2D,texID);
    }

    public void unbind(){
        glBindTexture(GL_TEXTURE_2D, 0);
    }

}
