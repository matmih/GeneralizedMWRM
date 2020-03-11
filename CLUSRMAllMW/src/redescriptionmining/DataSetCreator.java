/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redescriptionmining;

import clus.data.io.ARFFFile;
import clus.data.io.ClusReader;
import clus.data.io.ClusView;
import clus.data.rows.DataTuple;
import clus.data.rows.RowData;
import clus.data.type.ClusAttrType;
import clus.data.type.ClusSchema;
import clus.data.type.NominalAttrType;
import clus.data.type.NumericAttrType;
import clus.main.Settings;
import clus.util.ClusException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 *
 * @author Matej Mihelcic
 * @institution Rudjer Boskovic Institute / Department of Mathematics, University of Zagreb, Zagreb, Croatia
 * @mail matmih@math.hr
 * @description class containing functions needed to create datasets used during redescription mining
 */
public class DataSetCreator {
    public String DataPath;
    public int numExamples;
    public ClusReader cread=null;
    Settings cset;   
    RowData data=null;
    ClusSchema schema=null;
    ArrayList<Integer> W2indexs=new ArrayList<>();

    public DataSetCreator(){
        
    }

    
    public ArrayList<DataSetCreator> createSplit(double percentage){
        
        DataSetCreator train=new DataSetCreator();
        DataSetCreator test=new DataSetCreator();
        
        train.W2indexs=this.W2indexs;
        test.W2indexs=this.W2indexs;
        
        train.DataPath=this.DataPath;
        test.DataPath=this.DataPath;
        
        train.W2indexs=new ArrayList<>();
        train.W2indexs.addAll(this.W2indexs);
        
        test.W2indexs=new ArrayList<>();
        test.W2indexs.addAll(this.W2indexs);
        
        try{
            train.cread=new ClusReader(train.DataPath,this.cset);
            test.cread=new ClusReader(test.DataPath,this.cset);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
        train.cset = new Settings();
        test.cset = new Settings();
 
        train.schema = this.schema;
        test.schema = this.schema;
         train.data=new RowData(schema);
         test.data=new RowData(schema);
        
        RowData datTrain = null;
        RowData datTest = null;
        
        double a=this.numExamples*percentage;
        int numExTrain = (int)a;
        int numExTest = this.numExamples - numExTrain;
        
        Random rand=new Random();
        
        ArrayList<DataTuple> dataList=data.toArrayList();
         ArrayList<DataTuple> dataTrainList=new ArrayList<>();
         
          ArrayList<DataTuple> dataTestList=new ArrayList<>();
        
         HashSet<Integer> indexes=new HashSet<>();
         HashSet<Integer> indexesTest=new HashSet<>();
         
         int count=0;
         
         while(count<numExTrain){
             
             int randomNum = rand.nextInt((dataList.size() - 0) + 0) + 0;
             
             
             if(!indexes.contains(randomNum)){
                 count++;
                 indexes.add(randomNum);
             }
             
         }
         
         for(int i=0;i<dataList.size();i++)
             if(!indexes.contains(i))
                    indexesTest.add(i);
         
         for(int i:indexes){
             dataTrainList.add(dataList.get(i));
         }
         
         for(int i:indexesTest){
             dataTestList.add(dataList.get(i));
         }
         
         
         train.data.setFromList(dataTrainList);
         train.numExamples=numExTrain;
         
         test.data.setFromList(dataTestList);
         test.numExamples=numExTest;
         
         ArrayList<DataSetCreator> res = new ArrayList<>();
         res.add(train); res.add(test);
         
         System.out.println("Num examples train: "+dataTrainList.size());
         System.out.println("Num examples test: "+dataTestList.size());
         
         return res; 
    }
    
    public DataSetCreator(ArrayList<String> inputs, String outFolder, ApplicationSettings appset ){
        this.DataPath=inputs.get(0);
        ArrayList<DataSetCreator> dats=new ArrayList<>();
        for(int i=1;i<inputs.size();i++){
            DataSetCreator dt=new DataSetCreator(inputs.get(i));
            dats.add(dt);
        }
            
        cset=new Settings();
        String name=outFolder;

       if(appset.system.equals("windows"))
            name+="\\Jinput.arff";
        else 
            name+="/Jinput.arff";

        try{
                  this.readDataset();
        }
        catch(Exception e){
                    e.printStackTrace();
                }
        
         W2indexs.add(this.schema.getNbAttributes()+1);
         int curr = W2indexs.get(0);
        for(int i=0;i<dats.size();i++){
                try{
                  dats.get(i).readDataset();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                if((i+1)<dats.size()){
                 W2indexs.add(curr+dats.get(i).schema.getNbAttributes()-1);
                 curr = curr+dats.get(i).schema.getNbAttributes()-1;
                }
        }

         ArrayList<DataTuple> dataList=data.toArrayList();
         int lastDouble=schema.getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length;
         System.out.println("lastDouble: "+lastDouble);
         int lastCat=schema.getNominalAttrUse(ClusAttrType.ATTR_USE_ALL).length;

         int shCount=0, nhCount=0;
         for(int i=0;i<dats.size();i++){
             shCount+=dats.get(i).schema.getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length;
             nhCount+=dats.get(i).schema.getNominalAttrUse(ClusAttrType.ATTR_USE_ALL).length;
         }
         
         System.out.println("Number of numeric attributes in remaining datasets: "+shCount);
         
                for(int j=0;j<data.getNbRows();j++){
                    int l=data.getSchema().getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length+shCount;
                    double arow[]=null;
                    int carow[]=null;
                    int l1=data.getSchema().getNominalAttrUse(ClusAttrType.ATTR_USE_ALL).length+nhCount;
                    
                    if(l1>0)
                        carow=new int[l1];
                    
                    if(l>0)
                    arow=new double[l];
                    if(data.getSchema().getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length>0){
                        for(int k=0;k<dataList.get(j).m_Doubles.length;k++){
                            arow[k]=dataList.get(j).m_Doubles[k];
                         }
                    }
                    
                   if(data.getSchema().getNominalAttrUse(ClusAttrType.ATTR_USE_ALL).length>0){
                        for(int k=0;k<dataList.get(j).m_Ints.length;k++){
                             carow[k]=dataList.get(j).m_Ints[k];
                        }
                  }
                    
                   int ind=lastDouble;
                   for(int k1=0;k1<dats.size();k1++){
                        DataSetCreator d2=dats.get(k1);
                        ArrayList<DataTuple> dataList2=d2.data.toArrayList();
                    if(d2.data.getSchema().getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length>0){
                         for(int k=0;k<dataList2.get(j).m_Doubles.length;k++){
                             arow[k+ind]=dataList2.get(j).m_Doubles[k];
                             }
                         }
                    
                    if(d2.data.getSchema().getNominalAttrUse(ClusAttrType.ATTR_USE_ALL).length>0){
                         for(int k=0;k<dataList2.get(j).m_Ints.length;k++){
                             carow[k+lastCat]=dataList2.get(j).m_Ints[k];
                              }
                          }
                    ind+=d2.schema.getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length;
                   }
                 dataList.get(j).m_Doubles=arow;
                 dataList.get(j).m_Ints=carow;
             }
    
     if(appset.useNC.get(0)==true){
         ClusAttrType cat=schema.getAttrType(schema.getNbAttributes()-1);
         ClusAttrType copy=cat.cloneType();
         schema.addAttrType(copy);
         for(int i=schema.getNbAttributes()-2;i>=2;i--){
             ClusAttrType copy1=schema.getAttrType(i-1).cloneType();
             schema.setAttrType(copy1, i);
         }
         
         ClusAttrType copy1=new NumericAttrType("GIS");
             schema.setAttrType(copy1, 1);
         
          schema.getAttrType(1).setName("GIS");
         
          int numD=-1;
          double count=0.0;
        for(int j=0;j<data.getNbRows();j++){
            double row[]=null;
            if(dataList.get(j).m_Doubles!=null){
                row=new double[dataList.get(j).m_Doubles.length+1];
                numD=dataList.get(j).m_Doubles.length;
            }
            else
                row=new double[1];
         row[0]=count;
         count++;
         
         if(numD>0){
         row[1]=dataList.get(j).m_Doubles[0];
         for(int i=1;i<dataList.get(j).m_Doubles.length;i++)
             row[i+1]=dataList.get(j).m_Doubles[i];
         }
         
         dataList.get(j).m_Doubles=row;
        }
         
        for(int i=0;i<W2indexs.size();i++)
            W2indexs.set(i, W2indexs.get(i)+1);
        
     }           
                
     for(int k=0;k<dats.size();k++){  
         DataSetCreator d2=dats.get(k);
       for(int i=1;i<d2.schema.getNbAttributes();i++){
           ClusAttrType attr = d2.schema.getAttrType(i);
	   ClusAttrType copy = attr.cloneType();
            schema.addAttrType(copy);
        }
     }
     
       try{
              schema.addIndices(0);
       }
       catch(ClusException e){
           e.printStackTrace();
       }
       
                 data.setFromList(dataList);
                 data.setSchema(schema);
                 schema.setSettings(cset);

        try{
                 writeArff(name, data);
            }
                 catch(Exception e){
                     e.printStackTrace();
                 }
    }
    
    public DataSetCreator(String input1, String input2, String outFolder ){
 
        DataSetCreator d2=new DataSetCreator(input2);
        this.DataPath=input1;
        cset=new Settings();
        String name=outFolder;

        name+="\\Jinput.arff";

        try{
        this.readDataset();
        d2.readDataset();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        W2indexs.add(this.schema.getNbAttributes()+1);

        cset=new Settings();

         ArrayList<DataTuple> dataList2=d2.data.toArrayList();
         ArrayList<DataTuple> dataList=data.toArrayList();
         int lastDouble=schema.getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length;
         System.out.println("lastDouble: "+lastDouble);
         int lastCat=schema.getNominalAttrUse(ClusAttrType.ATTR_USE_ALL).length;
		 
                for(int j=0;j<data.getNbRows();j++){
                    int l=data.getSchema().getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length+d2.data.getSchema().getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length;
                    double arow[]=null;
                    int carow[]=null;
                    int l1=data.getSchema().getNominalAttrUse(ClusAttrType.ATTR_USE_ALL).length+d2.data.getSchema().getNominalAttrUse(ClusAttrType.ATTR_USE_ALL).length;
                    
                    if(l1>0)
                        carow=new int[l1];
                    
                    if(l>0)
                    arow=new double[l];
                    if(data.getSchema().getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length>0){
                        for(int k=0;k<dataList.get(j).m_Doubles.length;k++){
                            arow[k]=dataList.get(j).m_Doubles[k];
                         }
                    }
                    
                   if(data.getSchema().getNominalAttrUse(ClusAttrType.ATTR_USE_ALL).length>0){
                        for(int k=0;k<dataList.get(j).m_Ints.length;k++){
                             carow[k]=dataList.get(j).m_Ints[k];
                        }
                  }
                    
                    if(d2.data.getSchema().getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length>0){
                         for(int k=0;k<dataList2.get(j).m_Doubles.length;k++){
                             arow[k+lastDouble]=dataList2.get(j).m_Doubles[k];
                  }
                    }
                    
                    if(d2.data.getSchema().getNominalAttrUse(ClusAttrType.ATTR_USE_ALL).length>0){
                         for(int k=0;k<dataList2.get(j).m_Ints.length;k++){
                             carow[k+lastCat]=dataList2.get(j).m_Ints[k];
                  }
                    }

                 dataList.get(j).m_Doubles=arow;
                 dataList.get(j).m_Ints=carow;
               }
                
       for(int i=1;i<d2.schema.getNbAttributes();i++){
           ClusAttrType attr = d2.schema.getAttrType(i);
	   ClusAttrType copy = attr.cloneType();
            schema.addAttrType(copy);
        }
     
       try{
              schema.addIndices(0);
       }
       catch(ClusException e){
           e.printStackTrace();
       }
       
                 data.setFromList(dataList);
                 data.setSchema(schema);
                 schema.setSettings(cset);

        try{
                 writeArff(name, data);
            }
                 catch(Exception e){
                     e.printStackTrace();
                 }
    }
    
    void initialClustering(String outFolder, ApplicationSettings appset){
          ArrayList<DataTuple> dataList=data.toArrayList();
          ArrayList<DataTuple> newTuples=new ArrayList<>();
        
         int lastDouble=schema.getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length;

         System.out.println("initial lastDouble: "+lastDouble);
         System.out.println("old attr size: "+schema.getNbAttributes());
         schema.addAttrType(new NumericAttrType("target"));
         schema.getAttrType(schema.getNbAttributes()-1).setArrayIndex(lastDouble++);
         System.out.println("new attr size: "+schema.getNbAttributes()); 
         
         int numShufflingStep=Math.min(500, Math.min((int)(0.7*data.getNbRows()),schema.getNbAttributes()));
         
         Random rand=new Random();
         
         System.out.println("Doing initial clustering...");
         System.out.println("data list size: "+dataList.size());
         System.out.println("Num shuffling steps: "+numShufflingStep);
         
         int numIt=0, step=dataList.size()/100;
         
          for(int j=0;j<dataList.size();j++){
              DataTuple tup=dataList.get(j).deepCloneTuple();
              String id=tup.m_Objects[0].toString().replace("\"", "");
              id="\""+id+"new-Init\"";
              tup.m_Objects[0]=id;
              
              for(int i=0;i<numShufflingStep;i++){
                    int ind=rand.nextInt(dataList.size());
                    DataTuple told=dataList.get(ind);
                    for(int k=0;k<((int)(Math.max(1.0,0.08*schema.getNbAttributes())));k++){
                           int toChange=0;
                           int toChange1=0;
                           
                       for(int nview=0;nview<W2indexs.size()-1;nview++){                     
                           if(nview==0)
                             toChange=rand.nextInt(W2indexs.get(0)-1);
                           else{
                               toChange1=rand.nextInt(W2indexs.get(nview)-1);
                               toChange=rand.nextInt(W2indexs.get(nview+1)-W2indexs.get(k)+1); 
                               toChange+=toChange1;
                           }

                        ClusAttrType t=schema.getAttrType(toChange);
                        if(t.getTypeName().contains("Numeric")){
                            int elemInd=t.getArrayIndex();
                            tup.m_Doubles[elemInd]=told.m_Doubles[elemInd];
                        }
                        else if(t.getTypeName().contains("Nominal")){
                            int elemInd=t.getArrayIndex();
                            tup.m_Ints[elemInd]=told.m_Ints[elemInd];
                        }
                       }
                       
                       toChange= rand.nextInt(schema.getNbAttributes()-1-W2indexs.get(W2indexs.size()-1)+1);
                       toChange1= rand.nextInt(W2indexs.get(W2indexs.size()-1)-1);
                       toChange+=toChange1;
                               
                        ClusAttrType t1=schema.getAttrType(toChange);
                        if(t1.getTypeName().contains("Numeric")){
                            int elemInd=t1.getArrayIndex();
                            if(elemInd==0 && appset.networkInit==true)
                                elemInd++;
                            if(elemInd<tup.m_Doubles.length)
                            tup.m_Doubles[elemInd]=told.m_Doubles[elemInd];
                            else{k--; continue;}
                        }
                        else if(t1.getTypeName().contains("Nominal")){
                            int elemInd=t1.getArrayIndex();
                            tup.m_Ints[elemInd]=told.m_Ints[elemInd];
                        }                   
                    }
              }
              
              newTuples.add(tup);
              
              numIt++;
        if(numIt%step==0)
                System.out.println((((double)numIt/dataList.size())*100)+"% completed...");
                if(numIt==dataList.size())
                    System.out.println("100% completed!");
              
          }
          
          for(int i=0;i<dataList.size();i++){
              double arow[];
              
              if(data.getSchema().getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length>0)
              arow=new double[dataList.get(i).m_Doubles.length+1];
              else
                  arow=new double[1];
              
              if(data.getSchema().getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length>0){
                 for(int j=0;j<dataList.get(i).m_Doubles.length;j++)
                    arow[j]=dataList.get(i).m_Doubles[j];
              }
             
              if(data.getSchema().getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length>0)
                arow[dataList.get(i).m_Doubles.length]=1.0;
              else
                  arow[0]=1.0;
              dataList.get(i).m_Doubles=arow;
          }
          
          for(int i=0;i<newTuples.size();i++){
              double arow[];
              
              if(data.getSchema().getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length>0)
                 arow=new double[newTuples.get(i).m_Doubles.length+1];
              else
                  arow=new double[1];
              
              if(data.getSchema().getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length>0){
                     for(int j=0;j<newTuples.get(i).m_Doubles.length;j++){
                         if(j==0 && appset.networkInit==true)
                             arow[j]=data.getNbRows()+i;
                         else
                            arow[j]=newTuples.get(i).m_Doubles[j];
                     }
              }
              if(data.getSchema().getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length>0)
                    arow[newTuples.get(i).m_Doubles.length]=0.0;
              else arow[0]=0.0;
              newTuples.get(i).m_Doubles=arow;
          }
          
          for(int i=0;i<newTuples.size();i++)
              dataList.add(newTuples.get(i));
        
           data.setFromList(dataList);
                 data.setSchema(schema);
                 schema.setSettings(cset);

                 String name=outFolder;
        
        if(appset.system.equals("windows"))
            name+="\\JinputInitial.arff";
        else 
            name+="/JinputInitial.arff";
                 
        try{
                 writeArff(name, data);
            }
                 catch(Exception e){
                     e.printStackTrace();
                 }       
    }
    
    
    void initialClusteringGen1(String outFolder, ApplicationSettings appset, int numChangedAttrs, Random r){
          ArrayList<DataTuple> dataList=data.toArrayList();
          ArrayList<DataTuple> newTuples=new ArrayList<>();
        
         int lastDouble=schema.getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length;

         System.out.println("initial lastDouble: "+lastDouble);
         System.out.println("old attr size: "+schema.getNbAttributes());
         schema.addAttrType(new NumericAttrType("target"));
         schema.getAttrType(schema.getNbAttributes()-1).setArrayIndex(lastDouble++);
         System.out.println("new attr size: "+schema.getNbAttributes()); 
         
         int numShufflingStep=Math.min(500, Math.min((int)(0.7*data.getNbRows()),schema.getNbAttributes()));
         System.out.println("Doing initial clustering...");
         System.out.println("data list size: "+dataList.size());
         System.out.println("Num shuffling steps: "+numShufflingStep);
         
         int numIt=0, step=dataList.size()/100;
         if(step==0)
             step=1;
         
          for(int j=0;j<dataList.size();j++){
              DataTuple tup=dataList.get(j).deepCloneTuple();
              String id=tup.m_Objects[0].toString().replace("\"", "");
              id="\""+id+"new-Init\"";
              tup.m_Objects[0]=id;
              
              int[] array = new int[dataList.size()];
              
              for(int i=0;i<dataList.size();i++)
                  array[i]=i;
         
              for(int i = 0; i < dataList.size(); i++){
                      int ran = i + r.nextInt (dataList.size()-i);

                       int temp = array[i];
                        array[i] = array[ran];
                        array[ran] = temp;
                }   
              
              int numAttrs=schema.getNbAttributes()-2;

              for(int i=0;i<numAttrs;i++){
                   ClusAttrType t=schema.getAttrType(i+1);
                        if(t.getTypeName().contains("Numeric")){
                            int elemInd=t.getArrayIndex();
                            tup.m_Doubles[elemInd]=dataList.get(array[i%dataList.size()]).m_Doubles[elemInd];
                        }
                        else if(t.getTypeName().contains("Nominal")){
                            int elemInd=t.getArrayIndex();
                            tup.m_Ints[elemInd]=dataList.get(array[i%dataList.size()]).m_Ints[elemInd];
                        }
              }
             
              newTuples.add(tup);
              
              numIt++;
        if(numIt%step==0)
                System.out.println((((double)numIt/dataList.size())*100)+"% completed...");
                if(numIt==dataList.size())
                    System.out.println("100% completed!");   
          }
          
          for(int i=0;i<dataList.size();i++){
              double arow[];
              
              if(data.getSchema().getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length>0)
              arow=new double[dataList.get(i).m_Doubles.length+1];
              else
                  arow=new double[1];
    
              if(data.getSchema().getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length>0){
                 for(int j=0;j<dataList.get(i).m_Doubles.length;j++)
                    arow[j]=dataList.get(i).m_Doubles[j];
              }
             
              if(data.getSchema().getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length>0)
                arow[dataList.get(i).m_Doubles.length]=1.0;
              else
                  arow[0]=1.0;
              dataList.get(i).m_Doubles=arow;
          }
          
          for(int i=0;i<newTuples.size();i++){
              double arow[];
              
              if(data.getSchema().getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length>0)
                 arow=new double[newTuples.get(i).m_Doubles.length+1];
              else
                  arow=new double[1];
              
              if(data.getSchema().getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length>0){
                     for(int j=0;j<newTuples.get(i).m_Doubles.length;j++){
                         if(j==0 && appset.networkInit==true)
                             arow[j]=data.getNbRows()+i;
                         else
                            arow[j]=newTuples.get(i).m_Doubles[j];
                     }
              }
              if(data.getSchema().getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length>0)
                    arow[newTuples.get(i).m_Doubles.length]=0.0;
              else arow[0]=0.0;
              newTuples.get(i).m_Doubles=arow;
          }
          
          for(int i=0;i<newTuples.size();i++)
              dataList.add(newTuples.get(i));
        
           data.setFromList(dataList);
                 data.setSchema(schema);
                 schema.setSettings(cset);

                 String name=outFolder;

        if(appset.system.equals("windows"))
            name+="\\JinputInitial.arff";
        else 
            name+="/JinputInitial.arff";
                 
        try{
                 writeArff(name, data);
            }
                 catch(Exception e){
                     e.printStackTrace();
                 }       
    }
    
    
    void initialClusteringGen(String outFolder, ApplicationSettings appset, int numChangedAttrs){
          ArrayList<DataTuple> dataList=data.toArrayList();
          ArrayList<DataTuple> newTuples=new ArrayList<>();
        
         int lastDouble=schema.getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length;

         System.out.println("initial lastDouble: "+lastDouble);
         System.out.println("old attr size: "+schema.getNbAttributes());
         schema.addAttrType(new NumericAttrType("target"));
         schema.getAttrType(schema.getNbAttributes()-1).setArrayIndex(lastDouble++);
         System.out.println("new attr size: "+schema.getNbAttributes()); 
         
         int numShufflingStep=Math.min(500, Math.min((int)(0.7*data.getNbRows()),schema.getNbAttributes()));
         System.out.println("Doing initial clustering...");
         System.out.println("data list size: "+dataList.size());
         System.out.println("Num shuffling steps: "+numShufflingStep);
         
         int numIt=0, step=dataList.size()/100;
         Random r=new Random();
         
          for(int j=0;j<dataList.size();j++){
              DataTuple tup=dataList.get(j).deepCloneTuple();
              String id=tup.m_Objects[0].toString().replace("\"", "");
              id="\""+id+"new-Init\"";
              tup.m_Objects[0]=id;
              
              int[] array = new int[dataList.size()];
              
              for(int i=0;i<dataList.size();i++)
                  array[i]=i;
         
              for(int i = 0; i < dataList.size(); i++){
                      int ran = i + r.nextInt (dataList.size()-i);

                       int temp = array[i];
                        array[i] = array[ran];
                        array[ran] = temp;
                }   
              
              int numAttrs=schema.getNbAttributes()-2;
              for(int i=0;i<numAttrs;i++){
                   ClusAttrType t=schema.getAttrType(i+1);
                        if(t.getTypeName().contains("Numeric")){
                            int elemInd=t.getArrayIndex();
                            tup.m_Doubles[elemInd]=dataList.get(array[i%dataList.size()]).m_Doubles[elemInd];
                        }
                        else if(t.getTypeName().contains("Nominal")){
                            int elemInd=t.getArrayIndex();
                            tup.m_Ints[elemInd]=dataList.get(array[i%dataList.size()]).m_Ints[elemInd];
                        }
              }
             
              newTuples.add(tup);
              
              numIt++;
        if(numIt%step==0)
                System.out.println((((double)numIt/dataList.size())*100)+"% completed...");
                if(numIt==dataList.size())
                    System.out.println("100% completed!");   
          }
          
          for(int i=0;i<dataList.size();i++){
              double arow[];
              
              if(data.getSchema().getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length>0)
              arow=new double[dataList.get(i).m_Doubles.length+1];
              else
                  arow=new double[1];
    
              if(data.getSchema().getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length>0){
                 for(int j=0;j<dataList.get(i).m_Doubles.length;j++)
                    arow[j]=dataList.get(i).m_Doubles[j];
              }
             
              if(data.getSchema().getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length>0)
                arow[dataList.get(i).m_Doubles.length]=1.0;
              else
                  arow[0]=1.0;
              dataList.get(i).m_Doubles=arow;
          }
          
          for(int i=0;i<newTuples.size();i++){
              double arow[];
              
              if(data.getSchema().getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length>0)
                 arow=new double[newTuples.get(i).m_Doubles.length+1];
              else
                  arow=new double[1];
              
              if(data.getSchema().getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length>0){
                     for(int j=0;j<newTuples.get(i).m_Doubles.length;j++){
                         if(j==0 && appset.networkInit==true)
                             arow[j]=data.getNbRows()+i;
                         else
                            arow[j]=newTuples.get(i).m_Doubles[j];
                     }
              }
              if(data.getSchema().getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length>0)
                    arow[newTuples.get(i).m_Doubles.length]=0.0;
              else arow[0]=0.0;
              newTuples.get(i).m_Doubles=arow;
          }
          
          for(int i=0;i<newTuples.size();i++)
              dataList.add(newTuples.get(i));
        
           data.setFromList(dataList);
                 data.setSchema(schema);
                 schema.setSettings(cset);

                 String name=outFolder;

         if(appset.system.equals("windows"))
            name+="\\JinputInitial.arff";
        else 
            name+="/JinputInitial.arff";
            
        try{
                 writeArff(name, data);
            }
                 catch(Exception e){
                     e.printStackTrace();
                 }       
    }
    
    
    void initialClusteringCategorical(String outFolder){
          ArrayList<DataTuple> dataList=data.toArrayList();
          ArrayList<DataTuple> newTuples=new ArrayList<>();
        
         int lastDouble=schema.getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length;

         System.out.println("initial lastDouble: "+lastDouble);
         System.out.println("old attr size: "+schema.getNbAttributes());
         schema.addAttrType(new NominalAttrType("target"));
         schema.getAttrType(schema.getNbAttributes()-1).setArrayIndex(lastDouble++);
         System.out.println("new attr size: "+schema.getNbAttributes()); 
         
         int numShufflingStep=Math.min(500, Math.min((int)(0.7*data.getNbRows()),schema.getNbAttributes()));
         
         Random rand=new Random();
         
         System.out.println("Doing initial clustering...");
         System.out.println("data list size: "+dataList.size());
         System.out.println("Num shuffling steps: "+numShufflingStep);
         
         int numIt=0, step=dataList.size()/100;
         
          for(int j=0;j<dataList.size();j++){
              DataTuple tup=dataList.get(j).deepCloneTuple();
              String id=tup.m_Objects[0].toString().replace("\"", "");
              id="\""+id+"new-Init\"";
              tup.m_Objects[0]=id;
              
              for(int i=0;i<numShufflingStep;i++){
                    int ind=rand.nextInt(dataList.size());
                    DataTuple told=dataList.get(ind);
                    for(int k=0;k<((int)(Math.max(1.0,0.08*schema.getNbAttributes())));k++){
                        int toChange=rand.nextInt(schema.getNbAttributes()-1);
                        ClusAttrType t=schema.getAttrType(toChange);

                        if(t.getTypeName().contains("Numeric")){
                            int elemInd=t.getArrayIndex();
                            tup.m_Doubles[elemInd]=told.m_Doubles[elemInd];
                        }
                        else if(t.getTypeName().contains("Nominal")){
                            int elemInd=t.getArrayIndex();
                            tup.m_Ints[elemInd]=told.m_Ints[elemInd];
                        }
                        
                    }
              }
              
              newTuples.add(tup);
              
              numIt++;
        if(numIt%step==0)
                System.out.println((((double)numIt/dataList.size())*100)+"% completed...");
                if(numIt==dataList.size())
                    System.out.println("100% completed!");
              
          }
          
          for(int i=0;i<dataList.size();i++){
              int arow[];
              
              if(data.getSchema().getNominalAttrUse(ClusAttrType.ATTR_USE_ALL).length>0)
              arow=new int[dataList.get(i).m_Ints.length+1];
              else
                  arow=new int[1];
              
              if(data.getSchema().getNominalAttrUse(ClusAttrType.ATTR_USE_ALL).length>0){
                 for(int j=0;j<dataList.get(i).m_Ints.length;j++)
                    arow[j]=dataList.get(i).m_Ints[j];
              }
             
              if(data.getSchema().getNominalAttrUse(ClusAttrType.ATTR_USE_ALL).length>0)
                arow[dataList.get(i).m_Ints.length]=0;
              else
                  arow[0]=1;
              dataList.get(i).m_Ints=arow;
          }
          
          for(int i=0;i<newTuples.size();i++){
              int arow[];
              
              if(data.getSchema().getNominalAttrUse(ClusAttrType.ATTR_USE_ALL).length>0)
                 arow=new int[newTuples.get(i).m_Ints.length+1];
              else
                  arow=new int[1];
              
              if(data.getSchema().getNominalAttrUse(ClusAttrType.ATTR_USE_ALL).length>0){
                     for(int j=0;j<newTuples.get(i).m_Ints.length;j++)
                         arow[j]=newTuples.get(i).m_Ints[j];
              }
              if(data.getSchema().getNominalAttrUse(ClusAttrType.ATTR_USE_ALL).length>0)
                    arow[newTuples.get(i).m_Ints.length]=1;
              else arow[0]=0;
              newTuples.get(i).m_Ints=arow;
          }
          
          for(int i=0;i<newTuples.size();i++)
              dataList.add(newTuples.get(i));
        
           data.setFromList(dataList);
                 data.setSchema(schema);
                 schema.setSettings(cset);

                 String name=outFolder;

        name+="\\JinputInitial.arff";
                 
        try{
                 writeArff(name, data);
            }
                 catch(Exception e){
                     e.printStackTrace();
                 }
         
    }

    public DataSetCreator(String path){
        DataPath=path;
        cset=new Settings(); 
    }
    
    HashSet<Integer> analyzeAttributes(double misPerc){
      HashSet<Integer> attrIndex=new HashSet<>();
      
      double missingPerc=0.0;
      int missing=0;
      ArrayList<DataTuple> dataList=data.toArrayList();
      
  
          for(int k=0;k<dataList.get(0).m_Doubles.length;k++){
              missing=0;   
              missingPerc=0.0;
              for(int j=0;j<data.getNbRows();j++){
                      if(dataList.get(j).m_Doubles[k]==Double.POSITIVE_INFINITY)
                          missing++;
          }
           
              missingPerc=(double)missing/data.getNbRows();
              if(missingPerc>=misPerc)
                  attrIndex.add(k+1);
      }
      
      return attrIndex;
    }
    
    public void removeAttributes(HashSet<Integer> attributeIndexes, String outFolder){
        
     ArrayList<DataTuple> dataList=data.toArrayList();
                RowData dat=data;
                ClusSchema sch=new ClusSchema(dat.getSchema().getRelationName());
               
                sch.addAttrType(schema.getAttrType(0));
                System.out.println("Number of doubles: "+dataList.get(0).m_Doubles.length);

                for(int i=1;i<schema.getNbAttributes()-1;i++)
                    if(!attributeIndexes.contains(i))
                        sch.addAttrType(schema.getAttrType(i+1));
               
                for(int j=0;j<data.getNbRows();j++){
                     double arow[]=new double[dataList.get(j).m_Doubles.length-attributeIndexes.size()];
                     int count=0;
                  for(int k=0;k<dataList.get(j).m_Doubles.length;k++){
                      if(!attributeIndexes.contains(k+1))
                       arow[count++]=dataList.get(j).m_Doubles[k];
                    }
                 dataList.get(j).m_Doubles=arow;
               }
                 dat.setFromList(dataList);
                 dat.setSchema(sch);
                 sch.setSettings(cset);  
                 
                 String out=outFolder+"\\JinputTmp.arff";
                 
                 try{
                 writeArff(out,data);
                 }
                 catch(Exception e){
                     e.printStackTrace();
                 }
    }
    
    public void readDataset()throws IOException{
     
        cread=new ClusReader(DataPath,cset); 
        ARFFFile a=new ARFFFile(cread);
        try{
		schema = a.read(cset);
		schema.initialize();
		ClusView view = schema.createNormalView();
		 data = view.readData(cread, schema);
                 schema=data.getSchema();
        }
        catch(Exception e){
           e.printStackTrace();
        } 
            numExamples=data.getNbRows();
           cread.close();
           
           System.out.println("Read dataset num doubles: "+schema.getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length);
    }
    
    
    public double getValue(int attId, int rowInd){
        double val=0.0;
        int dIndex=-1;
             
        dIndex=schema.getAttrType(attId+1).getArrayIndex();
        val=data.getTuple(rowInd).getDoubleVal(dIndex);
       
        return val;
    }
    
    public String getValueCategorical(int attId, int rowInd){
        String val="";
        int nIndex=-1;
        
        nIndex=schema.getAttrType(attId+1).getArrayIndex();
        
        NominalAttrType[] nom = schema.getNominalAttrUse(ClusAttrType.ATTR_USE_ALL);
        
        DataTuple tup=data.getTuple(rowInd);
        if(tup.m_Ints[nIndex]>=nom[nIndex].m_Values.length)
            return "?";
        val=nom[nIndex].m_Values[tup.m_Ints[nIndex]];
        return val;
    }
    
    public void modifyDataset(int numRules, RuleReader RR,Mappings map,String Output) throws IOException{
        
           int naex=numExamples;
           int nARules=numRules;
       
            ArrayList<DataTuple> dataList=data.toArrayList();

            System.out.println("New rule index: "+RR.newRuleIndex);
            System.out.println("Rule size: "+RR.rules.size());
            
            for(int i=RR.newRuleIndex;i<RR.rules.size();i++)
                if(RR.rules.get(i).elements.size()<=naex*0.2 && RR.rules.get(i).elements.size()>3)
            schema.addAttrType(new NumericAttrType("target"+(i+1-RR.newRuleIndex)));
            System.out.println("nARules: "+nARules);
            System.out.println("New number of attributes: "+schema.getNbAttributes());
         int count=0; 
           
        for (int j=0;j<data.getNbRows();j++){
            count=0;
        double arow[]=new double[schema.getNbAttributes()-1];
        for(int k=0;k<dataList.get(j).m_Doubles.length;k++){
            arow[k]=dataList.get(j).m_Doubles[k];
                    }
            for(int i=RR.newRuleIndex;i<RR.rules.size();i++)
                if(RR.rules.get(i).elements.size()<=naex*0.2 && RR.rules.get(i).elements.size()>3){
                    if(RR.rules.get(i).elements.contains(map.exampleId.get((String)dataList.get(j).m_Objects[0])))
                arow[dataList.get(j).m_Doubles.length+count]=1.0;
                    else
                         arow[dataList.get(j).m_Doubles.length+count]=0.0;
                    count++;
                }
            dataList.get(j).m_Doubles=arow;
        }
           
       data.setFromList(dataList);
       data.setSchema(schema);
       schema.setSettings(cset);
       
       try{
       writeArff(Output, data);
       }
       catch(Exception e){
           e.printStackTrace();
       }  
    }

       public void modifyDatasetCat(int numRules, double startPercentage, double endPercentage, int startIndex, int endIndex, int cuttof,int cuttofMax , RuleReader RR,String Output, Mappings map) throws IOException{

           int naex=numExamples;
           int nARules=numRules;
    
            ArrayList<DataTuple> dataList=data.toArrayList();

            System.out.println("New rule index: "+RR.newRuleIndex);
            System.out.println("Rule size: "+RR.rules.size());

            int lastDouble=schema.getNominalAttrUse(ClusAttrType.ATTR_USE_ALL).length;
            int prevDouble=schema.getNominalAttrUse(ClusAttrType.ATTR_USE_ALL).length;
            int inc=0;
            
            for(int i=startIndex;i<endIndex;i++)
                if(RR.rules.get(i).elements.size()<=naex*endPercentage && RR.rules.get(i).elements.size()>=naex*startPercentage && RR.rules.get(i).elements.size()>=cuttof && RR.rules.get(i).elements.size()<=cuttofMax){
            schema.addAttrType(new NominalAttrType("target"+(i+1-startIndex)));
            schema.getAttrType(schema.getNbAttributes()-1).setArrayIndex(lastDouble++);
            inc++;
                }
            System.out.println("nARules: "+nARules);
            System.out.println("New number of attributes: "+schema.getNbAttributes());
         int count=0;
         
         int l=schema.getNominalAttrUse(ClusAttrType.ATTR_USE_ALL).length;
         System.out.println("New size: "+(l+inc));
         System.out.println("l: "+l);
         System.out.println("inc: "+inc);
         System.out.println("l+inc: "+(l+inc));
         System.out.println("Old size: "+prevDouble);
         
        for (int j=0;j<data.getNbRows();j++){
            count=0;
         
        int arow[]=new int[l+inc];
        if(prevDouble>0){
            for(int k=0;k<dataList.get(j).m_Ints.length;k++){
                arow[k]=dataList.get(j).m_Ints[k];
                    }
        }

            for(int i=startIndex;i<endIndex;i++)
                if(RR.rules.get(i).elements.size()<=naex*endPercentage && RR.rules.get(i).elements.size()>=naex*startPercentage && RR.rules.get(i).elements.size()>=cuttof && RR.rules.get(i).elements.size()<=cuttofMax){
                    if(RR.rules.get(i).elements.contains(map.exampleId.get((String)dataList.get(j).m_Objects[0])))
                arow[l+count]=0;
                    else
                         arow[l+count]=1;
                    count++;
                }
            dataList.get(j).m_Ints=arow;
        }

       data.setFromList(dataList);
       data.setSchema(schema);
       schema.setSettings(cset);

       try{
       writeArff(Output, data);
       }
       catch(Exception e){
           e.printStackTrace();
       }
    }
       
       public void modifyDatasetCat(int numRules, double startPercentage, double endPercentage, int startIndex, int endIndex, int cuttof,int cuttofMax , ArrayList<Redescription> redescriptions,String Output, Mappings map) throws IOException{

           int naex=numExamples;
           int nARules=numRules;
      
            ArrayList<DataTuple> dataList=data.toArrayList();

            int lastDouble=schema.getNominalAttrUse(ClusAttrType.ATTR_USE_ALL).length;
            int prevDouble=schema.getNominalAttrUse(ClusAttrType.ATTR_USE_ALL).length;
            int inc=0;
            
            for(int i=startIndex;i<endIndex;i++)
                if(redescriptions.get(i).elements.size()<=naex*endPercentage && redescriptions.get(i).elements.size()>=naex*startPercentage && redescriptions.get(i).elements.size()>=cuttof && redescriptions.get(i).elements.size()<=cuttofMax){
            schema.addAttrType(new NominalAttrType("target"+(i+1-startIndex)));
            schema.getAttrType(schema.getNbAttributes()-1).setArrayIndex(lastDouble++);
            inc++;
                }
            System.out.println("nARules: "+nARules);
            System.out.println("New number of attributes: "+schema.getNbAttributes());
         int count=0;
         
         int l=schema.getNominalAttrUse(ClusAttrType.ATTR_USE_ALL).length;
         System.out.println("New size: "+(l+inc));
         System.out.println("l: "+l);
         System.out.println("inc: "+inc);
         System.out.println("l+inc: "+(l+inc));
         System.out.println("Old size: "+prevDouble);
         
        for (int j=0;j<data.getNbRows();j++){
            count=0;
         
        int arow[]=new int[l+inc];
        if(prevDouble>0){
            for(int k=0;k<dataList.get(j).m_Ints.length;k++){
                arow[k]=dataList.get(j).m_Ints[k];
                    }
        }

            for(int i=startIndex;i<endIndex;i++)
                if(redescriptions.get(i).elements.size()<=naex*endPercentage && redescriptions.get(i).elements.size()>=naex*startPercentage && redescriptions.get(i).elements.size()>=cuttof && redescriptions.get(i).elements.size()<=cuttofMax){
                    if(redescriptions.get(i).elements.contains(map.exampleId.get((String)dataList.get(j).m_Objects[0])))
                arow[l+count]=0;
                    else
                         arow[l+count]=1;
                    count++;
                }
            dataList.get(j).m_Ints=arow;
        }

       data.setFromList(dataList);
       data.setSchema(schema);
       schema.setSettings(cset);

       try{
       writeArff(Output, data);
       }
       catch(Exception e){
           e.printStackTrace();
       }
    }
       
       public void modifyDatasetS(int startIndex, int endIndex, RuleReader RR,String Output, Mappings map, ApplicationSettings appset) throws IOException{

           int naex=numExamples;
     
            ArrayList<DataTuple> dataList=data.toArrayList();

            System.out.println("New rule index: "+RR.newRuleIndex);
            System.out.println("Rule size: "+RR.rules.size());

            int lastDouble=schema.getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length;
            int prevDouble=schema.getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length;
            int inc=0;
            
            for(int i=startIndex;i<endIndex;i++)
                if(RR.rules.get(i).elements.size()>=appset.minSupport){
            schema.addAttrType(new NumericAttrType("target"+(i+1-startIndex)));
            schema.getAttrType(schema.getNbAttributes()-1).setArrayIndex(lastDouble++);
            inc++;
                }
            System.out.println("nARules: "+inc);
            System.out.println("New number of attributes: "+schema.getNbAttributes());
         int count=0;
         
         int l=schema.getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length;
         System.out.println("New size: "+(l+inc));
         System.out.println("l: "+l);
         System.out.println("inc: "+inc);
         System.out.println("l+inc: "+(l+inc));
         System.out.println("Old size: "+prevDouble);
         
        for (int j=0;j<data.getNbRows();j++){
            count=0;
         
        double arow[]=new double[l+inc];
        if(prevDouble>0){
            for(int k=0;k<dataList.get(j).m_Doubles.length;k++){
                arow[k]=dataList.get(j).m_Doubles[k];
                    }
        }

            for(int i=startIndex;i<endIndex;i++)
                if(RR.rules.get(i).elements.size()>=appset.minSupport){
                    if(RR.rules.get(i).elements.contains(map.exampleId.get((String)dataList.get(j).m_Objects[0])))
                arow[l+count]=1.0;
                    else
                         arow[l+count]=0.0;
                    count++;
                }
            dataList.get(j).m_Doubles=arow;
        }

       data.setFromList(dataList);
       data.setSchema(schema);
       schema.setSettings(cset);

       try{
       writeArff(Output, data);
       }
       catch(Exception e){
           e.printStackTrace();
       }
    }
       
       public void modifyDatasetCat(int startIndex, int endIndex, RuleReader RR,String Output, Mappings map, ApplicationSettings appset) throws IOException{

           int naex=numExamples;
         
            ArrayList<DataTuple> dataList=data.toArrayList();

            System.out.println("New rule index: "+RR.newRuleIndex);
            System.out.println("Rule size: "+RR.rules.size());

            int lastDouble=schema.getNominalAttrUse(ClusAttrType.ATTR_USE_ALL).length;
            int prevDouble=schema.getNominalAttrUse(ClusAttrType.ATTR_USE_ALL).length;
            int inc=0;
            
            for(int i=startIndex;i<endIndex;i++)
                if(RR.rules.get(i).elements.size()>=appset.minSupport){
            schema.addAttrType(new NominalAttrType("target"+(i+1-startIndex)));
            schema.getAttrType(schema.getNbAttributes()-1).setArrayIndex(lastDouble++);
            inc++;
                }
				
            System.out.println("nARules: "+inc);
            System.out.println("New number of attributes: "+schema.getNbAttributes());
         int count=0;
         
         int l=schema.getNominalAttrUse(ClusAttrType.ATTR_USE_ALL).length;
         System.out.println("New size: "+(l+inc));
         System.out.println("l: "+l);
         System.out.println("inc: "+inc);
         System.out.println("l+inc: "+(l+inc));
         System.out.println("Old size: "+prevDouble);
         
        for (int j=0;j<data.getNbRows();j++){
            count=0;
         
        int arow[]=new int[l+inc];
        if(prevDouble>0){
            for(int k=0;k<dataList.get(j).m_Ints.length;k++){
                arow[k]=dataList.get(j).m_Ints[k];
                    }
        }

            for(int i=startIndex;i<endIndex;i++)
                if(RR.rules.get(i).elements.size()>=appset.minSupport){
                    if(RR.rules.get(i).elements.contains(map.exampleId.get((String)dataList.get(j).m_Objects[0])))
                arow[l+count]=0;
                    else
                         arow[l+count]=1;
                    count++;
                }
            dataList.get(j).m_Ints=arow;
        }

       data.setFromList(dataList);
       data.setSchema(schema);
       schema.setSettings(cset);

       try{
       writeArff(Output, data);
       }
       catch(Exception e){
           e.printStackTrace();
       }
    }
       
       public void modifyDatasetS(int startIndex, int endIndex,ArrayList<Redescription> redescriptions,String Output, Mappings map,ApplicationSettings appset) throws IOException{

           int naex=numExamples;

            ArrayList<DataTuple> dataList=data.toArrayList();

            int lastDouble=schema.getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length;
            int prevDouble=schema.getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length;
            int inc=0;
            
            for(int i=startIndex;i<endIndex;i++)
                if(redescriptions.get(i).elements.size()>=appset.minSupport){
            schema.addAttrType(new NumericAttrType("target"+(i+1-startIndex)));
            schema.getAttrType(schema.getNbAttributes()-1).setArrayIndex(lastDouble++);
            inc++;
                }
       
         int count=0;
         
         int l=schema.getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length;
         System.out.println("New size: "+(l+inc));
         System.out.println("l: "+l);
         System.out.println("inc: "+inc);
         System.out.println("l+inc: "+(l+inc));
         System.out.println("Old size: "+prevDouble);
         
        for (int j=0;j<data.getNbRows();j++){
            count=0;
         
        double arow[]=new double[l+inc];
        if(prevDouble>0){
            for(int k=0;k<dataList.get(j).m_Doubles.length;k++){
                arow[k]=dataList.get(j).m_Doubles[k];
                    }
        }

            for(int i=startIndex;i<endIndex;i++)
                if(redescriptions.get(i).elements.size()>=appset.minSupport){
                    if(redescriptions.get(i).elements.contains(map.exampleId.get((String)dataList.get(j).m_Objects[0])))
                arow[l+count]=1.0;
                    else
                         arow[l+count]=0.0;
                    count++;
                }
            dataList.get(j).m_Doubles=arow;
        }

       data.setFromList(dataList);
       data.setSchema(schema);
       schema.setSettings(cset);

       try{
       writeArff(Output, data);
       }
       catch(Exception e){
           e.printStackTrace();
       }
    }
       
       
       public void modifyDatasetSMW(int startIndex, int endIndex,ArrayList<Redescription> redescriptions,String Output, Mappings map,ApplicationSettings appset, int view) throws IOException{

           int naex=numExamples;

            ArrayList<DataTuple> dataList=data.toArrayList();

            int lastDouble=schema.getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length;
            int prevDouble=schema.getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length;
            int inc=0;
            
            for(int i=startIndex;i<endIndex;i++)
                if(redescriptions.get(i).elements.size()>=appset.minSupport && redescriptions.get(i).viewElementsLists.get(view).isEmpty()){
            schema.addAttrType(new NumericAttrType("target"+(i+1-startIndex)));
            schema.getAttrType(schema.getNbAttributes()-1).setArrayIndex(lastDouble++);
            inc++;
                }
         int count=0;
         
         int l=schema.getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length;
         System.out.println("New size: "+(l+inc));
         System.out.println("l: "+l);
         System.out.println("inc: "+inc);
         System.out.println("l+inc: "+(l+inc));
         System.out.println("Old size: "+prevDouble);
         
        for (int j=0;j<data.getNbRows();j++){
            count=0;
         
        double arow[]=new double[l+inc];
        if(prevDouble>0){
            for(int k=0;k<dataList.get(j).m_Doubles.length;k++){
                arow[k]=dataList.get(j).m_Doubles[k];
                    }
        }

            for(int i=startIndex;i<endIndex;i++)
                if(redescriptions.get(i).elements.size()>=appset.minSupport){
                    if(redescriptions.get(i).elements.contains(map.exampleId.get((String)dataList.get(j).m_Objects[0])))
                arow[l+count]=1.0;
                    else
                         arow[l+count]=0.0;
                    count++;
                }
            dataList.get(j).m_Doubles=arow;
        }

       data.setFromList(dataList);
       data.setSchema(schema);
       schema.setSettings(cset);

       try{
       writeArff(Output, data);
       }
       catch(Exception e){
           e.printStackTrace();
       }
    }
       
        public void modifyDatasetCat(int startIndex, int endIndex, ArrayList<Redescription> redescriptions,String Output, Mappings map, ApplicationSettings appset) throws IOException{

           int naex=numExamples;
       
            ArrayList<DataTuple> dataList=data.toArrayList();

            int lastDouble=schema.getNominalAttrUse(ClusAttrType.ATTR_USE_ALL).length;
            int prevDouble=schema.getNominalAttrUse(ClusAttrType.ATTR_USE_ALL).length;
            int inc=0;
            
            for(int i=startIndex;i<endIndex;i++)
                if(redescriptions.get(i).elements.size()>=appset.minSupport){
            schema.addAttrType(new NominalAttrType("target"+(i+1-startIndex)));
            schema.getAttrType(schema.getNbAttributes()-1).setArrayIndex(lastDouble++);
            inc++;
                }
            System.out.println("New number of attributes: "+schema.getNbAttributes());
         int count=0;
         
         int l=schema.getNominalAttrUse(ClusAttrType.ATTR_USE_ALL).length;
         System.out.println("New size: "+(l+inc));
         System.out.println("l: "+l);
         System.out.println("inc: "+inc);
         System.out.println("l+inc: "+(l+inc));
         System.out.println("Old size: "+prevDouble);
         
        for (int j=0;j<data.getNbRows();j++){
            count=0;
         
        int arow[]=new int[l+inc];
        if(prevDouble>0){
            for(int k=0;k<dataList.get(j).m_Ints.length;k++){
                arow[k]=dataList.get(j).m_Ints[k];
                    }
        }

            for(int i=startIndex;i<endIndex;i++)
                if(redescriptions.get(i).elements.size()>=appset.minSupport){
                    if(redescriptions.get(i).elements.contains(map.exampleId.get((String)dataList.get(j).m_Objects[0])))
                arow[l+count]=0;
                    else
                         arow[l+count]=1;
                    count++;
                }
            dataList.get(j).m_Ints=arow;
        }

       data.setFromList(dataList);
       data.setSchema(schema);
       schema.setSettings(cset);

       try{
       writeArff(Output, data);
       }
       catch(Exception e){
           e.printStackTrace();
       }
    }

       public void modifyDatasetS(int numRules, double startPercentage, double endPercentage, int startIndex, int endIndex, int cuttof,int cuttofMax , RuleReader RR,String Output, Mappings map) throws IOException{

           int naex=numExamples;
           int nARules=numRules;

            ArrayList<DataTuple> dataList=data.toArrayList();

            System.out.println("New rule index: "+RR.newRuleIndex);
            System.out.println("Rule size: "+RR.rules.size());

            int lastDouble=schema.getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length;
            int prevDouble=schema.getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length;
            int inc=0;
            
            for(int i=startIndex;i<endIndex;i++)
                if(RR.rules.get(i).elements.size()<=naex*endPercentage && RR.rules.get(i).elements.size()>=naex*startPercentage){
            schema.addAttrType(new NumericAttrType("target"+(i+1-startIndex)));
            schema.getAttrType(schema.getNbAttributes()-1).setArrayIndex(lastDouble++);
            inc++;
                }
            System.out.println("nARules: "+nARules);
            System.out.println("New number of attributes: "+schema.getNbAttributes());
         int count=0;
         
         int l=schema.getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length;
         System.out.println("New size: "+(l+inc));
         System.out.println("l: "+l);
         System.out.println("inc: "+inc);
         System.out.println("l+inc: "+(l+inc));
         System.out.println("Old size: "+prevDouble);
         
        for (int j=0;j<data.getNbRows();j++){
            count=0;
         
        double arow[]=new double[l+inc];
        if(prevDouble>0){
            for(int k=0;k<dataList.get(j).m_Doubles.length;k++){
                arow[k]=dataList.get(j).m_Doubles[k];
                    }
        }

            for(int i=startIndex;i<endIndex;i++)
                if(RR.rules.get(i).elements.size()<=naex*endPercentage && RR.rules.get(i).elements.size()>=naex*startPercentage){
                    if(RR.rules.get(i).elements.contains(map.exampleId.get((String)dataList.get(j).m_Objects[0])))
                arow[l+count]=1.0;
                    else
                         arow[l+count]=0.0;
                    count++;
                }
            dataList.get(j).m_Doubles=arow;
        }

       data.setFromList(dataList);
       data.setSchema(schema);
       schema.setSettings(cset);

       try{
       writeArff(Output, data);
       }
       catch(Exception e){
           e.printStackTrace();
       }
    }
       
       public void modifyDatasetS(int numRules, double startPercentage, double endPercentage, int startIndex, int endIndex, int cuttof,int cuttofMax , ArrayList<Redescription> redescriptions,String Output, Mappings map) throws IOException{

           int naex=numExamples;
           int nARules=numRules;
        
            ArrayList<DataTuple> dataList=data.toArrayList();

            int lastDouble=schema.getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length;
            int prevDouble=schema.getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length;
            int inc=0;
            
            for(int i=startIndex;i<endIndex;i++)
                if(redescriptions.get(i).elements.size()<=naex*endPercentage && redescriptions.get(i).elements.size()>=naex*startPercentage && redescriptions.get(i).elements.size()>=cuttof && redescriptions.get(i).elements.size()<=cuttofMax){
            schema.addAttrType(new NumericAttrType("target"+(i+1-startIndex)));
            schema.getAttrType(schema.getNbAttributes()-1).setArrayIndex(lastDouble++);
            inc++;
                }
            System.out.println("nARules: "+nARules);
            System.out.println("New number of attributes: "+schema.getNbAttributes());
         int count=0;
         
         int l=schema.getNumericAttrUse(ClusAttrType.ATTR_USE_ALL).length;
         System.out.println("New size: "+(l+inc));
         System.out.println("l: "+l);
         System.out.println("inc: "+inc);
         System.out.println("l+inc: "+(l+inc));
         System.out.println("Old size: "+prevDouble);
         
        for (int j=0;j<data.getNbRows();j++){
            count=0;
         
        double arow[]=new double[l+inc];
        if(prevDouble>0){
            for(int k=0;k<dataList.get(j).m_Doubles.length;k++){
                arow[k]=dataList.get(j).m_Doubles[k];
                    }
        }

            for(int i=startIndex;i<endIndex;i++)
                if(redescriptions.get(i).elements.size()<=naex*endPercentage && redescriptions.get(i).elements.size()>=naex*startPercentage && redescriptions.get(i).elements.size()>=cuttof && redescriptions.get(i).elements.size()<=cuttofMax){
                    if(redescriptions.get(i).elements.contains(map.exampleId.get((String)dataList.get(j).m_Objects[0])))
                arow[l+count]=1.0;
                    else
                         arow[l+count]=0.0;
                    count++;
                }
            dataList.get(j).m_Doubles=arow;
        }

       data.setFromList(dataList);
       data.setSchema(schema);
       schema.setSettings(cset);

       try{
       writeArff(Output, data);
       }
       catch(Exception e){
           e.printStackTrace();
       }
    }
       
    public void splitDataset(ArrayList<Integer> splitIndex, String outFolder){
        int oldIndex=0, numCreated=1;

        System.out.println("splitIndex size: "+splitIndex.size());

        for(int index:splitIndex){
            try{
            readDataset();
            }
            catch(IOException e){
                e.printStackTrace();
            }

             ArrayList<DataTuple> dataList=data.toArrayList();
                RowData dat=new RowData(data);
                ClusSchema sch=new ClusSchema(dat.getSchema().getRelationName());
                System.out.println("nb attr: "+schema.getNbAttributes());
                sch.addAttrType(schema.getAttrType(0));
                System.out.println("Number of doubles: "+dataList.get(0).m_Doubles.length);

                for(int i=oldIndex;i<index;i++)
                    sch.addAttrType(schema.getAttrType(i+1));

               
                for(int j=0;j<data.getNbRows();j++){
                     double arow[]=new double[index-oldIndex];
                  for(int k=oldIndex;k<index;k++){
                       arow[k-oldIndex]=dataList.get(j).m_Doubles[k];
                    }
                 dataList.get(j).m_Doubles=arow;
               }


                 dat.setFromList(dataList);
                 dat.setSchema(sch);
                 sch.setSettings(cset);

                 try{
                 writeArff(outFolder+"\\input"+(numCreated)+".arff", dat);
            }
                 catch(Exception e){
                     e.printStackTrace();
                 }

                 oldIndex=index;
                 numCreated++;

                 if((numCreated-1)==splitIndex.size()){

                     try{
            readDataset();
            }
            catch(IOException e){
                e.printStackTrace();
            }

                     System.out.println("Number of doubles: "+dataList.get(0).m_Doubles.length);
                     index=schema.getNbAttributes()-1;
                      dataList=data.toArrayList();
                      dat=new RowData(data);
                      sch=new ClusSchema(dat.getSchema().getRelationName());
                sch.clearAttributeStatusClusteringAndTarget();
                sch.addAttrType(schema.getAttrType(0));

                for(int i=oldIndex;i<index;i++)
                    sch.addAttrType(schema.getAttrType(i+1));

                  
                for(int j=0;j<data.getNbRows();j++){
                  double arow[]=new double[index-oldIndex];
                  for(int k=oldIndex;k<index;k++){
                       arow[k-oldIndex]=dataList.get(j).m_Doubles[k];
                    }
                 dataList.get(j).m_Doubles=arow;
               }
                 dat.setFromList(dataList);
                 dat.setSchema(sch);
                 sch.setSettings(cset);
                 oldIndex=index;

                 try{
                 writeArff(outFolder+"\\input"+(numCreated)+".arff", dat);
            }
                 catch(Exception e){
                     e.printStackTrace();
                 }

                 }
        }

    }
    
            public static void writeArff(String fname, RowData data) throws IOException, ClusException {
		PrintWriter wrt = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fname)));
		ClusSchema schema = data.getSchema();
		ARFFFile.writeArffHeader(wrt, schema);
                ARFFFile.writeArff(fname, data);
		wrt.close();
	}   
}
