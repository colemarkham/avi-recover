/**
 * Copyright 2008 Cole Markham, all rights reserved.
 */
package net.ccmcomputing.avi;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author Cole Markham
 * 
 */
public class FilePart extends Part{
   protected String fourcc;

   @Override
   public void read(FileChannel input, FileChannel output) throws Exception{
      ByteBuffer buffer = ByteBuffer.allocate(size + 8);
      input.position(offset);
      FilePartFactory.read(input, buffer);
      output.write(buffer);
   }

   @Override
   public String toString(){
      return fourcc + "(" + size + "@" + offset + ")";
   }
}
