package uk.ac.ncl.cs.csc8101.hadoop.calculate;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class RankCalculateMapper extends Mapper<LongWritable, Text, Text, Text> {
    /**
     * The `map(...)` method is executed against each item in the input split. A key-value pair is
     * mapped to another, intermediate, key-value pair.
     *
     * Specifically, this method should take Text objects in the form
     *      `"[page]    [initialPagerank]    outLinkA,outLinkB,outLinkC..."`
     * and store a new key-value pair mapping linked pages to this page's name, rank and total number of links:
     *      `"[otherPage]   [thisPage]    [thisPagesRank]    [thisTotalNumberOfLinks]"
     *
     * Note: Remember that the pagerank calculation MapReduce job will run multiple times, as the pagerank will get
     * more accurate with each iteration. You should preserve each page's list of links.
     *
     * @param key the key associated with each item output from {@link uk.ac.ncl.cs.csc8101.hadoop.parse.PageLinksParseReducer PageLinksParseReducer}
     * @param value the text value "[page]  [initialPagerank]   outLinkA,outLinkB,outLinkC..."
     * @param context Mapper context object, to which key-value pairs are written
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] data = value.toString().split("\t");
        //Check if any outLinks exists if no just write to context
        boolean anyOutLinks = data.length == 3;
        if (anyOutLinks) {
            String[] outLinks = data[2].split(",");
        /*
                data[0] - otherPage
                data[1] - otherPage page rank
                data[2]/outLinks - outgoing links (length indicates how many links are outgoing)

                store is the context value combined from data described above
          */
            String store = data[0] + "\t" + data[1] + "\t" + outLinks.length;
            // each outLink is treated as context key
            for (String link : outLinks) {
                context.write(new Text(link), new Text(store));
            }
            // Linkage of each page (Same as value provided by parser just page rank changed to '!')
            context.write(new Text(data[0]), new Text("!\t" + data[2]));
        } else {
            context.write(new Text(data[0]), new Text("!"));
        }
    }
}
