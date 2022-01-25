package renderer;

import util.AssetPool;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class DebugDraw {

    private static int MAX_LINES = 500;

    private static List<Line2D> lines = new ArrayList<>();
    //6 floats per vertex, 2 vertices per line;
    private static float[] vertexArray = new float[MAX_LINES * 6 * 2];
    private static Shader shader = AssetPool.getShader("assets/shaders/debugLine2D.glsl");

    private static int vaoID;
    private static int vboID;

    boolean started = false;

    public static void start(){
        //Generate VAO
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        //Create the VBO and bind some buffers
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER,vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);

        //Enable Vertex array attributes
        glVertexAttribPointer(0,3,GL_FLOAT, false, 6*Float.BYTES,0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1,3,GL_FLOAT,false,6*Float.BYTES,3*Float.BYTES);
        glEnableVertexAttribArray(1);

        // ToDO set line width


    }

    public static void beginFrame(){

    }

    public static void draw(){

    }

}
