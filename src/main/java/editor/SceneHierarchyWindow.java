package editor;

import engine.GameObject;
import engine.Window;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.List;

public class SceneHierarchyWindow {

    public void imgui(){
        ImGui.begin("Scene Hierarchy");

        List<GameObject> gameObjects = Window.getScene().getGameObjects();

        int index = 0;
        for (GameObject go : gameObjects){
            if(!go.doSerialization()){
                continue;
            }

            ImGui.pushID(index);
            boolean treeNodeOpen = ImGui.treeNodeEx(
                    go.getName(),
                    ImGuiTreeNodeFlags.DefaultOpen |
                            ImGuiTreeNodeFlags.FramePadding |
                            ImGuiTreeNodeFlags.OpenOnArrow |
                            ImGuiTreeNodeFlags.SpanAvailWidth,
                    go.getName()
            );
            ImGui.popID();

            //Pop the tree node to avoid stack overflow errors
            if(treeNodeOpen){
                ImGui.treePop();
            }
            index++;
        }
        ImGui.end();
    }
}
