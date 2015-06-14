package po;

import java.awt.Image;
import java.io.Serializable;

public class TeamPlayerImage implements Serializable{
       PlayerImage playerImage;
       String name;
       public TeamPlayerImage(Image image,String name)
       {
    	   this.playerImage = new PlayerImage(image);
    	   this.name = name;
       }
       /**
        *获得图片 
        * @return  图片
        */
       public Image getImage()
       {
    	   return playerImage.getImage();
    	  
       }
       /**
        * 获得名字
        * @return  名字
        */
       public String getName()
       {
    	   return name;
       }
       public String toString()
       {
    	   return name;
       }
}
