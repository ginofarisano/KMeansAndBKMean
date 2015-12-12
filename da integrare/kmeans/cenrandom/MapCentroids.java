package org.soa.cenrandom;

import java.io.IOException;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class MapCentroids
  extends Mapper<Text, MapWritable, IntWritable, MapWritable>
{
  private IntWritable indexSelectedWr = new IntWritable();
  private Random rnd = new Random();
  private int prob;
  private int numbersOfCentroids;
  private Counter cs;
  
  static enum MAP_OUTPUT
  {
    COUNTER;
  }
  
  private static int output = 0;
  
  protected void setup(Mapper<Text, MapWritable, IntWritable, MapWritable>.Context context)
    throws IOException, InterruptedException
  {
    super.setup(context);
    
    Configuration conf = context.getConfiguration();
    
    this.cs = context.getCounter(MAP_OUTPUT.COUNTER);
    
    this.numbersOfCentroids = conf.getInt("centroids", 2);
    
    this.prob = conf.getInt("prob", 2);
    if (this.prob < 1) {
      this.prob = 2;
    }
  }
  
  public void map(Text key, MapWritable value, Mapper<Text, MapWritable, IntWritable, MapWritable>.Context context)
    throws IOException, InterruptedException
  {
    if (this.cs.getValue() < this.numbersOfCentroids)
    {
      this.cs.increment(1L);
      context.write(this.indexSelectedWr, value);
    }
    else
    {
      int t = this.rnd.nextInt(this.prob);
      if (this.rnd.nextInt(this.prob) == t) {
        context.write(this.indexSelectedWr, value);
      }
    }
  }
}
