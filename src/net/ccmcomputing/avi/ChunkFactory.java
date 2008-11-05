/**
 * Copyright 2008 Cole Markham, all rights reserved.
 */
package net.ccmcomputing.avi;

import java.nio.channels.FileChannel;

import net.ccmcomputing.avi.Chunk.ChunkType;

/**
 * @author Cole Markham
 * 
 */
public class ChunkFactory extends FilePartFactory{
   @Override
   protected FilePart create(){
      return new Chunk();
   }

   @Override
   public boolean isValid(String fourcc){
      try{
         Integer.parseInt(fourcc.substring(0, 2));
         ChunkType.lookup(fourcc.substring(2));
      }catch(Exception e){
         return false;
      }
      return true;
   }

   @Override
   public void parseData(FileChannel channel, FilePart part) throws Exception{
      Chunk chunk = (Chunk)part;
      chunk.streamNum = Integer.parseInt(chunk.fourcc.substring(0, 2));
      chunk.type = ChunkType.lookup(chunk.fourcc.substring(2));
      if(chunk.size % 2 != 0){
         // align to word boundary
         System.out.println("\tAligning to word boundary");
         chunk.size += 1;
      }
      // seek to end of data
      super.parseData(channel, part);
   }

}
