package engine;

import components.Component;
import editor.EImGui;
import org.joml.Vector2f;

public class Transform extends Component {

    public Vector2f position;
    public Vector2f scale;
    public float rotation;
    public int zIndex;

    public Transform() {
        this.init(new Vector2f(), new Vector2f());
    }

    public Transform(Vector2f position) {
        this.init(position, new Vector2f());
    }

    public Transform(Vector2f position, Vector2f scale) {
        this.init(position, scale);
    }

    public void init(Vector2f position, Vector2f scale){
        this.position = position;
        this.scale = scale;
        this.rotation = 0.0f;
        this.zIndex = 0;
    }

    public Transform copy() {
        return new Transform(new Vector2f(this.position), new Vector2f(this.scale));
    }

    public void copy(Transform to) {
        to.position.set(this.position);
        to.scale.set(this.scale);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Transform)) return false;

        Transform t = (Transform)o;
        return this.position.equals(t.position) && this.scale.equals(t.scale)
                && this.rotation == t.rotation && this.zIndex == t.zIndex;
    }

    @Override
    public void imgui() {
        gameObject.name = EImGui.inputText("Name: ", gameObject.name);
        EImGui.drawVec2Control("Position", this.position, 0.125f,150.0f, 0.01f);
        EImGui.drawVec2Control("Scale", this.scale, .250f, 150.0f, 0.01f);
        this.rotation = EImGui.dragFloat("Rotation", this.rotation);
        this.zIndex = EImGui.dragInt("Z-Index", this.zIndex);
    }
}
