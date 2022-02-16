package components;

import engine.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderer.DebugDraw;
import util.Settings;

public class GridLines extends Component{

    @Override
    public void update(float dt){
        Vector2f cameraPos = Window.getScene().getCamera().position;
        Vector2f projectionSize = Window.getScene().getCamera().getProjectionSize();

        int firstX = ((int)(cameraPos.x / Settings.gridWidth) * Settings.gridWidth);
        int firstY = ((int)(cameraPos.y / Settings.gridHeight) * Settings.gridHeight);

        int numVerticalLines = (int)(projectionSize.x / Settings.gridWidth);
        int numHorizontalLine = (int)(projectionSize.y / Settings.gridHeight);

        int height = (int)projectionSize.y;
        int width = (int)projectionSize.x;

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
