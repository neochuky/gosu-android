package gosu.java;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.microedition.khronos.opengles.GL10;

public class DrawOp implements Comparable<DrawOp>{
	
    // For sorting before drawing the queue.
    public int z;
    
    private RenderState renderState;
    // Only valid if renderState.texName != NO_TEXTURE
    public float top, left, bottom, right;
    
    // TODO: Merge with Gosu::ArrayVertex.
    public Vertex vertices[];
    
    // Number of vertices used, or: complement index of code block
    private int verticesOrBlockIndex;
    
    private GL10 gl;
    
    public DrawOp( GL10 gl_ ){
    	gl = gl_;
    	vertices = new Vertex[4];
    	renderState = new RenderState();
    }
    
    public RenderState getRenderState(){
    	return renderState;
    }
 
    public void setRenderState(RenderState renderState_ ){
    	renderState = renderState_;
    }   
    
    public int getVerticesOrBlockIndex(){
    	return verticesOrBlockIndex;
    }
    
    public void setVerticesOrBlockIndex( int verticesOrBlockIndex_ ){
    	verticesOrBlockIndex = verticesOrBlockIndex_;
    }
    
    public void perform( DrawOp nextOp){
    	IntBuffer colorBuffer = null;
    	FloatBuffer textureBuffer = null;
    	
        //TODO On Gosu even textures can have color, here every star on 
        //tutorial.rb is white
        if(verticesOrBlockIndex < 2 || verticesOrBlockIndex > 4){
        	throw new RuntimeException("Wrong verticesOrBlockIndex");
        }
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY); 
;

        float []index = new float[vertices.length*2];
        float []texture = new float[vertices.length*2];
        //Vertex index[] = new Vertex[vertices.length];      
        if(renderState.texName != ClipRect.NO_TEXTURE){
          gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
          
          int i = 0;
          int k = 0;
          for(int j = 0; j < vertices.length; j++){
	    	  index[k] = vertices[j].x;
	    	  index[k+1] = vertices[j].y;
	  
	    	  switch(i){
	    	  case 0:
	    		  texture[k] = left;
	    		  texture[k+1] = top;
	    		  break;
	    	  case 1:
	    		  texture[k] = right;
	    		  texture[k+1] = top;
	    		  break;
	    	  case 2:
	    		  texture[k] = left;
	    		  texture[k+1] = bottom;
	    		  break;
	    	  case 3:
	    		  texture[k] = right;
	    		  texture[k+1] = bottom;
	    		  i = -1;	  
	    	  }
	    	  k += 2; 
	    	  i++;
          }            
          
          ByteBuffer tbb = ByteBuffer.allocateDirect(texture.length*4);
          tbb.order(ByteOrder.nativeOrder());
          textureBuffer = tbb.asFloatBuffer();          
          textureBuffer.put(texture);
          textureBuffer.position(0); 
        }else{      
          gl.glEnableClientState(GL10.GL_COLOR_ARRAY); 
          int []color = new int[vertices.length*4];
          
          int k = 0, l = 0;
          for(int i = 0; i < vertices.length; i++){
        	  color[k] = vertices[i].c.getRed();
        	  color[k + 1] = vertices[i].c.getGreen();
        	  color[k + 2] = vertices[i].c.getBlue();
        	  color[k + 3] = vertices[i].c.getAlpha();
        	  
        	  index[l] = vertices[i].x;
        	  index[l + 1] = vertices[i].x;
        	  k += 4;
        	  l += 2;
          }
          
          ByteBuffer cbb = ByteBuffer.allocateDirect(color.length*4);
          cbb.order(ByteOrder.nativeOrder());
          colorBuffer = cbb.asIntBuffer();
          colorBuffer.put(color);
          colorBuffer.position(0);         
        }
               
      
        ByteBuffer vbb = ByteBuffer.allocateDirect(index.length*4);
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer vertex_buffer = vbb.asFloatBuffer();
        vertex_buffer.put(index);
        vertex_buffer.position(0);

        if(renderState.texName == ClipRect.NO_TEXTURE){
          gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);
        } 
        
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertex_buffer);
        
        if(renderState.texName != ClipRect.NO_TEXTURE){      
          gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);  
        }
              
        switch(verticesOrBlockIndex){
        case 2:
          gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, 2);  
          break;
        case 3:
          gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 3);
          break;
        case 4:
          //This draws a quad using two triangles
          gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
          break;
        }
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY); 
        if(renderState.texName == ClipRect.NO_TEXTURE){
          gl.glDisableClientState(GL10.GL_COLOR_ARRAY); 
        }else{
          gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);        
        }  
      }

	@Override
	public int compareTo(DrawOp another) {
		if(this.z < another.z)
		{
			return -1;
		}
		else if(this.z > another.z)
		{
			return 1;
		}
		return 0;		
	}    	
}
