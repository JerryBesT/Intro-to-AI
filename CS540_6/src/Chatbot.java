
import java.util.*;
import java.io.*;

public class Chatbot{
    private static String filename = "./WARC201709_wid.txt";
    private static ArrayList<Integer> readCorpus(){
        ArrayList<Integer> corpus = new ArrayList<Integer>();
        try{
            File f = new File(filename);
            Scanner sc = new Scanner(f);
            while(sc.hasNext()){
                if(sc.hasNextInt()){
                    int i = sc.nextInt();
                    corpus.add(i);
                }
                else{
                    sc.next();
                }
            }
        }
        catch(FileNotFoundException ex){
            System.out.println("File Not Found.");
        }
        return corpus;
    }
    
    /*
     *  The unigram model
     */
    private static int unigram(double area, ArrayList<Integer> corpus) {
    	List<double[]> distribution = new ArrayList<>();
        HashMap<Integer, Integer> table = new HashMap<>();
        for(int curr : corpus)
        	table.put(curr, table.getOrDefault(curr, 0) + 1);
        double prev = 0.0;
        for(int i = 0;i < 4700;i++) {
        	distribution.add(new double[]{i, prev, prev + (double)table.get(i) / (double)corpus.size()});
        	prev += (double)table.get(i) / (double)corpus.size();
        }
        
        int num = 0;
        for(double[] interval : distribution){
        	if(area >= interval[1] && area <= interval[2]) {
        		num = (int) interval[0];
        		break;
        	}
        }
        return num;
    }
    
    /*
     * The bigram model
     */
    private static int bigram(double area, ArrayList<Integer> corpus, int h) {
        ArrayList<double[]> distribution = new ArrayList<>();
        LinkedHashMap<Integer, Integer> word = new LinkedHashMap<>();
        int sum_h = 0;
        for(int i = 0;i < corpus.size() - 1;i++) {
        	if(corpus.get(i) == h) {
        		sum_h++;
        		word.put(corpus.get(i + 1), word.getOrDefault(corpus.get(i + 1), 0) + 1);
        	}
        }
        
        double prev = 0.0;
        for(int i = 0 ;i < 4700;i++) {
        	if(word.containsKey(i)) {
            	distribution.add(new double[]{i, prev, prev + (double) word.get(i) / (double) sum_h});
            	prev += (double) word.get(i) / (double) sum_h;
        	}
        }
        
        int num = 0;
        for(double[] interval : distribution) {
        	if(area >= interval[1] && area <= interval[2]) {
        		num = (int) interval[0];
        		break;
        	}
        }
        return num;
    }
    
    /*
     * The tri-gram model
     */
    private static int trigram(double area, ArrayList<Integer> corpus, int h1, int h2) {
    	ArrayList<double[]> distribution = new ArrayList<>();
        LinkedHashMap<Integer, Integer> word = new LinkedHashMap<>();
        int sum_h = 0;
        for(int i = 0;i < corpus.size() - 2;i++) {
        	if(corpus.get(i) == h1 && corpus.get(i + 1) == h2) {
        		sum_h++;
        		word.put(corpus.get(i + 2), word.getOrDefault(corpus.get(i + 2), 0) + 1);
        	}
        }
        
        double prev = 0.0;
        for(int i = 0 ;i < 4700;i++) {
        	if(word.containsKey(i)) {
            	distribution.add(new double[]{i, prev, prev + (double) word.get(i) / (double) sum_h});
            	prev += (double) word.get(i) / (double) sum_h;
        	}
        }
        
        if(sum_h == 0)
        	System.out.println("undefined");
        
        int num = 0;
        for(double[] interval : distribution) {
        	if(area >= interval[1] && area <= interval[2]) {
        		num = (int) interval[0];
        		break;
        	}
        }
    	return num;
    }
    
    static public void main(String[] args){
        ArrayList<Integer> corpus = readCorpus();
		int flag = Integer.valueOf(args[0]);
        
        if(flag == 100){
			int w = Integer.valueOf(args[1]);
            int count = 0;
            for(int curr : corpus)
            	if(curr == w)
            		count++;
            System.out.println(count);
            System.out.println(String.format("%.7f",count/(double)corpus.size()));
        }
        else if(flag == 200){
            int n1 = Integer.valueOf(args[1]);
            int n2 = Integer.valueOf(args[2]);
            double area = (double)n1 / (double)n2;
            //System.out.println(String.format("%.7f", area));
            List<double[]> distribution = new ArrayList<>();
            HashMap<Integer, Integer> table = new HashMap<>();
            for(int curr : corpus)
            	table.put(curr, table.getOrDefault(curr, 0) + 1);
            double prev = 0.0;
            for(int i = 0;i < 4700;i++) {
            	distribution.add(new double[]{i, prev, prev + (double)table.get(i) / (double)corpus.size()});
            	prev += (double)table.get(i) / (double)corpus.size();
            }
            
            for(double[] interval : distribution){
            	if(area >= interval[1] && area <= interval[2]) {
            		System.out.println((int)interval[0]);
            		System.out.println(String.format("%.7f", interval[1]));
            		System.out.println(String.format("%.7f", interval[2]));
            		break;
            	}
            }
        }
        else if(flag == 300){
            int h = Integer.valueOf(args[1]);
            int w = Integer.valueOf(args[2]);
            int count = 0;
            ArrayList<Integer> words_after_h = new ArrayList<Integer>();
            for(int i = 0;i < corpus.size() - 1;i++) {
            	if(corpus.get(i) == h && corpus.get(i + 1) == w)
            		count++;
            	if(corpus.get(i) == h)
            		words_after_h.add(corpus.get(i + 1));
            }
            //output 
            System.out.println(count);
            System.out.println(words_after_h.size());
            System.out.println(String.format("%.7f",count/(double)words_after_h.size()));
        }
        else if(flag == 400){
            int n1 = Integer.valueOf(args[1]);
            int n2 = Integer.valueOf(args[2]);
            int h = Integer.valueOf(args[3]);
            double area = (double) n1 / (double) n2;
            ArrayList<double[]> distribution = new ArrayList<>();
            LinkedHashMap<Integer, Integer> word = new LinkedHashMap<>();
            int sum_h = 0;
            for(int i = 0;i < corpus.size() - 1;i++) {
            	if(corpus.get(i) == h) {
            		sum_h++;
            		word.put(corpus.get(i + 1), word.getOrDefault(corpus.get(i + 1), 0) + 1);
            	}
            }
            
            double prev = 0.0;
            for(int i = 0 ;i < 4700;i++) {
            	if(word.containsKey(i)) {
	            	distribution.add(new double[]{i, prev, prev + (double) word.get(i) / (double) sum_h});
	            	prev += (double) word.get(i) / (double) sum_h;
            	}
            }
      
            for(double[] interval : distribution) {
            	if(area >= interval[1] && area <= interval[2]) {
            		System.out.println((int) interval[0]);
            		System.out.println(String.format("%.7f", interval[1]));
            		System.out.println(String.format("%.7f", interval[2]));
            		break;
            	}
            }
        }
        else if(flag == 500){
            int h1 = Integer.valueOf(args[1]);
            int h2 = Integer.valueOf(args[2]);
            int w = Integer.valueOf(args[3]);
            int count = 0;
            ArrayList<Integer> words_after_h1h2 = new ArrayList<Integer>();
            for(int i = 0;i < corpus.size() - 2;i++) {
            	if(corpus.get(i) == h1 && corpus.get(i + 1) == h2) {
            		words_after_h1h2.add(corpus.get(i + 2));
            		if(corpus.get(i + 2) == w)
            			count++;
            	}
            }
           
            //output 
            System.out.println(count);
            System.out.println(words_after_h1h2.size());
            if(words_after_h1h2.size() == 0)
                System.out.println("undefined");
            else
                System.out.println(String.format("%.7f",count/(double)words_after_h1h2.size()));
        }
        else if(flag == 600){
            int n1 = Integer.valueOf(args[1]);
            int n2 = Integer.valueOf(args[2]);
            int h1 = Integer.valueOf(args[3]);
            int h2 = Integer.valueOf(args[4]);
            double area = (double) n1 / (double) n2;
            ArrayList<double[]> distribution = new ArrayList<>();
            LinkedHashMap<Integer, Integer> word = new LinkedHashMap<>();
            int sum_h = 0;
            for(int i = 0;i < corpus.size() - 2;i++) {
            	if(corpus.get(i) == h1 && corpus.get(i + 1) == h2) {
            		sum_h++;
            		word.put(corpus.get(i + 2), word.getOrDefault(corpus.get(i + 2), 0) + 1);
            	}
            }
            
            double prev = 0.0;
            for(int i = 0 ;i < 4700;i++) {
            	if(word.containsKey(i)) {
	            	distribution.add(new double[]{i, prev, prev + (double) word.get(i) / (double) sum_h});
	            	prev += (double) word.get(i) / (double) sum_h;
            	}
            }
            
            if(sum_h == 0)
            	System.out.println("undefined");
            
            for(double[] interval : distribution) {
            	if(area >= interval[1] && area <= interval[2]) {
            		System.out.println((int) interval[0]);
            		System.out.println(String.format("%.7f", interval[1]));
            		System.out.println(String.format("%.7f", interval[2]));
            		break;
            	}
            }
        }
        else if(flag == 700){
            int seed = Integer.valueOf(args[1]);
            int t = Integer.valueOf(args[2]);
            int h1=0,h2=0;

            Random rng = new Random();
            if (seed != -1) rng.setSeed(seed);

            if(t == 0){
                // TODO Generate first word using r
                double r = rng.nextDouble();
                h1 = unigram(r, corpus);
                System.out.println(h1);
                if(h1 == 9 || h1 == 10 || h1 == 12){
                    return;
                }

                // TODO Generate second word using r
                r = rng.nextDouble();
                h2 = bigram(r, corpus, h1);
                System.out.println(h2);
            }
            else if(t == 1){
                h1 = Integer.valueOf(args[3]);
                // TODO Generate second word using r
                double r = rng.nextDouble();
                h2 = bigram(r, corpus, h1);
                System.out.println(h2);
            }
            else if(t == 2){
                h1 = Integer.valueOf(args[3]);
                h2 = Integer.valueOf(args[4]);
            }

            while(h2 != 9 && h2 != 10 && h2 != 12){
                double r = rng.nextDouble();
                int w  = 0;
                // TODO Generate new word using h1,h2
                w = trigram(r, corpus, h1, h2);
                System.out.println(w);
                h1 = h2;
                h2 = w;
            }
        }

        return;
    }
}
