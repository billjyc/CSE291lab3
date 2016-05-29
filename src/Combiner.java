import com.jcraft.jsch.Buffer;

import java.io.*;
import java.util.*;

/**
 * Created by billjyc on 5/28/16.
 */
public class Combiner {
    public static void main(String[] args) {
        int length = args.length;
        if(length != 4) {
            throw new RuntimeException("number of arguments doesn't equal to 4");
        }
        File[] files = new File[4];
        for(int i = 0; i < files.length; i++) {
            files[i] = new File("output/" + args[i]);
            System.out.println(files[i].getAbsolutePath());
            if(!files[i].exists()) {
                System.err.println("File " + args[i] +" does not exist!");
                System.exit(1);
            }
        }

        Map<String, Integer> map = new LinkedHashMap<>();
        for(int i = 0; i < 4; i++) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(files[i]));
                String line;
                while((line = br.readLine()) != null) {
                    String[] strs = line.trim().split("\t");
                    if(strs.length > 2) {
                        StringBuilder sb = new StringBuilder();
                        for(int j = 0; j < strs.length - 1; j++) {
                            sb.append(strs[j]).append(" ");
                        }
                        map.put(sb.toString(), Integer.valueOf(strs[strs.length - 1]));
                    } else {
                        map.put(strs[0], Integer.parseInt(strs[1]));
                    }
//                    System.out.println(strs[0]);
//                    System.out.println(strs[1]);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Map<String, Integer> newMap = sortMap(map);
        int total = 0;
        for(Map.Entry<String, Integer> entry: newMap.entrySet()) {
            total += entry.getValue();
        }

        System.out.println("The total number of bigrams: " + newMap.size());
        for(Map.Entry<String, Integer> entry: newMap.entrySet()) {
            System.out.println("The most common bigram: " + entry.getKey() + "\t" + entry.getValue());
            break;
        }

        int subtotal = 0;
        int num = 0;
        for(Map.Entry<String, Integer> entry: newMap.entrySet()) {
            if(entry.getValue() + subtotal > total / 10) {
                break;
            }
            subtotal += entry.getValue();
            num++;
        }

        System.out.println("The number of bigrams required to add up to 10% of all bigrams: " + num);
//        File fout = new File("out.txt");
//        try {
//            FileOutputStream fos = new FileOutputStream(fout);
//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
//
//            for(Map.Entry<String, Integer> entry: newMap.entrySet()) {
//                bw.write(entry.getKey() + "\t" + entry.getValue());
//                bw.newLine();
//            }
//            bw.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static Map sortMap(Map oldMap) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(oldMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> t0, Map.Entry<String, Integer> t1) {
                return t1.getValue() - t0.getValue();
            }
        });
        Map newMap = new LinkedHashMap<>();
        for(int i = 0; i < list.size(); i++) {
            newMap.put(list.get(i).getKey(), list.get(i).getValue());
        }
        return newMap;
    }
}
