/*
 * Created by: Ehsan Elgendi
 *
 * This code reads the questions from a csv file (StackOverFlow2.csv).
 * The file has only one column with the question body.
 * After reading the question body, the code removes the stop words to
 * generate a search query. A Google search is being performed using this
 * query. The first three Google search results are stored in another csv
 * file (GoogleLinks.csv).
 *
 * Version 2.1
 * last edit: 4/15/2018
 * by: Ehsan Elgendi
*/
package googlesearch;

/**
 *
 * @author ehsan
 */
import java.io.IOException;
import java.util.Scanner;
import java.util.List;
import java.util.Arrays;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.StopFilter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.PrintWriter;

import java.util.concurrent.TimeUnit;
import java.util.Random; 


public class GoogleSearch {
    
    
   public static final String GOOGLE_SEARCH_URL = "https://www.google.com/search";
      
   public static String tokenizeString(Analyzer analyzer, String str) {
    String output = "";
    try {
      TokenStream stream  = analyzer.tokenStream(null, new StringReader(str));
      stream.reset();
      while (stream.incrementToken()) {
        output = output + " " + (stream.getAttribute(CharTermAttribute.class));
      }
    } catch (IOException e) {
      // not thrown b/c we're using a string reader...
      throw new RuntimeException(e);
    }
    return output;
  }
	public static String[] stopwords = {"a", "as", "able", "about", "above", "according", "accordingly", "across", "actually", "after", "afterwards", "again", "against", "aint", "all", "allow", "allows", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "an", "and", "another", "any", "anybody", "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart", "appear", "appreciate", "appropriate", "are", "arent", "around", "as", "aside", "ask", "asking", "associated", "at", "available", "away", "awfully", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "believe", "below", "beside", "besides", "best", "better", "between", "beyond", "both", "brief", "but", "by", "cmon", "cs", "came", "can", "cant", "cannot", "cant", "cause", "causes", "certain", "certainly", "changes", "clearly", "co", "com", "come", "comes", "concerning", "consequently", "consider", "considering", "contain", "containing", "contains", "corresponding", "could", "couldnt", "course", "currently", "definitely", "described", "despite", "did", "didnt", "different", "do", "does", "doesnt", "doing", "dont", "done", "down", "downwards", "during", "each", "edu", "eg", "eight", "either", "else", "elsewhere", "enough", "entirely", "especially", "et", "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere", "ex", "exactly", "example", "except", "far", "few", "ff", "fifth", "first", "five", "followed", "following", "follows", "for", "former", "formerly", "forth", "four", "from", "further", "furthermore", "get", "gets", "getting", "given", "gives", "go", "goes", "going", "gone", "got", "gotten", "greetings", "had", "hadnt", "happens", "hardly", "has", "hasnt", "have", "havent", "having", "he", "hes", "hello", "help", "hence", "her", "here", "heres", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit", "however", "i", "id", "ill", "im", "ive", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc", "indeed", "indicate", "indicated", "indicates", "inner", "insofar", "instead", "into", "inward", "is", "isnt", "it", "itd", "itll", "its", "its", "itself", "just", "keep", "keeps", "kept", "know", "knows", "known", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "lets", "like", "liked", "likely", "little", "look", "looking", "looks", "ltd", "mainly", "many", "may", "maybe", "me", "mean", "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "much", "must", "my", "myself", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not", "nothing", "novel", "now", "nowhere", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on", "once", "one", "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over", "overall", "own", "particular", "particularly", "per", "perhaps", "placed", "please", "plus", "possible", "presumably", "probably", "provides", "que", "quite", "qv", "rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards", "relatively", "respectively", "right", "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious", "seriously", "seven", "several", "shall", "she", "should", "shouldnt", "since", "six", "so", "some", "somebody", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", "sure", "ts", "take", "taken", "tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "thats", "thats", "the", "their", "theirs", "them", "themselves", "then", "thence", "there", "theres", "thereafter", "thereby", "therefore", "therein", "theres", "thereupon", "these", "they", "theyd", "theyll", "theyre", "theyve", "think", "third", "this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two", "un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up", "upon", "us", "use", "used", "useful", "uses", "using", "usually", "value", "various", "very", "via", "viz", "vs", "want", "wants", "was", "wasnt", "way", "we", "wed", "well", "were", "weve", "welcome", "well", "went", "were", "werent", "what", "whats", "whatever", "when", "whence", "whenever", "where", "wheres", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whos", "whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", "without", "wont", "wonder", "would", "would", "wouldnt", "yes", "yet", "you", "youd", "youll", "youre", "youve", "your", "yours", "yourself", "yourselves", "zero"};

	public static void main(String[] args) throws IOException {
            
            String Question;
            Random rand = new Random(); 

            //output file
            PrintWriter pw = new PrintWriter(new File("GoogeLinks.csv"));
            ///Reading from CSV file
            BufferedReader br = null;
            try
            {
                //Reading the csv file
                 br = new BufferedReader(new FileReader("StackOverFlow2.csv"));
                       
                String line = "";
                //Read and Skip the first line
                br.readLine();
                //Reading from the second line
                while ((line = br.readLine()) != null) 
                {
                    //Timeout between google searches
                    TimeUnit.SECONDS.sleep(rand.nextInt(10) + 15);
                    //Remove tags from the question
                    Question = Jsoup.parse(line).text();
                    
                    //Remove stopwords from the question
                    List<String> wordList = Arrays.asList(stopwords);  
                    CharArraySet stops = StopFilter.makeStopSet(wordList);
                    Analyzer analyzer = new StandardAnalyzer(stops);
                    String ss=tokenizeString(analyzer,Question);
		
                    String searchURL = GOOGLE_SEARCH_URL + "?q="+ss+"&num="+3;
                    //without proper User-Agent, we will get 403 error
                    Document doc = Jsoup.connect(searchURL).userAgent("Mozilla/5.0").get();
		
                    Elements results = doc.select("h3.r > a");
                    StringBuilder sb = new StringBuilder();
                     
                    sb.append(Question);
                    sb.append(',');
                    for (Element result : results) {
                        String linkHref = result.attr("href");
                        sb.append(linkHref.substring(6, linkHref.indexOf("&")));
                        sb.append(',');
                    }
                    sb.append('\n');
                    System.out.println(sb);
                    pw.write(sb.toString());
                    pw.flush();
                }
                pw.close();
            
            }
            catch(Exception ee)
            {
                ee.printStackTrace();
            }
            finally
            {
                try
                {
                    br.close();
                }
                catch(IOException ie)
                {
                    System.out.println("Error occured while closing the BufferedReader");
                    ie.printStackTrace();
                }
            } 
    }
    
}
