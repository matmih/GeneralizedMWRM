/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redescriptionmining;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.javatuples.Triplet;
import static redescriptionmining.SettingsReader.ENCODING;
/**
 *
 * @author Matej Mihelcic
 * @institution Rudjer Boskovic Institute Zagreb, Croatia
 * @mail matmih1@gmail.com
 * @description class containing abstract functions
 */
public class NHMCDistanceMatrix {
    int distanceMatrix[][];
    int numElem=0;
    HashMap<String,Double> m_distancesS = new HashMap<>();
    HashMap<String,Double> m_distancesN = new HashMap<>();
    HashMap<Integer,HashSet<Integer>> connectivity = new HashMap<>();
    HashMap<Integer,HashMap<Integer,ArrayList<Triplet<Integer,Integer,Double>>>> connectivityMultiplex=new HashMap<Integer,HashMap<Integer,ArrayList<Triplet<Integer,Integer,Double>>>>();
     int networkType=0, numLayers=1, inputType=0;
    NHMCDistanceMatrix(int numElements, ApplicationSettings appset){
        distanceMatrix=new int[numElements][numElements];
        numElem=numElements;
        
        for(int i=0;i<numElements;i++)
            for(int j=0;j<numElements;j++)
                distanceMatrix[i][j]=appset.maxDistance;
        
        for(int i=0;i<numElements;i++)
            distanceMatrix[i][i]=0;
        
    }
    
    void computeDistanceMatrix(RuleReader rr, Mappings map, int MaxDistance, int numElements){//not available
      
    }
    
    void computeDistanceMatrix(ArrayList<Redescription> redescriptions, Mappings map, int MaxDistance, int numElements, int[] oldRindex){//not available
      
    }
    
    void loadDistance(File input, Mappings map){
        
        try {
      Path path =Paths.get(input.getAbsolutePath());
      System.out.println("Path: "+input.getAbsolutePath());
      BufferedReader reader;
      String file="";
      reader = Files.newBufferedReader(path,ENCODING);
      String line = null;
      distanceMatrix=new int[map.exampleId.keySet().size()][map.exampleId.keySet().size()];
      int rowInd=0;
      while ((line = reader.readLine()) != null) {
               String tmp[]=line.split(" ");
               for(int j=0;j<tmp.length;j++)
                   distanceMatrix[rowInd][j]=Integer.parseInt(tmp[j].trim());
               rowInd++;
        
    }
      reader.close();
         }
         catch(IOException io){
             io.printStackTrace();
         }
    }
    
    void reset(ApplicationSettings appset){
        
        for(int i=0;i<numElem;i++)
            for(int j=0;j<numElem;j++)
                distanceMatrix[i][j]=appset.maxDistance;
        
        for(int i=0;i<numElem;i++)
            distanceMatrix[i][i]=0;
    }
    
    void writeToFile(File output, Mappings map, ApplicationSettings appset){
  
      
           try{
         PrintWriter out = new PrintWriter(output.getAbsolutePath());
         for(int i=0;i<numElem;i++)
             for(int j=0;j<numElem;j++){
                 if(distanceMatrix[i][j]!=appset.maxDistance)
          out.write(i+","+j+","+distanceMatrix[i][j]+"\n");
                 else continue;
             }
         out.close();
         }
         catch(FileNotFoundException ex){
             ex.printStackTrace();
         }
    }
    
    void resetFile(File output){
        try{
         PrintWriter out = new PrintWriter(output.getAbsolutePath());
         out.close();
         }
         catch(FileNotFoundException ex){
             ex.printStackTrace();
         }
    }
    
}

