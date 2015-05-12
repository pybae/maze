package maze;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;

public class CrateEntity implements MazeEntity {

    public CrateEntity() {

    }
 
   public void renderObject(Vector3f loc, Node rootNode,
                             AssetManager assetManager,
                             PhysicsSpace physicsSpace) {
        Node crate = (Node) assetManager.loadModel("Models/3cratesOGAgpl/3cratesOGAgpl.j3o");

        crate.setLocalTranslation(loc);
        crate.setLocalScale(5.0f);
        
        CollisionShape shape = CollisionShapeFactory.createMeshShape(crate);
        RigidBodyControl control = new RigidBodyControl(shape);
        physicsSpace.add(control);
        crate.addControl(control);

        rootNode.attachChild(crate);
    }

    public void renderObject(Vector3f loc, Node rootNode, Node wallNode,
                             AssetManager assetManager,
                             PhysicsSpace physicsSpace,
                             boolean orientation) {
    }

    public float getWidth() {
        return 0.0f;
    }

    public float getLength() {
        return 0.0f;
    }

    public float getHeight() {
        return 0.0f;
    }
}
