/**
 * Copyright 2008 Cole Markham, all rights reserved.
 */
package net.ccmcomputing.avi;

import java.nio.channels.FileChannel;

/**
 * @author Cole Markham
 * 
 */
public class AVIHandler extends ListHandler{
   private void readListPart(FileChannel channel, ListPart listPart, String fourcc) throws Exception{
      FilePart part = RIFFParser.parse(channel);
      if(!(part instanceof ListPart) || !fourcc.equals(((ListPart)part).type)) throw new Exception("Unexpected part: " + (part == null ? "null" : part.toString()) + ", expecting LIST ('" + fourcc + "' ...)");
      listPart.data.add(part);
   }

   /**
    * <pre>
    * The AVI format looks like: 
    * RIFF ('AVI ' 
    *    LIST ('hdrl' ... ) 
    *    LIST ('INFO' ... )
    *    LIST ('movi' ... ) 
    *    ['idx1' ... ]
    * )
    * </pre>
    */
   @Override
   public void parse(FileChannel channel, ListPart listPart) throws Exception{
      // super.parse makes a dummy data, so don't call it here
      readListPart(channel, listPart, "hdrl");
      readListPart(channel, listPart, "INFO");
      FilePart part = RIFFParser.parse(channel);
      if(part.fourcc.equals("JUNK")){
         listPart.data.add(part);
      }else throw new Exception("not JUNK");
      readListPart(channel, listPart, "movi");
      // Optional index
      try{
         part = RIFFParser.parse(channel);
         if(part.fourcc.equals("idx1")){
            listPart.data.add(part);
         }
      }catch(Exception e){
         e.printStackTrace();
         // TODO Auto-generated catch block - Nov 3, 2008
         System.out.println("Did not find optional index: " + e.getMessage());
      }
   }
}
