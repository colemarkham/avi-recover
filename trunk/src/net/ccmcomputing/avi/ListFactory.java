/**
 * Copyright 2008 Cole Markham, all rights reserved.
 */
package net.ccmcomputing.avi;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Cole Markham
 * 
 */
public class ListFactory extends FilePartFactory{
   private static final List<String> LIST_FOURCC = Arrays.asList("RIFF", "LIST");
   protected static Map<String, ListHandler> HANDLERS;
   static{
      HANDLERS = new HashMap<String, ListHandler>();
      HANDLERS.put("AVI ", new AVIHandler());
      HANDLERS.put("movi", new MoviHandler());
   }

   @Override
   protected ListPart create(){
      return new ListPart();
   }

   @Override
   public boolean isValid(String fourcc){
      return LIST_FOURCC.contains(fourcc);
   }

   @Override
   public void parseData(FileChannel channel, FilePart part) throws Exception{
      ListPart listPart = (ListPart)part;
      ByteBuffer buffer = ByteBuffer.allocate(4);
      listPart.type = readString(channel, buffer);
      ListHandler listHandler = HANDLERS.get(listPart.type);
      if(listHandler != null){
         listHandler.parse(channel, listPart);
      }else{
         new ListHandler().parse(channel, listPart);
      }
      // should already be at end of data, so don't call super
   }
}
