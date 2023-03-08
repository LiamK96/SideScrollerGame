package components;

import editor.PropertiesWindow;
import engine.MouseListener;
import scenes.Scene;

public class ScaleGizmo extends Gizmo {

    public ScaleGizmo(Sprite scaleSprite, PropertiesWindow propertiesWindow) {
        super(scaleSprite, propertiesWindow);
    }

    @Override
    public void editorUpdate(float dt) {
        if (propertiesWindow.getActiveGameObject() != null) {
            if (xAxisActive && !yAxisActive){
                //todo fix scale gizmo mouse bug
                propertiesWindow.getActiveGameObject().transform.scale.x -= MouseListener.getWorldX();
            } else if (yAxisActive){
                propertiesWindow.getActiveGameObject().transform.scale.y -= MouseListener.getWorldY();
            }
        }

        super.editorUpdate(dt);
    }

}
