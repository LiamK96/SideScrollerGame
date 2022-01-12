package renderer;

import components.SpriteRenderer;
import engine.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch implements Comparable<RenderBatch>{

    //Vertex
    //======================
    //Position              Color                       texCoords       texID
    // float, float         float,float,float,float     float,float     float
    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEX_COORDS_SIZE = 2;
    private final int TEX_ID_SIZE = 1;

    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;
    private final int VERTEX_SIZE = 9;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom; //sprites
    private float[] vertices;

    private List<Texture> textures;
    private int[] texSlots = {0,1,2,3,4,5,6,7};

    private int vaoID, vboID;
    private int maxBatchSize;
    private Shader shader;
    private int zIndex;

    public RenderBatch(int maxBatchSize, int zIndex){
        this.zIndex = zIndex;
        this.shader = AssetPool.getShader("assets/shaders/default.glsl");
        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        //4 Vertices quads
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.numSprites = 0;
        this.hasRoom = true;
        this.textures = new ArrayList<>();
    }

    public void start(){

        //Generate and bind a vertex array object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        //Allocate Space for the vertices;
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER,vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        //Create and upload the indices buffer
        int eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        //enable buffer attribute pointers
        glVertexAttribPointer(0,POS_SIZE,GL_FLOAT,false,VERTEX_SIZE_BYTES,POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1,COLOR_SIZE,GL_FLOAT,false,VERTEX_SIZE_BYTES,COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, TEX_COORDS_SIZE,GL_FLOAT,false,VERTEX_SIZE_BYTES,TEX_COORDS_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3,TEX_ID_SIZE,GL_FLOAT,false,VERTEX_SIZE_BYTES,TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);

    }

    public void addSprite(SpriteRenderer spr){
        //Get index and add renderObject
        int index = this.numSprites;
        this.sprites[index] = spr;
        this.numSprites++;

        if (spr.getTexture() != null){
            if (!textures.contains(spr.getTexture())){
                textures.add(spr.getTexture());
            }
        }

        //Add properties to local vertices array
        loadVertexProperties(index);

        if (numSprites >= this.maxBatchSize){
            this.hasRoom = false;
        }

    }

    public void render(){

        boolean rebufferData = false;
        for (int i =0; i<numSprites;i++){
            SpriteRenderer spr = sprites[i];
            if (spr.isDirty()){
                loadVertexProperties(i);
                spr.setClean();
                rebufferData = true;
            }
        }
        if (rebufferData){
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER,0,vertices);
        }


        //Use Shader
        shader.use();
        shader.uploadMat4f("uProjection", Window.getScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView",Window.getScene().getCamera().getViewMatrix());

        //Bind textures
        for (int i = 0; i<textures.size(); i++){
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }
        shader.uploadIntArray("uTextures", texSlots);

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);


        //unbind textures
        for (int i = 0; i<textures.size(); i++){
            textures.get(i).unbind();
        }
        shader.detach();

    }

    private void loadVertexProperties(int index){
        SpriteRenderer sprite = this.sprites[index];

        //Find offset in array (4 vertices per sprite)
        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();

        //Get tex coords
        Vector2f[] texCoords = sprite.getTexCoords();

        //Get tex id
        int texId = 0;
        if (sprite.getTexture() != null){                       //slot 0 reserved for no texture
            for (int i = 0; i<textures.size();i++){
                if (textures.get(i) == sprite.getTexture()){
                    texId = i + 1;
                    break;
                }
            }
        }


        //Add vertice with appropriate properties

        // *    *
        // *    *

        float xAdd = 1.0f, yAdd = 1.0f;
        for (int i = 0; i<4;i++){
            if (i == 1){
                yAdd = 0.0f;
            } else if (i == 2){
                xAdd = 0.0f;
            } else if (i == 3){
                yAdd = 1.0f;
            }

            //Load Position
            vertices[offset] = sprite.gameObject.transform.position.x + (xAdd * sprite.gameObject.transform.scale.x);
            vertices[offset + 1] = sprite.gameObject.transform.position.y + (yAdd * sprite.gameObject.transform.scale.y);

            //Load Color
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            //Load Texture coordinates
            vertices[offset + 6] = texCoords[i].x;
            vertices[offset + 7] = texCoords[i].y;

            //Load Texture ID
            vertices[offset + 8] = texId;

            offset += VERTEX_SIZE;

        }
    }

    private int[] generateIndices(){
        //6 Indices per quad (3 per triangle)
        int[] elements = new int[6 * maxBatchSize];
        for (int i = 0; i<maxBatchSize;i++){
            loadElementIndices(elements,i);
        }
        return elements;
    }

    private void loadElementIndices(int[] elements, int index){
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;
        // 3, 2, 0, 0, 2, 1         7, 6, 4, 4, 6, 5

        //Triangle1
        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset + 0;
        //Triangle 2
        elements[offsetArrayIndex + 3] = offset + 0;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }

    public boolean hasRoom(){
        return this.hasRoom;
    }

    public boolean hasRoomTexture(){
        return this.textures.size() < 8;
    }

    public boolean hasTexture(Texture texture){
        return this.textures.contains(texture);
    }

    public int zIndex(){
        return this.zIndex;
    }

    @Override
    public int compareTo(RenderBatch o) {
        return Integer.compare(this.zIndex, o.zIndex);
    }
}