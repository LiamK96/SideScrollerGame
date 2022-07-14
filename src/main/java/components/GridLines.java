package components;

import engine.Camera;
import engine.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderer.DebugDraw;
import util.Settings;

public class GridLines extends Component{

    @Override
    public void editorUpdate(float dt){
        Camera camera = Window.getScene().getCamera();
        Vector2f cameraPos = camera.position;
        Vector2f projectionSize = camera.getProjectionSize();

        float firstX = ((cameraPos.x / Settings.gridWidth) * Settings.gridWidth);
        float firstY = ((cameraPos.y / Settings.gridHeight) * Settings.gridHeight);

        int numVerticalLines = (int)(projectionSize.x * camera.getZoom() / Settings.gridWidth)+1;
        int numHorizontalLine = (int)(projectionSize.y * camera.getZoom() / Settings.gridHeight)+1;

        float height = ((int)projectionSize.y * camera.getZoom());
        float width =  ((int)projectionSize.x * camera.getZoom());

        int maxLines = Math.max(numHorizontalLine,numVerticalLines);

        Vector3f color = new Vector3f(0.2f,0.2f,0.2f);
        for (int i = 0; i<maxLines; i++){
            float x = firstX + (Settings.gridWidth * i);
            float y = firstY + (Settings.gridHeight * i);
            if (i < numVerticalLines){
                DebugDraw.addLine2D(new Vector2f(x,firstY),new Vector2f(x,firstY+height),color);
            }
            if (i < numHorizontalLine){
                DebugDraw.addLine2D(new Vector2f(firstX,y), new Vector2f(firstX+width,y),color);
            }
        }

    }

}
