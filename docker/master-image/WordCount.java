import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.map.InverseMapper;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * Created by billjyc on 5/27/16.
 */
public class WordCount {

    public static class TokenizerMapper
            extends org.apache.hadoop.mapreduce.Mapper<Object, Text, Text, IntWritable> {

        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {
//            String[] iters = value.toString().split(" ");
//            for(int i = 0; i < iters.length - 1; i++) {
//                word.set(iters[i] + "__" + iters[i + 1]);
//                context.write(word, one);
//            }
            StringTokenizer itr = new StringTokenizer(value.toString());
            String previous = null;
            while (itr.hasMoreTokens()) {
                String current = itr.nextToken();
                if(previous == null) {
                    previous = current;
                    continue;
                } else {
                    word.set(previous + "__" + current);
                    previous = current;
                }

                context.write(word, one);
            }
        }
    }

    public static class IntSumReducer
            extends org.apache.hadoop.mapreduce.Reducer<Text,IntWritable,Text,IntWritable> {
        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values,
                           Context context
        ) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }

    /**
     * reverse the location of key and map
     * e.g. ("word", 2) --> (2, "word")
     */
    public static class ReverseMapper
        extends org.apache.hadoop.mapreduce.Mapper<Object, Text, IntWritable, Text> {

        public void map(Object key, Text value, Context context
            ) throws IOException, InterruptedException {
            String[] iters = value.toString().trim().split("\\s+");

            context.write(new IntWritable(Integer.parseInt(iters[1])), new Text(iters[0]));
        }

    }

    public static class ReverseReducer
            extends org.apache.hadoop.mapreduce.Reducer<IntWritable,Text,IntWritable,Text> {

        private Text result = new Text();
        public void reduce(IntWritable key, Iterable<Text> values,
                           Context context
        ) throws IOException, InterruptedException {

            //Text sum = new Text();

            for (Text val : values) {
                context.write(key, val);
            }
            //result.set(sum);
            //context.write(key, result);
        }
    }

    public static class DescendingKeyComparator extends WritableComparator {
        protected DescendingKeyComparator() {
            super(IntWritable.class, true);
        }

        @SuppressWarnings("rawtypes")
        @Override
        public int compare(WritableComparable w1, WritableComparable w2) {
            IntWritable key1 = (IntWritable) w1;
            IntWritable key2 = (IntWritable) w2;
            return -1 * key1.compareTo(key2);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length < 2) {
            System.err.println("Usage: wordcount <in> [<in>...] <out>");
            System.exit(2);
        }
        Job job = new Job(conf, "word count");
        job.setJarByClass(WordCount.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(WordCount.IntSumReducer.class);
        job.setReducerClass(WordCount.IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setNumReduceTasks(4);
        for (int i = 0; i < otherArgs.length - 1; ++i) {
            org.apache.hadoop.mapreduce.lib.input.FileInputFormat.addInputPath(job, new Path(otherArgs[i]));
        }
        org.apache.hadoop.mapreduce.lib.output.FileOutputFormat.setOutputPath(job,
                new Path("temp"));

        if(job.waitForCompletion(true)) {
            Job job2 = new Job(conf, "word count");
            job2.setJarByClass(WordCount.class);
            job2.setMapperClass(ReverseMapper.class);
            job2.setCombinerClass(WordCount.ReverseReducer.class);
            job2.setReducerClass(WordCount.ReverseReducer.class);
            job2.setSortComparatorClass(DescendingKeyComparator.class);
            job2.setOutputKeyClass(IntWritable.class);
            job2.setOutputValueClass(Text.class);
            job2.setNumReduceTasks(4);
            org.apache.hadoop.mapreduce.lib.input.FileInputFormat.addInputPath(job2, new Path("temp/"));
            org.apache.hadoop.mapreduce.lib.output.FileOutputFormat.setOutputPath(job2,
                    new Path(otherArgs[otherArgs.length - 1]));
            job2.waitForCompletion(true);
        }

//        Path path = new Path("output/part-r-00000");
//        FileSystem fs = FileSystem.get(new Configuration());
//        try {
//            BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(path)));
//            String line;
//            line = br.readLine();
//            String[] strs = line.split("\\s");
//            System.out.println("The most frequent bigram: " + strs[1] + "\t" + strs[0]);
//            while(line != null) {
//                line = br.readLine();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }
}
