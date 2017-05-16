package edu.uci.ics.textdb.perftest.runme;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import edu.uci.ics.textdb.api.exception.DataFlowException;
import edu.uci.ics.textdb.api.exception.StorageException;
import edu.uci.ics.textdb.perftest.dictionarymatcher.DictionaryMatcherPerformanceTest;
import edu.uci.ics.textdb.perftest.fuzzytokenmatcher.FuzzyTokenMatcherPerformanceTest;
import edu.uci.ics.textdb.perftest.keywordmatcher.KeywordMatcherPerformanceTest;
import edu.uci.ics.textdb.perftest.nlpextractor.NlpExtractorPerformanceTest;
import edu.uci.ics.textdb.perftest.regexmatcher.RegexMatcherPerformanceTest;
import edu.uci.ics.textdb.perftest.utils.PerfTestUtils;

/**
 * @author Hailey Pan
 */

public class RunTests {
    /*
     * Write Indices Run all performance tests.
     * 
     * Passed in below arguments: 
     * file folder path (where data set stored)
     * result folder path (where performance test results stored) 
     * standard index folder path (where standard index stored) 
     * trigram index folder path(where trigram index stored) 
     * queries folder path (where query files stored)
     * 
     * If above arguments are not passed in, default paths will be used (refer
     * to PerfTestUtils.java) If some of the arguments are not applicable,
     * define them as empty string.
     * 
     * Make necessary changes for arguments, such as query file name, threshold
     * list, and regexQueries
     *
     */
    public static void main(String[] args) {
        
        try {
            PerfTestUtils.setFileFolder(args[0]);
            PerfTestUtils.setResultFolder(args[1]);
            PerfTestUtils.setStandardIndexFolder(args[2]);
            PerfTestUtils.setTrigramIndexFolder(args[3]);
            PerfTestUtils.setQueryFolder(args[4]);
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println("missing arguments will be set to default");
        }

        try {
            PerfTestUtils.deleteDirectory(new File(PerfTestUtils.standardIndexFolder));
            PerfTestUtils.deleteDirectory(new File(PerfTestUtils.trigramIndexFolder));

            PerfTestUtils.writeStandardAnalyzerIndices();
            PerfTestUtils.writeTrigramIndices();

            List<Double> thresholds = Arrays.asList(0.8, 0.65, 0.5, 0.35);
            List<String> regexQueries = Arrays.asList("mosquitos?", "v[ir]{2}[us]{2}", "market(ing)?",
                    "medic(ine|al|ation|are|aid)?", "[A-Z][aeiou|AEIOU][A-Za-z]*");

            KeywordMatcherPerformanceTest.runTest("sample_queries.txt");
            DictionaryMatcherPerformanceTest.runTest("sample_queries.txt");
            FuzzyTokenMatcherPerformanceTest.runTest("sample_queries.txt", thresholds);
            RegexMatcherPerformanceTest.runTest(regexQueries);
            NlpExtractorPerformanceTest.runTest();

        } catch (StorageException | DataFlowException | IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
