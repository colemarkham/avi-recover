/**
 * Copyright 2008 Cole Markham, all rights reserved.
 */
package net.ccmcomputing.avi;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Cole Markham
 * 
 */
public class ListPart extends FilePart{

   protected String type;
   protected List<Part> data;

   public ListPart(){
      data = new ArrayList<Part>();
   }

   @Override
   public void read(FileChannel input, FileChannel output) throws Exception{
      // don't call super because the data might not be contiguous
      // just read the header
      ByteBuffer buffer = ByteBuffer.allocate(12);
      input.position(offset);
      FilePartFactory.read(input, buffer);
      output.write(buffer);
      // and delegate to the subparts
      for(Part part: data){
         part.read(input, output);
      }
   }

   @Override
   public String toString(){
      return "LIST ('" + type + "'" + " ...)@" + offset;
   }

}
