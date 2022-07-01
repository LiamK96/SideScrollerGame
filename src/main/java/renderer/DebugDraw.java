package renderer;

import engine.Camera;
import engine.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.AssetPool;
import util.JMath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class DebugDraw {

    private static final int MAX_LINES = 3000;

    private static List<Line2D> lines = new ArrayList<>();
    //6 floats per vertex, 2 vertices per line;
    private static float[] vertexArray = new float[MAX_LINES * 6 * 2];
    private static Shader shader = AssetPool.getShader("assets/shaders/debugLine2D.glsl");

    private static int vaoID;
    private static int vboID;

    private static boolean started = false;

    public static void start(){
        //Generate VAO
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        //Create the VBO and bind some buffers
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER,vboID);
        glBufferData(GL_ARRAY_BUFFER, (long)vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);

        //Enable Vertex array attributes
        glVertexAttribPointer(0,3,GL_FLOAT, false, 6*Float.BYTES,0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1,3,GL_FLOAT,false,6*Float.BYTES,3*Float.BYTES);
        glEnableVertexAttribArray(1);

        glLineWidth(2.0f);


    }

    public static void beginFrame(){
        if (!started){
            start();
            started = true;
        }
        //Remove dead lines **don't use for each because we are removing as we are incrementing**
        for (int i = 0; i<lines.size(); i++){
            if (lines.get(i).beginFrame() < 0){
                lines.remove(i);
                i--;
            }
        }
    }

    public static void draw(){
        if (lines.size() <= 0){
            return;
        }
        int index = 0;
        for (Line2D line : lines){
            for (int i = 0; i<2; i++){
                Vector2f position = (i==0)? line.getFrom() : line.getTo();
                Vector3f color = line.getColor();

                //load position into vertex array;
                vertexArray[index] = position.x;
                vertexArray[index + 1] = position.y;
                vertexArray[index + 2] = -10.0f;

                //Load color
                vertexArray[index + 3] = color.x;
                vertexArray[index + 4] = color.y;
                vertexArray[index + 5] = color.z;
                index += 6;
            }
        }
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, lines.size() * 6 * 2));

        //Use our shader
        shader.use();
        shader.uploadMat4f("uProjection", Window.getScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().getCamera().getViewMatrix());

        //Bind VAO
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        //Draw the batch        //Bressenhams line algorithm
        glDrawArrays(GL_LINES,0,lines.size() * 6 * 2);

        //Disable location
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        //Unbind shader
        shader.detach();
    }

    /////////////////////
    // Draw 2d line    //
    /////////////////////
    public static void addLine2D(Vector2f from, Vector2f to){
        addLine2D(from, to, new Vector3f(0,1,0), 1);
    }

    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color){
        addLine2D(from, to, color, 1);
    }

    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color, int lifetime){
        Camera camera = Window.getScene().getCamera();
        Vector2f cameraLeft = new Vector2f(camera.position).add(new Vector2f(-2.0f,-2.0f));
        Vector2f cameraRight = new Vector2f(camera.position)
                .add(new Vector2f(camera.getProjectionSize()).mul(camera.getZoom()))
                .add(new Vector2f(4.0f,4.0f));
        boolean lineInView = ((from.x >= cameraLeft.x && from.x <= cameraRight.x)
                && (from.y >= cameraLeft.y && from.y <= cameraRight.y))
                || ((to.x >= cameraLeft.x && to.x <= cameraRight.x)
                && (to.y >= cameraLeft.y && to.y <= cameraRight.y));

        if (lines.size() >= MAX_LINES || !lineInView){
            return;
        }
        DebugDraw.lines.add(new Line2D(from, to, color, lifetime));
    }

    /////////////////////
    // Draw 2d Box     //
    /////////////////////
    public static void addBox2D(Vector2f center, Vector2f dimensions){
        addBox2D(center, dimensions, 0, new Vector3f(0,0,1), 1);
    }

    public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation){

        addBox2D(center, dimensions, rotation, new Vector3f(0,0,1), 1);
    }

    public static void addBox2D(Vector2f center, Vector2f dimensions, Vector3f color){

        addBox2D(center, dimensions,0, color, 1);
    }

    public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation, Vector3f color){

        addBox2D(center, dimensions, rotation, color, 1);
    }

    public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation, Vector3f color, int lifetime){
        Vector2f min = new Vector2f(center).sub(new Vector2f(dimensions).div(2.0f));
        Vector2f max = new Vector2f(center).add(new Vector2f(dimensions).div(2.0f));

        Vector2f[] vertices= {
                new Vector2f(max.x,max.y), new Vector2f(max.x,min.y),
                new Vector2f(min.x,min.y), new Vector2f(min.x,max.y)

        };

        if (rotation != 0.0){
            for (Vector2f vec : vertices){
                JMath.rotate(vec, rotation, center);
            }
        }

        addLine2D(vertices[0], vertices[1],color, lifetime);
        addLine2D(vertices[1], vertices[2],color, lifetime);
        addLine2D(vertices[2], vertices[3],color, lifetime);
        addLine2D(vertices[3], vertices[0],color, lifetime);

    }

    /////////////////////
    // Draw 2d Circle  //
    /////////////////////
    public static void addCircle(Vector2f center, float radius){
        addCircle(center, radius, new Vector3f(0,1,0), 1);
    }

    public static void addCircle(Vector2f center,float radius,Vector3f color){

        addCircle(center, radius, color, 1);
    }

    public static void addCircle(Vector2f center, float radius, Vector3f color, int lifetime){
        Vector2f[] points = new Vector2f[20];
        int increment = 360 / points.length;
        int currentAngle = 0;

        for (int i = 0; i < points.length; i++){
            Vector2f temp = new Vector2f(0, radius);
            JMath.rotate(temp, currentAngle, new Vector2f());
            points[i] = new Vector2f(temp).add(center);

            if (i > 0){
                addLine2D(points[i-1], points[i], color, lifetime);
            }
            currentAngle += increment;
        }
        addLine2D(points[points.length-1], points[0], color, lifetime);
    }
}
