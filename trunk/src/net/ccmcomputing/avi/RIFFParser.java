/**
 * Copyright 2008 Cole Markham, all rights reserved.
 */
package net.ccmcomputing.avi;

import static net.ccmcomputing.avi.FilePartFactory.bytes;
import static net.ccmcomputing.avi.FilePartFactory.cluster;
import static net.ccmcomputing.avi.FilePartFactory.sector;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Cole Markham
 * 
 */
public class RIFFParser{
   protected static List<FilePartFactory> FACTORIES;

   static{
      FACTORIES = new ArrayList<FilePartFactory>();
      FACTORIES.add(new ListFactory());
      FACTORIES.add(new ChunkFactory());
      FACTORIES.add(new FilePartFactory());
   }

   public static void extractFile(FileChannel inChannel, long offset, String outfile) throws IOException, Exception, FileNotFoundException{
      inChannel.position(offset);
      FilePart part = parse(inChannel);
      if(part == null) throw new Exception("Didn't find a part");
      FileOutputStream outputStream = new FileOutputStream(outfile);
      try{
         FileChannel outChannel = outputStream.getChannel();
         part.read(inChannel, outChannel);
      }finally{
         outputStream.close();
      }
   }

   public static void main(String[] args) throws IOException{
      if(args.length == 0 || args.length != 1 || args.length != 3){
         usage();
         return;
      }
      FileInputStream inputStream = new FileInputStream(args[0]);
      try{
         FileChannel inChannel = inputStream.getChannel();
         if(args.length > 1){
            try{
               long offset = Long.parseLong(args[1]);
               String outfile = args[2];
               extractFile(inChannel, offset, outfile);
            }catch(Exception e){
               // TODO Auto-generated catch block - Nov 2, 2008
               e.printStackTrace();
            }
         }else{
            int count = 0;
            ByteBuffer buffer = ByteBuffer.allocate(4);
            System.out.println("Scanning for video files:");
            while(inChannel.position() < inChannel.size() - 8){
               try{
                  buffer.rewind();
                  String fourcc = FilePartFactory.readString(inChannel, buffer);
                  if("RIFF".equals(fourcc)){
                     System.out.println();
                     extractFile(inChannel, inChannel.position() - 4, "extracted_video" + count++ + ".avi");
                  }else{
                     long cluster = cluster(inChannel.position());
                     cluster += 1; // skip to the next cluster
                     inChannel.position(bytes(sector(cluster)));
                     System.out.print(".");
                     if(cluster % 100 == 0){
                        System.out.println();
                     }
                  }
               }catch(Exception e){
                  // TODO Auto-generated catch block - Nov 4, 2008
                  e.printStackTrace();
                  break;
               }
            }
         }
      }finally{
         inputStream.close();
      }

   }

   private static void usage(){
      System.out.println("Usage: java -jar avi_recover.jar <input file> [<offset> <output file>]");
      System.out.println("\tIf <offset> and <output file> are specified, a single video is read starting at the given offset and written to the given file.");
      System.out.println("\tOtherwise, the entire input file is scanned and all videos are extracted to the current directory with the format extracted_video#.avi");
   }

   public static FilePart parse(FileChannel channel) throws Exception{
      ByteBuffer buffer = ByteBuffer.allocate(4);
      String fourcc = FilePartFactory.readString(channel, buffer);
      FilePart part = null;
      for(FilePartFactory factory: FACTORIES){
         if(factory.isValid(fourcc)){
            part = factory.parse(channel, fourcc);
            break;
         }
      }
      if(part == null) throw new Exception("Unable to find factory for code: " + fourcc + " @" + (channel.position() - 4));
      return part;
   }

}
