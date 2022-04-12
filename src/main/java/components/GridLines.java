package components;

import engine.Camera;
import engine.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderer.DebugDraw;
import util.Settings;

public class GridLines extends Component{

    @Override
    public void update(float dt){
        Camera camera = Window.getScene().getCamera();
        Vector2f cameraPos = camera.position;
        Vector2f projectionSize = camera.getProjectionSize();

        int firstX = ((int)(cameraPos.x / Settings.gridWidth) * Settings.gridWidth);
        int firstY = ((int)(cameraPos.y / Settings.gridHeight) * Settings.gridHeight);

        int numVerticalLines = (int)(projectionSize.x * camera.getZoom() / Settings.gridWidth);
        int numHorizontalLine = (int)(projectionSize.y * camera.getZoom() / Settings.gridHeight);

        int height = (int) ((int)projectionSize.y * camera.getZoom());
        int width = (int) ((int)projectionSize.x * camera.getZoom());

        int maxLines = Math.max(numHorizontalLine,numVerticalLines);

        Vector3f color = new Vector3f(0.2f,0.2f,0.2f);
        for (int i = 0; i<maxLines; i++){
            int x = firstX + (Settings.gridWidth * i);
            int y = firstY + (Settings.gridHeight * i);
            if (i < numVerticalLines){
                DebugDraw.addLine2D(new Vector2f(x,firstY),new Vector2f(x,firstY+height),color);
            }
            if (i < numHorizontalLine){
                DebugDraw.addLine2D(new Vector2f(firstX,y), new Vector2f(firstX+width,y),color);
            }
        }

    }

}
