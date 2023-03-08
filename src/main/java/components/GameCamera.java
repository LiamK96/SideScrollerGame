package components;

import engine.Camera;
import engine.GameObject;
import engine.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;
import physics2d.components.Box2DCollider;
import physics2d.components.RigidBody2D;
import physics2d.enums.BodyType;

import javax.management.monitor.GaugeMonitor;

public class GameCamera extends Component {

    private transient GameObject player;
    private transient GameObject border;
    private transient Camera gameCamera;
    private transient float highestX = Float.MIN_VALUE;
    private transient float undergroundYLevel = 0.0f;
    private transient float cameraBuffer = 1.5f;
    private transient float playerBuffer = 0.25f;

    private Vector4f skyColor = new Vector4f(92.0f / 255.0f, 148.0f / 255.0f, 252.0f / 255.0f, 1.0f);
    private Vector4f undergroundColor = new Vector4f(0,0,0,1);

    public GameCamera(Camera gameCamera) {
        this.gameCamera = gameCamera;

    }

    @Override
    public void start() {
        this.player = Window.getScene().getGameObjectWith(PlayerController.class);
        this.gameCamera.clearColor.set(skyColor);
        this.undergroundYLevel = this.gameCamera.position.y
                - this.gameCamera.getProjectionSize().y - this.cameraBuffer;

        this.border = new GameObject("border");
        RigidBody2D rb = new RigidBody2D();
        rb.setBodyType(BodyType.STATIC);
        rb.setMass(0.0f);
        rb.setFixedRotation(true);
        rb.setContinuousCollision(false);

        Box2DCollider boxCollider = new Box2DCollider();
        boxCollider.setHalfSize(new Vector2f(0.05f, 0.5f * 24.0f));
        boxCollider.setOffset(new Vector2f(-0.05f,0.0f));

        border.addComponent(rb);
        border.addComponent(boxCollider);
        border.setNoSerialize();
        border.addComponent(new GameBorder());
        Window.getScene().addGameObjectToScene(border);
        border.transform.position.set(0,0);

    }

    @Override
    public void update(float dt) {
        if (player != null && !player.getComponent(PlayerController.class).hasWon()) {
            gameCamera.position.x = Math.max(player.transform.position.x - 2.5f, highestX);
            border.transform.position.x = gameCamera.position.x;
            highestX = Math.max(highestX, gameCamera.position.x);

            if (!player.getComponent(PlayerController.class).isDead()) {
                if (player.transform.position.y < -playerBuffer) {
                    this.gameCamera.position.y = undergroundYLevel;
                    this.gameCamera.clearColor.set(undergroundColor);
                } else if (player.transform.position.y >= 0.0f) {
                    this.gameCamera.position.y = 0.0f;
                    this.gameCamera.clearColor.set(skyColor);
                }
            }
        }
    }

    public void resetHighestX() {
        highestX = Float.MIN_VALUE;
    }

}
