/**
 * Copyright 2008 Cole Markham, all rights reserved.
 */
package net.ccmcomputing.avi;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.List;

/**
 * @author Cole Markham
 * 
 */
public class FilePartFactory{
   private static final List<String> GENERIC_FOURCC = Arrays.asList("avih", "strh", "strf", "idx1", "JUNK", "ISFT");

   public static long bytes(long sector){
      return sector * 512;
   }

   public static long cluster(long position){
      return (position / 512 - 511) / 32 + 2;
   }

   public static void read(FileChannel channel, ByteBuffer buf) throws IOException, Exception{
      buf.order(ByteOrder.LITTLE_ENDIAN);
      int length = buf.remaining();
      int read = channel.read(buf);
      if(read != length) throw new EOFException("Could not read part");
      buf.flip();
   }

   public static String readString(FileChannel channel, ByteBuffer buf) throws Exception{
      read(channel, buf);
      return new String(buf.array(), "UTF-8");
   }

   public static long sector(long cluster){
      return (cluster - 2) * 32 + 511;
   }

   protected FilePart create(){
      return new FilePart();
   }

   protected void parseData(FileChannel channel, FilePart part) throws Exception{
      // by default just seek to end of data
      channel.position(part.offset + 8 + part.size);
   }

   public boolean isValid(String fourcc){
      return GENERIC_FOURCC.contains(fourcc);
   }

   /**
    * @param channel
    * @param fourcc
    * @throws Exception
    * @return the FilePart that was parsed
    */
   public FilePart parse(FileChannel channel, String fourcc) throws Exception{
      FilePart part = create();
      part.fourcc = fourcc;
      part.offset = channel.position() - 4;
      ByteBuffer buffer = ByteBuffer.allocate(4);
      read(channel, buffer);
      buffer.order(ByteOrder.LITTLE_ENDIAN);
      part.size = buffer.getInt();
      parseData(channel, part);
      System.out.println("Found " + part.toString());
      return part;
   }

}
