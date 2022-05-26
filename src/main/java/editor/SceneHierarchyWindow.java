package editor;

import engine.GameObject;
import engine.Window;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.List;

public class SceneHierarchyWindow {

    private static String payloadDragDropType = "SceneHierarchy";

    public void imgui(){
        ImGui.begin("Scene Hierarchy");

        List<GameObject> gameObjects = Window.getScene().getGameObjects();

        int index = 0;
        for (GameObject go : gameObjects){
            if(!go.doSerialization()){
                continue;
            }

            boolean treeNodeOpen = doTreeNode(go, index);

            //Pop the tree node to avoid stack overflow errors
            if(treeNodeOpen){
                ImGui.treePop();
            }
            index++;
        }
        ImGui.end();
    }

    private boolean doTreeNode(GameObject go, int index){
        ImGui.pushID(index);
        boolean treeNodeOpen = ImGui.treeNodeEx(
                go.name,
                ImGuiTreeNodeFlags.DefaultOpen |
                        ImGuiTreeNodeFlags.FramePadding |
                        ImGuiTreeNodeFlags.OpenOnArrow |
                        ImGuiTreeNodeFlags.SpanAvailWidth,
                go.name
        );
        ImGui.popID();

        if (ImGui.beginDragDropSource()){
            ImGui.setDragDropPayload(payloadDragDropType,go);
            ImGui.text(go.name);

            ImGui.endDragDropSource();
        }

        if (ImGui.beginDragDropTarget()){
            Object payloadObj = ImGui.acceptDragDropPayload(payloadDragDropType);
            if (payloadObj != null){
                if (payloadObj.getClass().isAssignableFrom(GameObject.class)){
                    GameObject playerGameObj = (GameObject)payloadObj;
                }
            }

            ImGui.endDragDropTarget();
        }

        return treeNodeOpen;
    }
}
