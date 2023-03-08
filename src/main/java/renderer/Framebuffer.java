package renderer;

import static org.lwjgl.opengl.GL30.*;

public class Framebuffer {

    private int fboId = 0;
    private Texture texture = null;

    public Framebuffer(int width, int height) {
        //Generate frameBuffer
        fboId = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboId);

        //create texture to render the data to and attach to framebuffer
        this.texture = new Texture(width, height);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,GL_TEXTURE_2D,this.texture.getTexID(),0);

        //create renderbuffer store the depth info
        int rboID = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER,rboID);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width, height);
        //attach to framebuffer
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboID);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            assert false : "Error(Framebuffer): GL framebuffer is not complete";
        }
        //unbinds current framebuffer
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER,fboId);
    }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER,0);
    }

    public int getFboId() {
        return fboId;
    }

    public int getTextureID() {
        return texture.getTexID();
    }

}
