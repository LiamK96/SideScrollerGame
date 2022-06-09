package components;

import editor.PropertiesWindow;
import engine.MouseListener;
import scenes.Scene;


public class TranslateGizmo extends Gizmo{

    public TranslateGizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow){
        super(arrowSprite, propertiesWindow);
    }

    @Override
    public void editorUpdate(float dt){
        if (propertiesWindow.getActiveGameObject() != null){
            if (xAxisActive && !yAxisActive){

                propertiesWindow.getActiveGameObject().transform.position.x = MouseListener.getWorldX();
            } else if (yAxisActive){
                propertiesWindow.getActiveGameObject().transform.position.y = MouseListener.getWorldY();
            }
        }

        super.editorUpdate(dt);
    }

}
