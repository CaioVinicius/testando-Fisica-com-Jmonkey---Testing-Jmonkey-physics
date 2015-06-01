/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author CaioVinicius
 */
public class PhysicsFallingBricks extends SimpleApplication implements PhysicsCollisionListener{
    
    private static final String SHOOT = "shoot";
    
    private BulletAppState bulletAppState;
    
    private Material brickMat;
    private Material ballMat;
    
    private static final Sphere ballMesh;
    private static final Box brickMesh;
    private static final Box floorMesh;
    private static Node wallNode;
    
    private RigidBodyControl brickPhy;
    private RigidBodyControl ballPhy;
    private RigidBodyControl floorPhy;
    
    private final static float BRICK_LENGHT = 0.4f;
    private final static float BRICK_WIDTH = 0.3f;
    private final static float BRICK_HEIGHT = 0.25f;
    private final static float WALL_WIDTH = 12;
    private final static float WALL_HEIGHT = 6;
     
    static{
        floorMesh = new Box(Vector3f.ZERO,10f,0.5f,5f);
        brickMesh = new Box(Vector3f.ZERO,BRICK_LENGHT,BRICK_HEIGHT,BRICK_WIDTH);
        ballMesh = new Sphere(32,32,0.25f,true,false);
        ballMesh.setTextureMode(Sphere.TextureMode.Projected);
        floorMesh.scaleTextureCoordinates(new Vector2f(4f,4f));
    } 
    
    public static void main(String[] args) {
        PhysicsFallingBricks app = new PhysicsFallingBricks();
        app.start();
    }
    @Override
    public void simpleInitApp() {
       
        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
//cria um plano como um chão para os blocos
        
         Material floorMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
         floorMat.setColor("Color", ColorRGBA.Green);
         
         brickMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
         brickMat.setColor("Color", ColorRGBA.Blue);
         
         ballMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
         ballMat.setColor("Color", ColorRGBA.Yellow);
         
         Geometry floorGeo = new Geometry("Floor", floorMesh);
         floorGeo.setMaterial(floorMat);
         floorGeo.move(0f,-BRICK_HEIGHT*2f,0f);
         rootNode.attachChild(floorGeo);
         floorPhy = new RigidBodyControl(0.0f);
         floorGeo.addControl(floorPhy);
         
         bulletAppState.getPhysicsSpace().add(floorPhy);
         
 //---------------------------------------------------------------------------- 
 //cria um muro de blocos        
         wallNode = new Node("wall");
         float offsetH = BRICK_LENGHT/3;
         float offsetV = 0;
         
         for(int j=0; j < WALL_HEIGHT;j++){
             for(int i =0; i< WALL_WIDTH;i++){
                 Vector3f brickpos = new Vector3f(offsetH + BRICK_LENGHT *2.1f*i - (BRICK_LENGHT * WALL_WIDTH), offsetV+BRICK_HEIGHT,0f);
                 wallNode.attachChild(makeBrick(brickpos));
             }
             offsetH = -offsetH;
             offsetV += 2 * BRICK_HEIGHT;
         }
         rootNode.attachChild(wallNode);
 //----------------------------------------------------------------------------
       inputManager.addMapping(SHOOT, new MouseButtonTrigger(mouseInput.BUTTON_LEFT));
       inputManager.addListener(actionListener, SHOOT);
       
       bulletAppState.getPhysicsSpace().addCollisionListener(this);
    }
    private ActionListener actionListener = new ActionListener() {

        public void onAction(String name, boolean isPressed, float tpf) {
            if(name.equals("shoot")&&!isPressed){
                shootCanonBall();
            }
        }
    };
    
public Geometry makeBrick(Vector3f loc){
    Geometry brickGeo = new Geometry("brick", brickMesh);
    brickGeo.setMaterial(brickMat);
    wallNode.attachChild(brickGeo);
    brickGeo.move(loc);
    
    brickPhy = new RigidBodyControl(5f);
    brickGeo.addControl(brickPhy);
    bulletAppState.getPhysicsSpace().add(brickPhy);
    
    brickPhy.setFriction(20f);
    
    return brickGeo;
}

public void shootCanonBall(){
    Geometry ballGeo = new Geometry("canon ball", ballMesh);
    ballGeo.setMaterial(ballMat);
    ballGeo.setLocalTranslation(cam.getLocation());
   
    ballPhy = new RigidBodyControl(5f);
   
    ballGeo.addControl(ballPhy);
    bulletAppState.getPhysicsSpace().add(ballPhy);
    rootNode.attachChild(ballGeo);
     
    ballPhy.setCcdSweptSphereRadius(.1f);
    ballPhy.setCcdMotionThreshold(0.001f);
    ballPhy.setLinearVelocity(cam.getDirection().mult(50));
    
}

    public void collision(PhysicsCollisionEvent event) {
        if("brick".equals(event.getNodeA().getName())||"brick".equals(event.getNodeB().getName())){
            if("canon ball".equals(event.getNodeA().getName())||"canon ball".equals(event.getNodeB().getName())){
                fpsText.setText("voce acertou uma caixa!!");
            }
        }
    }



}
