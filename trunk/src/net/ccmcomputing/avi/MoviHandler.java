/**
 * Copyright 2008 Cole Markham, all rights reserved.
 */
package net.ccmcomputing.avi;

import static net.ccmcomputing.avi.FilePartFactory.bytes;
import static net.ccmcomputing.avi.FilePartFactory.cluster;
import static net.ccmcomputing.avi.FilePartFactory.sector;

import java.io.EOFException;
import java.nio.channels.FileChannel;

/**
 * @author Cole Markham
 * 
 */
public class MoviHandler extends ListHandler{
   @Override
   public void parse(FileChannel channel, ListPart listPart) throws Exception{
      // TODO move the scanning code to a common location
      int sizeCount = 0;
      Chunk chunk = null;
      long size = listPart.size - 4;
      while(sizeCount < size){
         try{
            FilePart part = RIFFParser.parse(channel);
            if(part instanceof Chunk){
               if(chunk == null){
                  System.out.println();
                  System.out.println("Found chunk at " + part.offset);
               }
               chunk = (Chunk)part;
            }else throw new Exception("Unexpected part, expecting chunk, found " + part.toString());
            listPart.data.add(chunk);
            // channel.position(channel.position() + chunk.size);
            sizeCount += chunk.size + 8;
         }catch(EOFException e){
            System.out.println("Reached end of file while trying to find next chunk.");
            throw e;
            // break;
         }catch(Exception e){
            if(chunk != null){
               System.out.println("\tError: " + e.getMessage() + " @" + channel.position() + " found " + sizeCount + " of " + size);
               System.out.println("\tScanning for next chunk.");
               long cluster = cluster(chunk.offset + 8 + chunk.size);
               cluster += 1; // skip to the next cluster
               channel.position(bytes(sector(cluster)));
               chunk = null;
            }else{
               // keep scanning for next chunk
               if(channel.position() % 16384 == 0){
                  System.out.print(".");
               }
               if(channel.position() % (16384 * 80) == 0){
                  System.out.println();
               }
            }
         }
      }
   }
}
