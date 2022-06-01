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
        if (Scene.getActiveGameObject() != null){
            if (xAxisActive && !yAxisActive){

                Scene.getActiveGameObject().transform.position.x = MouseListener.getWorldX();
            } else if (yAxisActive){
                Scene.getActiveGameObject().transform.position.y = MouseListener.getWorldY();
            }
        }

        super.editorUpdate(dt);
    }

}
