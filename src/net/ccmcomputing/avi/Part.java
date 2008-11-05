package net.ccmcomputing.avi;

import java.nio.channels.FileChannel;

public abstract class Part{

   protected long offset;
   protected int size;

   public abstract void read(FileChannel input, FileChannel output) throws Exception;

}
