package com.bask.appopengl;

import javax.microedition.khronos.egl.EGL10;            
import javax.microedition.khronos.egl.EGLConfig; 
import javax.microedition.khronos.opengles.GL10; 
 
import android.opengl.GLSurfaceView;                    
import java.lang.Math; 
 
class SquareRenderer implements GLSurfaceView.Renderer  
{ 
    public SquareRenderer(boolean useTranslucentBackground)  
    { 
        mTranslucentBackground = useTranslucentBackground; 
        mSquare = new Square();                                           
    } 
 
    public void onDrawFrame(GL10 gl)                                      
    { 
 
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);  
 
        gl.glMatrixMode(GL10.GL_MODELVIEW);                               
        gl.glLoadIdentity();                                              
        gl.glTranslatef(0.0f,(float)Math.sin(mTransY), -3.0f);            
 
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);                     
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY); 
 
        mSquare.draw(gl);                                                 
 
        mTransY += .075f;                                                 
    } 
 
    public void onSurfaceChanged(GL10 gl, int width, int height)     
    { 
           gl.glViewport(0, 0, width, height);        
 
           float ratio = (float) width / height; 
           gl.glMatrixMode(GL10.GL_PROJECTION);                           
           gl.glLoadIdentity(); 
           gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);                    
    } 
 
    public void onSurfaceCreated(GL10 gl, EGLConfig config)       
    { 
        gl.glDisable(GL10.GL_DITHER);                                     
 
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,                    
                 GL10.GL_FASTEST); 
 
             if (mTranslucentBackground)                                  
         { 
             gl.glClearColor(0,0,0,0);                         
         } 
             else  
         { 
                 gl.glClearColor(1,1,1,1); 
             } 
             gl.glEnable(GL10.GL_CULL_FACE);                  
             gl.glShadeModel(GL10.GL_SMOOTH);                 
             gl.glEnable(GL10.GL_DEPTH_TEST);                 
        } 
        private boolean mTranslucentBackground; 
        private Square mSquare; 
        private float mTransY; 
        private float mAngle; 
}    
