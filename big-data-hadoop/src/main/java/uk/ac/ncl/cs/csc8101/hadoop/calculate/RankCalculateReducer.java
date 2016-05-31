package uk.ac.ncl.cs.csc8101.hadoop.calculate;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class RankCalculateReducer extends Reducer<Text, Text, Text, Text> {

    /**
     * The `reduce(...)` method is called for each <key, (Iterable of values)> pair in the grouped input.
     * Output values must be of the same type as input values and Input keys must not be altered.
     *
     * Specifically, this method should take the iterable of links to a page, along with their pagerank and number of links.
     * It should then use these to increase this page's rank by its share of the linking page's:
     *      thisPagerank +=  linkingPagerank> / count(linkingPageLinks)
     *
     * Note: remember pagerank's dampening factor.
     *
     * Note: Remember that the pagerank calculation MapReduce job will run multiple times, as the pagerank will get
     * more accurate with each iteration. You should preserve each page's list of links.
     *
     * @param page The individual page whose rank we are trying to capture
     * @param values The Iterable of other pages which link to this page, along with their pagerank and number of links
     * @param context The Reducer context object, to which key-value pairs are written
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public void reduce(Text page, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        // Indicates outLinks for page
        String outLinks = "";
        double d = 0.85;
        double pageRank = 0;

        for (Text value: values) {
            // split each value
            String[] data = value.toString().split("\t");
            // Treat as value with outLinks for page
            if (data.length == 2) {
                outLinks = data[1];
            } else if (data.length == 3){
                // Calculate pageRank for each value that points to page
                // PR(T1)/C(T1)+...+PR(Tn)/C(n)
                pageRank += Double.parseDouble(data[1]) / Double.parseDouble(data[2]);
            }
        }
        // Calculate final pageRank
        pageRank = (1 - d) + (d * pageRank);
        // If no outLinks exists for this page do not put them into content
        Text stored = outLinks.equals("") ? new Text(pageRank + "") : new Text(pageRank + "\t" + outLinks);
        context.write(page, stored);

    }
}
