package COMP250_A4_W2020;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;

public class TwitterBenchmark extends HashTableBenchmark {
    private static final String stopWordsFileName = "stopWords.txt";
    private static final String tweetsFileName = "tester_tweets.csv";
    private ArrayList<Tweet> tweets;
    private ArrayList<String> stopWords;

    public TwitterBenchmark() {
        super();
        readStressTestFiles(tweetsFileName, stopWordsFileName);

    }

    public static void main(String[] args) {
        TwitterBenchmark tBM = new TwitterBenchmark();
        System.out.println(tBM.timedTwitterAdd(1));
        System.out.println(tBM.basicTwitterTest());
    }

    public String basicTwitterTest() {
        StringBuilder output = new StringBuilder();
        try {
            long startTime;
            long endTime;
            output.append("[BASIC TWITTER TEST] Working Directory = ").append(System.getProperty("user.dir")).append("\n");
            try {
                startTime = System.nanoTime();
                Thread.sleep(50);
                endTime = System.nanoTime();
                output.append("[BASIC TWITTER TESTER] Pause for 25ms took " + (endTime - startTime) + " nanoseconds. \n");
                startTime = System.nanoTime();
                Twitter t = new Twitter(tweets, stopWords);
                endTime = System.nanoTime();
                output.append("[BASIC TWITTER TEST] Twitter constructor ran in ").append(endTime - startTime).append(" nanoseconds. \n");

                try {
                    startTime = System.nanoTime();
                    t.addTweet(rand.nextTweet());
                    endTime = System.nanoTime();
                    output.append("[BASIC TWITTER TEST] Twitter add ran in ").append(endTime - startTime).append(" nanoseconds. \n");

                } catch (Exception e) {
                    output.append("[BASIC TWITTER TEST] Twitter add call failed. Error: ").append(e.getMessage()).append("\n");
                    e.printStackTrace();
                }

                try {
                    startTime = System.nanoTime();
                    ArrayList<Tweet> byDateOutput = t.tweetsByDate("2020-03-25 22:05:42");
                    endTime = System.nanoTime();
                    output.append("[BASIC TWITTER TEST] Twitter tweets per day ran in ").append(endTime - startTime).append(" nanoseconds. \n");
                    if (byDateOutput.size() != 228) {
                        output.append("[BASIC TWITTER TEST] Output size mismatch? Size was " + byDateOutput.size() + "\n[BASIC TWITTER TEST] Tweets from date produced tweets like: ").append(byDateOutput.get((byDateOutput.size() - 1) / 2)).append("\n");
                    } else
                        output.append("[BASIC TWITTER TEST] For fun, here's a tweet it got " + byDateOutput.get((byDateOutput.size() - 1) / 2) + "\n");
                } catch (Exception e) {
                    output.append("[BASIC TWITTER TEST] Twitter Tweets per day call failed. Error: ").append(e.getMessage() + "\n");
                    e.printStackTrace();
                }

                try {
                    startTime = System.nanoTime();
                    Tweet byNameOutput = t.latestTweetByAuthor("Chris");
                    endTime = System.nanoTime();
                    output.append("[BASIC TWITTER TEST] Twitter latest tweet by author ran in ").append(endTime - startTime).append(" nanoseconds. \n\n");
                    String realLatest = "Chris\tThu Mar 26 00:50:41 +0000 2020\t@batfren @rixor14 he was pretty much the main guy in happy home designer. i love him like a son.  come home digby.";
                    output.append("[BASIC TWITTER TEST] Twitter latest tweet by author yielded: MESSAGE:" + byNameOutput.getMessage() + "\n");
                    output.append("[BASIC TWITTER TEST] Twitter latest tweet by author yielded: AUTHOR: " + byNameOutput.getAuthor() + "\n");
                    output.append("[BASIC TWITTER TEST] Twitter latest tweet by author yielded: DATE: " + byNameOutput.getDateAndTime() + "\n\n");
                    output.append("[BASIC TWITTER TEST] Ouput should be " + realLatest + "\n\n");
                } catch (Exception e) {
                    output.append("[BASIC TWITTER TEST] Twitter Tweets by author call failed. Error: ").append(e.getMessage() + "\n");
                    e.printStackTrace();
                }

                try {
                    startTime = System.nanoTime();
                    ArrayList<String> keywords = t.trendingTopics();
                    endTime = System.nanoTime();
                    output.append("[BASIC TWITTER TEST] Twitter trending tipics ran in ").append(endTime - startTime).append(" nanoseconds. \n");
                    output.append("[BASIC TWITTER TEST] keywords were: \n");
                    int counter = 0;
                    for (String s : keywords) {
                        if (counter >= 35) {
                            output.append("[BASIC TWITTER TEST] .... more below, reached print limit.\n");
                            break;
                        }
                        if (!s.isEmpty()) {
                            output.append("[BASIC TWITTER TEST]                 ").append(s).append("\n");
                        } else {
                            output.append("[BASIC TWITTER TEST]                 Empty String in output.\n");
                        }
                        counter++;
                    }
                } catch (Exception e) {
                    output.append("[BASIC TWITTER TEST] Twitter trending topics call failed. Error: ").append(e.getMessage() + "\n");
                    e.printStackTrace();
                }

            } catch (Exception e) {
                output.append("[BASIC TWITTER TEST] Twitter constructor call failed. Error: ").append(e.getMessage() + "\n");
                e.printStackTrace();
            }


        } catch (Exception e) {
            output.append("[BASIC TWITTER TESTER] Failed to run.");
        }

        return output.toString();
    }

    public long timedTwitterConstructor(int numTweets, int numStopWords) {       //TODO: Freeze?
        ArrayList<String> toAddStopWords = new ArrayList<String>();
        if (numStopWords < stopWords.size()) {
            toAddStopWords.addAll(stopWords.subList(0, numStopWords));
        } else {
            if (!stopWords.isEmpty()) {
                toAddStopWords.addAll(stopWords);
                for (int i = 0; i < numStopWords - stopWords.size(); i++) {
                    toAddStopWords.add(rand.nextName());
                }
            } else {
                for (int i = 0; i < numStopWords; i++) {
                    toAddStopWords.add(rand.nextName());
                }
            }
        }
        ArrayList<Tweet> toAddTweets = new ArrayList<>(Arrays.asList(rand.nextTweets(numTweets, true)));
        long startTime = System.nanoTime();
        Twitter t = new Twitter(toAddTweets, toAddStopWords);
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    public long timedTwitterAdd(int length) {
        System.out.println(stopWords.size());
        Twitter t = new Twitter(new ArrayList<>(Arrays.asList(rand.nextTweets(1, true))), new ArrayList<>(stopWords.subList(0, (stopWords.size() - 1) / 2)));
        for (int i = 0; i < length - 1; i++) {
            t.addTweet(rand.nextTweet());
        }
        Tweet toAdd = rand.nextTweet();
        long startTime = System.nanoTime();
        t.addTweet(toAdd);
        long endTime = System.nanoTime();
        return endTime - startTime;
    }


    /**
     * File reader from Prof's code.
     * <p>
     * Copied from HashTableStressTester.java
     */
    public void readStressTestFiles(String tweetsFileName, String stopWordsFileName) {

        DateTimeFormatter dateFormatFile = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss xx yyyy");
        DateTimeFormatter dateFormatTweets = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        BufferedReader brTweets = null;
        BufferedReader brStopwords = null;
        String line = "";
        String cvsSplitBy = "\t";

        tweets = new ArrayList<Tweet>();
        stopWords = new ArrayList<String>();

        try {

            // read tweets into Arraylist

            brTweets = new BufferedReader(new FileReader(tweetsFileName));
            brTweets.readLine(); // skip first line
            while ((line = brTweets.readLine()) != null) {

                // use comma as separator
                String[] tweetArray = line.split(cvsSplitBy);

                // skip first line

                // skip multi line tweets in quotes
                if (tweetArray.length < 3) {
                    continue;
                }

                if (tweetArray[2].contains("\"")) {
                    continue;
                }

                TemporalAccessor dateTime = dateFormatFile.parse(tweetArray[1]);

                Tweet tweet = new Tweet(tweetArray[0], dateFormatTweets.format(dateTime), tweetArray[2]);

                tweets.add(tweet);
            }

            // read stopWords into ArrayList

            brStopwords = new BufferedReader(new FileReader(stopWordsFileName));
            while ((line = brStopwords.readLine()) != null) {

                // use comma as separator

                stopWords.add(line);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (brTweets != null) {
                try {
                    brTweets.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (brStopwords != null) {
                try {
                    brStopwords.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Long timedTwitterByDate(int i) {
        return i ^ 15L;
    }

    public Long timedTwitterByAuth(int i) {
        return 15L;
    }

    public Long timedTwitterTrending(int i) {
        return 15L;
    }
}