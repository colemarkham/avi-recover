/**
 * Copyright 2008 Cole Markham, all rights reserved.
 */
package net.ccmcomputing.avi;

/**
 * @author Cole Markham
 * 
 */
public class Chunk extends FilePart{
   protected int streamNum;
   protected ChunkType type;

   @Override
   public String toString(){
      StringBuilder sb = new StringBuilder();
      sb.append(this.streamNum);
      sb.append(" ");
      sb.append(this.type);
      sb.append(" ");
      sb.append((this.size + 8));
      sb.append(" @");
      sb.append(this.offset);
      return sb.toString();
   }

   public static enum ChunkType{
      db("Uncompressed video frame"),
      dc("Compressed video frame"),
      pc("Palette change"),
      wb("Audio data");

      public static ChunkType lookup(String code) throws Exception{
         for(ChunkType type: values()){
            if(type.name().equalsIgnoreCase(code)) return type;
         }
         throw new Exception("Could not determine chunk type");
      }

      private final String text;

      ChunkType(String text){
         this.text = text;
      }

      @Override
      public String toString(){
         return text;
      }
   }
}
