/**
 * Copyright 2008 Cole Markham, all rights reserved.
 */
package net.ccmcomputing.avi;

import java.nio.channels.FileChannel;

/**
 * @author Cole Markham
 * 
 */
public class ListHandler{

   /**
    * @param channel
    * @param listPart
    * @throws Exception
    */
   public void parse(FileChannel channel, ListPart listPart) throws Exception{
      ListData listData = new ListData();
      listData.offset = listPart.offset + 12;
      listData.size = listPart.size;
      listPart.data.add(listData);
      channel.position(listData.offset + listPart.size - 4);
      return;
   }

}
