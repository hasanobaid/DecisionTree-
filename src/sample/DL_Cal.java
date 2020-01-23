package sample;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

public class DL_Cal {
    ArrayList<ArrayList<String>> intialData = new ArrayList<ArrayList<String>>();
    ArrayList<ArrayList<String>> dataconvert = new ArrayList<ArrayList<String>>();
    ArrayList<ArrayList<String>> dataconvertForNext = new ArrayList<ArrayList<String>>();
    ArrayList<Double> indexes = new ArrayList<>();
    ArrayList<ArrayList<String>> test = new ArrayList<ArrayList<String>>();
    ArrayList<ArrayList<String>> testConvert = new ArrayList<ArrayList<String>>();
    ArrayList<ArrayList<String>> training = new ArrayList<ArrayList<String>>();
    ArrayList<ArrayList<String>> trainingConvert = new ArrayList<ArrayList<String>>();
    ArrayList<Double> nodeIndex = new ArrayList<>();
    ArrayList<Double> testindexes = new ArrayList<>();
    ArrayList<Double> accuracy = new ArrayList<>();
    ArrayList<Double> accuracyTest = new ArrayList<>();
    ArrayList<String> leafIndex = new ArrayList<>();
    ArrayList<String> classname = new ArrayList<>();
    ArrayList<ArrayList<String>> forProb = new ArrayList<ArrayList<String>>();

    public void readFile() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("E:\\hasan\\others\\Others\\hasan.java\\ML_assignment_1\\mushroom.arff"));
        ArffLoader.ArffReader arff = new ArffLoader.ArffReader(reader, 1000);
        Instances data = arff.getStructure();
        data.setClassIndex(data.numAttributes() - 1);
        Instance inst;
        while ((inst = arff.readInstance(data)) != null) {
            data.add(inst);
        }
        for (int i = 0; i < data.size(); i++) {
            ArrayList<String> datas = new ArrayList<>();
            datas.add(data.get(i) + "");
            intialData.add(datas);
        }
        System.out.println(intialData.get(0));
        dataconvert = convert(intialData);
        getTestandTrain();
        trainingConvert = convert(training);
        testConvert = convert(test);

        System.out.println(trainingConvert.get(0).size());
        System.out.println(testConvert.get(0).size());
//        testing();
//        cal_Traning();
        System.out.println("*************************test**************************");
        // cal_test();
        buildTree();

        classname.add("cap-shape");
        classname.add("cap-surface");
        classname.add("cap-color");
        classname.add("bruises");
        classname.add("odor");
        classname.add("gill-attachment");
        classname.add("gill-spacing");
        classname.add("gill-size");
        classname.add("gill-color");
        classname.add("stalk-shape");
        classname.add("stalk-root");
        classname.add("stalk-surface-above-ring");
        classname.add("stalk-surface-below-ring");
        classname.add("stalk-color-above-ring");
        classname.add("stalk-color-below-ring");
        classname.add("veil-type");
        classname.add("veil-color");
        classname.add("ring-number");
        classname.add("ring-type");
        classname.add("spore-print-color");
        classname.add("population");
        classname.add("habitat");

    }

    public ArrayList<ArrayList<String>> convert(ArrayList<ArrayList<String>> arr) {
        ArrayList<ArrayList<String>> converter = new ArrayList<ArrayList<String>>();
        int c = arr.size();
        String[] ar = arr.get(0).get(0).split(",");
        int r = ar.length;
        for (int i = 0; i < r; i++) {

            ArrayList<String> da = new ArrayList<>();
            for (int j = 0; j < c; j++) {
                String[] t = arr.get(j).get(0).split(",");

                da.add(t[i]);
            }


            converter.add(da);
        }


        return converter;
    }

    //for cal entropy
    private double cla_entropy(double e, double p) {
        if (e == 0 || p == 0)
            return 0;
        else if (e == 0.5 || p == 0.5)
            return 1;
        else
            return (-e * (Math.log10(e) / Math.log10(2))) - (p * (Math.log10(p) / Math.log10(2)));
    }

    private ArrayList<Integer> elementCounter(ArrayList<String> arr) {

        LinkedHashSet<String> hashSet = new LinkedHashSet<>(arr);

        ArrayList<String> listWithoutDuplicates = new ArrayList<>(hashSet);

        ArrayList<Integer> s = new ArrayList<>();
        for (int i = 0; i < listWithoutDuplicates.size(); i++) {
            s.add(Collections.frequency(arr, listWithoutDuplicates.get(i)));
        }
        return s;
    }

    public double peEntropy() {
        ArrayList<String> classcal = trainingConvert.get(trainingConvert.size() - 1);
        ArrayList<Integer> cal = elementCounter(classcal);

        double eprop = (double) cal.get(0) / classcal.size();
        double pprop = (double) cal.get(1) / classcal.size();
        double entropy = cla_entropy(eprop, pprop);
        return entropy;
    }

    public double peEntropyTest() {
        ArrayList<String> classcal = testConvert.get(testConvert.size() - 1);
        ArrayList<Integer> cal = elementCounter(classcal);

        double eprop = (double) cal.get(0) / classcal.size();
        double pprop = (double) cal.get(1) / classcal.size();
        double entropy = cla_entropy(eprop, pprop);
        return entropy;
    }

    public ArrayList<Double> proba(ArrayList<Integer> arr) {
        ArrayList<Double> s = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            double x = (double) arr.get(i) / trainingConvert.get(0).size();
            s.add(x);
        }
        return s;
    }

    public ArrayList<String> cla_classesEntropy(ArrayList<String> arr) {
        double ec = 0;
        double pc = 0;
        LinkedHashSet<String> hashSet = new LinkedHashSet<>(arr);
        ArrayList<String> prop = new ArrayList<>();
        ArrayList<String> listWithoutDuplicates = new ArrayList<>(hashSet);
        for (int i = 0; i < listWithoutDuplicates.size(); i++) {
            for (int j = 0; j < arr.size(); j++) {
                if (listWithoutDuplicates.get(i).equals(arr.get(j)) && trainingConvert.get(trainingConvert.size() - 1).get(j).equals("e")) {
                    ec++;
                }
                if (listWithoutDuplicates.get(i).equals(arr.get(j)) && trainingConvert.get(trainingConvert.size() - 1).get(j).equals("p")) {
                    pc++;
                }
            }
            prop.add(ec + "," + pc);
            ec = 0;
            pc = 0;
        }


        ArrayList<String> finals = new ArrayList<>();
        ArrayList<Double> s = new ArrayList<>();
        for (int i = 0; i < listWithoutDuplicates.size(); i++) {
            s.add((double) Collections.frequency(arr, listWithoutDuplicates.get(i)));
        }
        for (int i = 0; i < prop.size(); i++) {
            String[] token = prop.get(i).trim().split(",");
            double probe = Double.parseDouble(token[0]) / s.get(i);
            double propp = Double.parseDouble(token[1]) / s.get(i);
            finals.add(probe + "," + propp);
        }
        return finals;
    }

    private double getEntropy(ArrayList<String> arr) {
        int sizes = cla_classesEntropy(arr).size();

        double entro = 0;
        for (int i = 0; i < sizes; i++) {
            String[] token = cla_classesEntropy(arr).get(i).trim().split(",");
            entro += proba(elementCounter(arr)).get(i) * cla_entropy(Double.parseDouble(token[0]), Double.parseDouble(token[1]));
        }
        return entro;
    }

    private ArrayList<Double> getEntropyForAllClass(ArrayList<ArrayList<String>> arr) {
        ArrayList<Double> entropy = new ArrayList<>();
        for (int i = 0; i < arr.size() - 1; i++) {
            entropy.add(getEntropy(arr.get(i)));
        }
        return entropy;
    }

    public ArrayList<Double> getGain(double e, ArrayList<ArrayList<String>> data) {
        ArrayList<Double> arr = getEntropyForAllClass(data);
        ArrayList<Double> gains = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            gains.add(e - arr.get(i));
        }
        return gains;
    }

    public String getMaxIndex(ArrayList<Double> arr) {
        double i = 0;
        double maxIndex = -1;
        double max = 0;
        for (Double x : arr) {
            if ((x != null) && ((max == 0) || (x > max))) {
                max = x;
                maxIndex = i;
            }
            i++;
        }
        String x = max + "," + maxIndex;
        return x;

    }

    /*
    for next single class calculation
     */
    public ArrayList<String> getProbabilityForClassForSingelClass(ArrayList<String> arr, ArrayList<ArrayList<String>> data) {
        double ec = 0;
        double pc = 0;
        LinkedHashSet<String> hashSet = new LinkedHashSet<>(arr);
        ArrayList<String> prop = new ArrayList<>();
        ArrayList<String> listWithoutDuplicates = new ArrayList<>(hashSet);
        for (int i = 0; i < listWithoutDuplicates.size(); i++) {
            for (int j = 0; j < arr.size(); j++) {
                if (listWithoutDuplicates.get(i).equals(arr.get(j)) && data.get(data.size() - 1).get(j).equals("e")) {
                    ec++;
                }
                if (listWithoutDuplicates.get(i).equals(arr.get(j)) && data.get(data.size() - 1).get(j).equals("p")) {
                    pc++;
                }
            }
            prop.add(ec + "," + pc);
            ec = 0;
            pc = 0;
        }
        if (forProb.contains(prop) == false) {
            forProb.add(prop);
            //   System.out.println("prob  = "+prop);
        }

        ArrayList<String> finals = new ArrayList<>();
        ArrayList<Double> s = new ArrayList<>();
        for (int i = 0; i < listWithoutDuplicates.size(); i++) {
            s.add((double) Collections.frequency(arr, listWithoutDuplicates.get(i)));
        }
        for (int i = 0; i < prop.size(); i++) {
            String[] token = prop.get(i).trim().split(",");
            double probe = Double.parseDouble(token[0]) / s.get(i);
            double propp = Double.parseDouble(token[1]) / s.get(i);
            if (probe != 0 || propp != 0)
                finals.add(probe + "," + propp + "," + i);
        }
        return finals;
    }

    public ArrayList<String> getEnropyForSingleClass(ArrayList<String> arr, ArrayList<ArrayList<String>> data) {
        ArrayList<String> prob = getProbabilityForClassForSingelClass(arr, data);

        ArrayList<String> en = new ArrayList<>();
        double entro = 0;
        //   System.out.println("finals class is : "+prob);

        for (int i = 0; i < prob.size(); i++) {
            String[] token = prob.get(i).trim().split(",");
            entro += cla_entropy(Double.parseDouble(token[0]), Double.parseDouble(token[1]));
            en.add(entro + "," + token[2]);
            entro = 0;
        }
        return en;
    }

    public ArrayList<String> getEntropyForAllSingleClassField(ArrayList<ArrayList<String>> arr, double index) {

        ArrayList<String> dat = arr.get((int) index);
        ArrayList<String> finalData = getEnropyForSingleClass(dat, arr);
        ArrayList<String> finals = new ArrayList<>();
        for (int i = 0; i < finalData.size(); i++) {
            String[] to = finalData.get(i).trim().split(",");
            if (!to[0].equals("0.0"))
                finals.add(finalData.get(i));
        }

        return finals;
    }

    public ArrayList<ArrayList<String>> getNextLevelData(double index, ArrayList<ArrayList<String>> arr) {
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            data.add(arr.get(i));
        }
        ArrayList<String> propWithindex = getEntropyForAllSingleClassField(arr, index);

        ArrayList<ArrayList<String>> finaldata = new ArrayList<ArrayList<String>>();
        ArrayList<Double> in = new ArrayList<>();
        for (int i = 0; i < propWithindex.size(); i++) {
            String[] t = propWithindex.get(i).trim().split(",");
            double ii = Double.parseDouble(t[1].trim());
            double p = Double.parseDouble(t[0].trim());
            if (p != 0.0)
                in.add(ii);
        }

        System.out.println("in : " + in);
        ArrayList<String> roots = arr.get((int) index);

        ArrayList<String> finals = new ArrayList<>();
        LinkedHashSet<String> hashSet = new LinkedHashSet<>(roots);
        ArrayList<String> list = new ArrayList<>(hashSet);

        ArrayList<String> filterd = new ArrayList<>();

        for (int i = 0; i < in.size(); i++) {
            Double d = in.get(i);
            int g = d.intValue();
            filterd.add(list.get(g));
        }
        for (int i = 0; i < in.size(); i++) {
            leafIndex.add(in.get(i) + "," + filterd.get(i));
        }
        data.remove((int) index);

        System.out.println(filterd);
        for (int i = 0; i < filterd.size(); i++) {
            for (int j = 0; j < roots.size(); j++) {
                String x = "";
                if (roots.get(j).trim().equals(filterd.get(i).trim())) {
                    for (int k = 0; k < data.size(); k++) {
                        x += data.get(k).get(j) + ",";
                    }
                }
                if (!x.equals("")) {
                    x = x.trim().substring(0, x.length() - 1);
                    finals.add(x);

                }

            }
        }
        for (int i = 0; i < finals.size(); i++) {
            ArrayList<String> d = new ArrayList<>();
            d.add(finals.get(i));
            finaldata.add(d);


        }
        ArrayList<String> en = convert(finaldata).get(convert(finaldata).size() - 1);
        ArrayList<Integer> calc = elementCounter(en);
        System.out.println("calc size : " + calc.size());
        System.out.println("arr size : " + arr.size());
        System.out.println("calc : " + calc);
//        for (int i = 0; i <calc.size() ; i++) {
        double p = (double) calc.get(0) / (double) arr.get(0).size();
        double e = (double) calc.get(1) / (double) arr.get(0).size();
        System.out.println("p : " + p);
        System.out.println("e : " + e);

        double total = e + p;
        accuracy.add(total);
//        }

        return finaldata;
    }

    public ArrayList<ArrayList<String>> getNextLevelDataForTest(double index, ArrayList<ArrayList<String>> arr) {
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            data.add(arr.get(i));
        }
        ArrayList<String> propWithindex = getEntropyForAllSingleClassField(arr, index);

        ArrayList<ArrayList<String>> finaldata = new ArrayList<ArrayList<String>>();
        ArrayList<Double> in = new ArrayList<>();
        for (int i = 0; i < propWithindex.size(); i++) {
            String[] t = propWithindex.get(i).trim().split(",");
            double ii = Double.parseDouble(t[1].trim());
            double p = Double.parseDouble(t[0].trim());
            if (p != 0.0)
                in.add(ii);
        }
        System.out.println("in : " + in);
        ArrayList<String> roots = arr.get((int) index);

        ArrayList<String> finals = new ArrayList<>();
        LinkedHashSet<String> hashSet = new LinkedHashSet<>(roots);
        ArrayList<String> list = new ArrayList<>(hashSet);

        ArrayList<String> filterd = new ArrayList<>();

        for (int i = 0; i < in.size(); i++) {
            Double d = in.get(i);
            int g = d.intValue();
            filterd.add(list.get(g));
        }

        data.remove((int) index);

        System.out.println(filterd);
        for (int i = 0; i < filterd.size(); i++) {
            for (int j = 0; j < roots.size(); j++) {
                String x = "";
                if (roots.get(j).trim().equals(filterd.get(i).trim())) {
                    for (int k = 0; k < data.size(); k++) {
                        x += data.get(k).get(j) + ",";
                    }
                }
                if (!x.equals("")) {
                    x = x.trim().substring(0, x.length() - 1);
                    finals.add(x);

                }

            }
        }
        for (int i = 0; i < finals.size(); i++) {
            ArrayList<String> d = new ArrayList<>();
            d.add(finals.get(i));
            finaldata.add(d);


        }
        ArrayList<String> en = convert(finaldata).get(convert(finaldata).size() - 1);
        ArrayList<Integer> calc = elementCounter(en);
        System.out.println("calc size : " + calc.size());
        System.out.println("arr size : " + arr.size());
        System.out.println("calc : " + calc);

        double p = (double) calc.get(0) / (double) arr.get(0).size();
        double e = (double) calc.get(1) / (double) arr.get(0).size();
        System.out.println("p : " + p);
        System.out.println("e : " + e);

        double total = e + p;
        accuracyTest.add(total);
        System.out.println("class size: "+arr.get(0).size());


        return finaldata;
    }
    /*
    for leaf clalculation
     */


    private ArrayList<String> getProbabilityForClassesInLeaf(ArrayList<String> arr, ArrayList<ArrayList<String>> data) {
        double ec = 0;
        double pc = 0;
        LinkedHashSet<String> hashSet = new LinkedHashSet<>(arr);
        ArrayList<String> prop = new ArrayList<>();
        ArrayList<String> listWithoutDuplicates = new ArrayList<>(hashSet);
        for (int i = 0; i < listWithoutDuplicates.size(); i++) {
            for (int j = 0; j < arr.size(); j++) {
                if (listWithoutDuplicates.get(i).equals(arr.get(j)) && data.get(data.size() - 1).get(j).equals("e")) {
                    ec++;
                }
                if (listWithoutDuplicates.get(i).equals(arr.get(j)) && data.get(data.size() - 1).get(j).equals("p")) {
                    pc++;
                }
            }
            prop.add(ec + "," + pc);
            ec = 0;
            pc = 0;
        }

        ArrayList<String> finals = new ArrayList<>();
        ArrayList<Double> s = new ArrayList<>();
        for (int i = 0; i < listWithoutDuplicates.size(); i++) {
            s.add((double) Collections.frequency(arr, listWithoutDuplicates.get(i)));
        }
        for (int i = 0; i < prop.size(); i++) {
            String[] token = prop.get(i).trim().split(",");
            double probe = Double.parseDouble(token[0]) / s.get(i);
            double propp = Double.parseDouble(token[1]) / s.get(i);
            if (probe != 0 || propp != 0)
                finals.add(probe + "," + propp + "," + i);
        }
        return finals;
    }


    private double getEntropyForLeaf(ArrayList<String> arr, ArrayList<ArrayList<String>> data) {
        ArrayList<String> prob = getProbabilityForClassesInLeaf(arr, data);
        double entro = 0;
        for (int i = 0; i < prob.size(); i++) {
            String[] token = prob.get(i).trim().split(",");
            entro += probabilityForLeaf(elementCounter(arr), data).get(i) * cla_entropy(Double.parseDouble(token[0]), Double.parseDouble(token[1]));
        }
        return entro;
    }

    public ArrayList<Double> probabilityForLeaf(ArrayList<Integer> arr, ArrayList<ArrayList<String>> data) {
        ArrayList<Double> s = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            s.add((double) arr.get(i) / data.get(0).size());
        }
        return s;
    }

    private ArrayList<Double> getEntropyForAllLeaf(ArrayList<ArrayList<String>> arr) {
        ArrayList<Double> entropy = new ArrayList<>();
        for (int i = 0; i < arr.size() - 1; i++) {
            entropy.add(getEntropyForLeaf(arr.get(i), arr));
        }
        return entropy;
    }

    public ArrayList<Double> getGainForLeaf(double e, ArrayList<ArrayList<String>> data) {
        ArrayList<Double> arr = getEntropyForAllLeaf(data);
        ArrayList<Double> gains = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            gains.add(e - arr.get(i));
        }
        return gains;
    }

    public void cal_Traning() {
        ArrayList<String> entropy = new ArrayList<>();

        ArrayList<Double> gain = getGain(peEntropy(), trainingConvert);
        System.out.println(gain);
        System.out.println(gain.size());
        System.out.println(getMaxIndex(gain));

        String[] tokenIndex = getMaxIndex(gain).trim().split(",");
        double index1 = Double.parseDouble(tokenIndex[1]);
        indexes.add(index1);
/*
start root 2
 */
        entropy = getEntropyForAllSingleClassField(trainingConvert, index1);
        ArrayList<ArrayList<String>> next = convert(getNextLevelData(index1, trainingConvert));
        int i = 1;
        while (entropy.size() != 0) {
            System.out.println("entropy : " + entropy);
            String[] to = entropy.get(0).trim().split(",");
            double entropyindex1 = Double.parseDouble(to[1]);
            double entropy1 = Double.parseDouble(to[0]);

            System.out.println(next.get(next.size() - 1));
            System.out.println(next.size());
            ArrayList<Double> gain2 = getGainForLeaf(entropy1, next);
            System.out.println("gain2 : " + gain2);
//        System.out.println(gain2.size());
            System.out.println("index 2 " + getMaxIndex(gain2));
//
            String[] tokenIndex2 = getMaxIndex(gain2).trim().split(",");
            double index2 = Double.parseDouble(tokenIndex2[1]);
            double max2 = Double.parseDouble(tokenIndex2[0]);
            indexes.add(index2 + i);
            entropy = getEntropyForAllSingleClassField(next, index2);
            if (entropy.size() != 0)
                next = convert(getNextLevelData(index2, next));
            i++;
        }
        System.out.println("indexes : " + indexes);
        System.out.println("acc : " + accuracy);
        System.out.println("leaf index : " + leafIndex);

    }

    public void getTestandTrain() {
        double size = intialData.size() * 0.70;
        for (int i = 0; i < (int) size; i++) {
            training.add(intialData.get(i));
        }
        for (int i = training.size(); i < intialData.size(); i++) {
            test.add(intialData.get(i));
        }

    }

    public void cal_test() {
        ArrayList<Double> gain = getGain(peEntropyTest(), testConvert);
        System.out.println(gain);
        System.out.println(gain.size());
        System.out.println(getMaxIndex(gain));

        String[] tokenIndex = getMaxIndex(gain).trim().split(",");
        double index1 = Double.parseDouble(tokenIndex[1]);
        testindexes.add(index1);
        /*
start root 2
 */


        ArrayList<String> entropy = new ArrayList<>();


/*
start root 2
 */
        entropy = getEntropyForAllSingleClassField(testConvert, index1);
        ArrayList<ArrayList<String>> next = convert(getNextLevelDataForTest(index1, testConvert));
        while (entropy.size() != 0) {
            System.out.println("entropy : " + entropy);
            String[] to = entropy.get(0).trim().split(",");
            double entropyindex1 = Double.parseDouble(to[1]);
            double entropy1 = Double.parseDouble(to[0]);

            System.out.println(next.get(next.size() - 1));
            System.out.println(next.size());
            ArrayList<Double> gain2 = getGainForLeaf(entropy1, next);
            System.out.println("gain : " + gain2);

            System.out.println("index 2 " + getMaxIndex(gain2));

            String[] tokenIndex2 = getMaxIndex(gain2).trim().split(",");
            double index2 = Double.parseDouble(tokenIndex2[1]);
            double max2 = Double.parseDouble(tokenIndex2[0]);
            testindexes.add(index2);
            entropy = getEntropyForAllSingleClassField(next, index2);

            if (entropy.size() != 0)
                next = convert(getNextLevelDataForTest(index2, next));
        }
        System.out.println("indexes : " + testindexes);
        System.out.println("Accuracy : " + accuracyTest);

    }

    public void buildTree() {
        int l = 1;
        for (int i = 0; i < indexes.size(); i++) {

            for (int j = 0; j < dataconvert.size(); j++) {
                if ((double) j == indexes.get(i)) {
                    LinkedHashSet<String> hashSet = new LinkedHashSet<>(dataconvert.get(j));
                    ArrayList<String> list = new ArrayList<>(hashSet);
                    System.out.println(l + "\n|\n|\n|\n----->" + list);
                    if (i != indexes.size() - 1) {
                        String[] to = leafIndex.get(i).trim().split(",");
                        System.out.println(to[1]);
                    }
                    l++;
                }
            }
        }

    }
}
