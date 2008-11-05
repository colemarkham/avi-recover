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
public class ListData extends Part{

   @Override
   public void read(FileChannel input, FileChannel output) throws Exception{
      ByteBuffer buffer = ByteBuffer.allocate(size - 4);
      input.position(offset);
      input.read(buffer);
      buffer.flip();
      output.write(buffer);
   }

}
