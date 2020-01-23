package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private TextArea result;

    @FXML
    private Label accLab;

    @FXML
    private Button clear;

    @FXML
    private TextArea draw;

    @FXML
    private Button run;

    @FXML
    private Button drawBtn;
    DL_Cal dl = new DL_Cal();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        run.setOnAction(e -> {
            try {
                dl.readFile();
                ArrayList<String> entropy = new ArrayList<>();

                ArrayList<Double> gain = dl.getGain(dl.peEntropy(), dl.trainingConvert);



                String[] tokenIndex = dl.getMaxIndex(gain).trim().split(",");
                double index1 = Double.parseDouble(tokenIndex[1]);
                double max = Double.parseDouble(tokenIndex[0]);
                dl.indexes.add(index1);


                /*
                start root 2
                */

                entropy = dl.getEntropyForAllSingleClassField(dl.trainingConvert, index1);
                ArrayList<ArrayList<String>> next = dl.convert(dl.getNextLevelData(index1, dl.trainingConvert));
                int index =(int) index1;
                LinkedHashSet<String> hashSet = new LinkedHashSet<>(dl.trainingConvert.get(index));
                ArrayList<String> listWithoutDuplicates = new ArrayList<>(hashSet);

                int oo1 = 0 ;
                for (int j = 0; j < listWithoutDuplicates.size(); j++) {
                    String[] token =dl.forProb.get(0).get(j).trim().split(",");
                    double e1 = Double.parseDouble(token[0].trim());
                    double p = Double.parseDouble(token[1].trim());
                    if (e1 == 0 && p != 0) {
                        result.appendText(" if class = " + dl.classname.get(index) + " and " + dl.classname.get(index) + " is " + listWithoutDuplicates.get(j) + " then mashrom is  poisonous  ");
                        result.appendText("\n\n");
                    }  if (p == 0 && e1 != 0) {

                        result.appendText(" if class = " + dl.classname.get(index) + " and " + dl.classname.get(index) + " is " + listWithoutDuplicates.get(j) + " then mashrom is edible");
                        result.appendText("\n \n");
                    }       if (e1 != 0 && p != 0) {

                         oo1 = j ;
                        j++;
                    }

                }
                result.appendText(" if class = " + dl.classname.get(index) + " and " + dl.classname.get(index) + " is " + listWithoutDuplicates.get(oo1) + " then : ");
                result.appendText("\n \n");
                int k = 1;
                while (entropy.size() != 0) {
                    String[] to = entropy.get(0).trim().split(",");
                    double entropyindex1 = Double.parseDouble(to[1]);
                    double entropy1 = Double.parseDouble(to[0]);


                    ArrayList<Double> gain2 = dl.getGainForLeaf(entropy1, next);



                    String[] tokenIndex2 = dl.getMaxIndex(gain2).trim().split(",");
                    double index2 = Double.parseDouble(tokenIndex2[1]);
                    double max2 = Double.parseDouble(tokenIndex2[0]);
                    dl.indexes.add(index2+k);

                    int index3 =(int) index2;
                    LinkedHashSet<String> hashSet1 = new LinkedHashSet<>(next.get(index3));
                    ArrayList<String> listWithoutDuplicates1 = new ArrayList<>(hashSet1);
                    System.out.println("list : "+listWithoutDuplicates1);



                    entropy = dl.getEntropyForAllSingleClassField(next, index2);
                    if (entropy.size() != 0)
                        next = dl.convert(dl.getNextLevelData(index2, next));
                    System.out.println("prop size : "+dl.forProb);
                    int oo=0 ;
                    for (int j = 0; j < listWithoutDuplicates1.size(); j++) {
                        String[] token = dl.forProb.get(k).get(j).trim().split(",");
                        double e1 = Double.parseDouble(token[0].trim());
                        double p = Double.parseDouble(token[1].trim());

                        if (e1 == 0 && p != 0) {
                            result.appendText(" if class = " + dl.classname.get(index3 + k) + " and " + dl.classname.get(index3 + k) + " is " + listWithoutDuplicates1.get(j) + " then mashrom is  poisonous  ");
                            result.appendText("\n");
                        }
                        if (p == 0 && e1 != 0) {
                            result.appendText(" if class = " + dl.classname.get(index3 + k) + " and " + dl.classname.get(index3 + k) + " is " + listWithoutDuplicates1.get(j) + " then mashrom is edible");
                            result.appendText("\n");

                        }
                        if (e1 != 0 && p != 0) {
                          oo=j;
                            j++;
                        }
                    }
                    result.appendText(" if class = " + dl.classname.get(index3 + k) + " and " + dl.classname.get(index3 + k) + " is " + listWithoutDuplicates1.get(oo) + " then  ");
                    result.appendText("\n \n");
                    k++;
                }
                dl.cal_test();

               accLab.setText(dl.accuracyTest.get(0) + "");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        drawBtn.setOnAction(e->{
            int l  =1;
            for (int i = 0; i < dl.indexes.size(); i++) {

                for (int j = 0; j <  dl.dataconvert.size(); j++) {
                    if ((double) j ==  dl.indexes.get(i)) {
                        LinkedHashSet<String> hashSet = new LinkedHashSet<>( dl.dataconvert.get(j));
                        ArrayList<String> list = new ArrayList<>(hashSet);
                       draw.appendText(l+"\n|\n|\n|\n|----->" + list);
                       draw.appendText("\n");
                        if (i!= dl.indexes.size()-1) {
                            String[] to =  dl.leafIndex.get(i).trim().split(",");
                            draw.appendText(to[1]);
                            draw.appendText("\n");
                        }
                        l++;
                    }
                }
            }

        });
        clear.setOnAction(e->{
            draw.clear();
            result.clear();
        });

    }
}
