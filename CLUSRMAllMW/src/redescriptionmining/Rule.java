/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package redescriptionmining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.hash.TIntHashSet;
import java.util.Collections;
import java.util.Set;

/**
 *
 * @author Matej Mihelcic
 * @institution Rudjer Boskovic Institute, Zagreb, Croatia
 * @mail matmih1@gmail.com
 * @description class containing definition of a rule and related functions
 */
public class Rule {
    TIntHashSet elements=null;
    HashMap<Integer,ArrayList<Double>> ruleMap=null;
     TIntHashSet elementsMissing=null;
    TIntHashSet ruleAtts=null;
    String rule=null;
    int numElements=0;
    
    Rule(){
        elements=new TIntHashSet(200);
        ruleMap=new HashMap<>();
        ruleAtts=new TIntHashSet(200);
    }
    
    Rule(String _rule, Mappings map){
        rule=_rule;
         elements=new TIntHashSet();
        ruleMap=new HashMap();
        ruleAtts=new TIntHashSet();
        
         String rs[]=rule.split(" AND ");
        for(int i=0;i<rs.length;i++){
                if(rs[i].contains(">")){
                    String t[]=rs[i].split(" > ");
                     if(!ruleAtts.contains(map.attId.get(t[0]))){
                         ruleAtts.add(map.attId.get(t[0]));
                    }
                    else{
                    }
                }
                else if(rs[i].contains("<=")){
                     String t[]=rs[i].split(" <= ");
                     if(!ruleAtts.contains(map.attId.get(t[0]))){
                        ruleAtts.add(map.attId.get(t[0]));
                    }
                    else{
                    }
                }
                else if(rs[i].contains("=") && !rs[i].contains("<")){
                    String t[]=rs[i].split(" = ");
                    if(!ruleAtts.contains(map.attId.get(t[0]))){
                        ruleAtts.add(map.attId.get(t[0]));
                    }
                }
                else if(rs[i].contains("in")){
                    numElements=-1;
                    return;
                }
            }      
    }
    
   void ConstructRuleBagging(String _rule, Mappings map){
        rule=_rule;
         elements=new TIntHashSet();
        ruleMap=new HashMap();
        ruleAtts=new TIntHashSet();
        
         String rs[]=rule.split(" AND ");
        for(int i=0;i<rs.length;i++){
                if(rs[i].contains(">")){
                    String t[]=rs[i].split(" > ");
                     if(!ruleAtts.contains(map.attId.get(t[0]))){
                    
                         ruleAtts.add(map.attId.get(t[0]));

                             ruleMap.put(map.attId.get(t[0]), new ArrayList<Double>(Collections.nCopies(4,0.0)));
                             
                            t[1] = t[1].replaceAll(",",".");
                             int numOccOfDot = t[1].length() - t[1].replaceAll("\\.", "").length();
                             
                             if(numOccOfDot>1){
                                 while((t[1].length() - t[1].replaceAll("\\.", "").length())>1)
                                        t[1] = t[1].replaceFirst("\\.", "");
                             }
                             
                             ruleMap.get(map.attId.get(t[0])).set(1, Double.parseDouble(t[1])+0.0000001);
                             ruleMap.get(map.attId.get(t[0])).set(3,Double.POSITIVE_INFINITY);
                    }
                    else{
                       
                    }
                }
                else if(rs[i].contains("<=")){
                     String t[]=rs[i].split(" <= ");
                     if(!ruleAtts.contains(map.attId.get(t[0]))){
                        ruleAtts.add(map.attId.get(t[0]));
                             ruleMap.put(map.attId.get(t[0]), new ArrayList<Double>(Collections.nCopies(4,0.0)));
                              ruleMap.get(map.attId.get(t[0])).set(1,Double.NEGATIVE_INFINITY);
                              
                               t[1] = t[1].replaceAll(",",".");
                             int numOccOfDot = t[1].length() - t[1].replaceAll("\\.", "").length();
                             
                             if(numOccOfDot>1){
                                 while((t[1].length() - t[1].replaceAll("\\.", "").length())>1)
                                        t[1] = t[1].replaceFirst("\\.", "");
                             }
                              
                             ruleMap.get(map.attId.get(t[0])).set(3, Double.parseDouble(t[1])+0.0000001);
                         
                    }
                    else{
                    }
                }
                else if(rs[i].contains("=") && !rs[i].contains("<")){
                    String t[]=rs[i].split(" = ");

                    if(!ruleAtts.contains(map.attId.get(t[0]))){
                        int att = map.attId.get(t[0]);
                        ruleAtts.add(att);
                String val=t[1].trim();
                ArrayList<Double> attVal=new ArrayList<>();
                attVal.add((double)map.cattAtt.get(att).getValue0().get(val));
                this.ruleMap.put(att, attVal);
                    }
                }
                else if(rs[i].contains("in")){
                    numElements=-1;
                    return;
                }
            }      
    }
   
    void addElementsBagging(Mappings map, DataSetCreator dat){ 

        int inSupport = 0;
               
         for(int i=0;i<dat.numExamples;i++){
             if(!map.idExample.containsKey(i))
                 continue;
             inSupport = 1;
                         TIntIterator it=ruleAtts.iterator();
                         while(it.hasNext()){
                             int at=it.next();
                             if(!map.catAttInd.contains(at)){
                                if(dat.getValue(at, i)==Double.POSITIVE_INFINITY){
                                    inSupport = 0;
                                    break;
                                }
                                else{
                                    
                                    if(dat.getValue(at, i) == Double.POSITIVE_INFINITY){
                                        inSupport = 0;
                                        break;
                                    }
                                    
                                    double lowerBound = ruleMap.get(at).get(1);
                                    double upperBound = ruleMap.get(at).get(3);
                                    
                                        if(dat.getValue(at, i)<=lowerBound || dat.getValue(at, i)>upperBound){
                                            inSupport = 0;
                                            break;
                                        }
                                }
                            }
                            else{
                                  Set<String> catVal=map.cattAtt.get(at).getValue0().keySet();
                                if(!catVal.contains(dat.getValueCategorical(at, i))){
                                    inSupport = 0;
                                    break;
                                }
                         }
                    }
                          if(inSupport == 1)
                                 elements.add(i);
           }
    }
    
    
    void addElementsMissing(Mappings map, DataSetCreator dat, int mode){
        
        elementsMissing=new TIntHashSet(100);

         for(int i=0;i<dat.numExamples;i++){
            if(!elements.contains(i)){
                         TIntIterator it=ruleAtts.iterator();
                       if(mode==0){
                         while(it.hasNext()){
                             int at=it.next();
                             if(!map.catAttInd.contains(at)){
                                if(dat.getValue(at, i)==Double.POSITIVE_INFINITY){
                                    elementsMissing.add(i);
                                break;
                                }
                            }
                            else{
                                  Set<String> catVal=map.cattAtt.get(at).getValue0().keySet();
                                if(!catVal.contains(dat.getValueCategorical(at, i))){
                                    elementsMissing.add(i);
                                    break;
                                }
                            }
                         }
                    }
            else if(mode==1){
                int contained=1;
                 while(it.hasNext()){
                             int at=it.next();
                             if(!map.catAttInd.contains(at)){
                                if(dat.getValue(at, i)!=Double.POSITIVE_INFINITY){
                                    contained=0;
                                break;
                                }
                            }
                            else{
                                  Set<String> catVal=map.cattAtt.get(at).getValue0().keySet();
                                if(catVal.contains(dat.getValueCategorical(at, i))){
                                    contained=0;
                                    break;
                                }
                            }
                         }
                 
                         if(contained==1)
                             elementsMissing.add(i);
                     }
               }
           }
    }
    
    void closeInterval(DataSetCreator dat, Mappings map){
        
         TIntIterator it = ruleAtts.iterator();
         
         while(it.hasNext()){
             ruleMap.put(it.next(), null);
         }

         Iterator<Integer> itL=ruleMap.keySet().iterator();
         
         while(itL.hasNext()){
             double min=Double.POSITIVE_INFINITY,max=Double.NEGATIVE_INFINITY;
             int att=itL.next();
         
         if(!map.cattAtt.containsKey(att)){
              TIntIterator itT=elements.iterator();
             while(itT.hasNext()){
                 
                 int s=itT.next();
           int ik=s;
        double val=dat.getValue(att, ik);
        
                if(val<min)
                    min=val;
                if(val>max)
                    max=val;
        }
             ArrayList<Double> attVal=new ArrayList<>(Collections.nCopies(4, 0.0));

             attVal.set(0, 1.0);
             attVal.set(2, 1.0);
             attVal.set(1, min);
             attVal.set(3, max);
             ruleMap.put(att, attVal);
         }
         else{
           TIntIterator  itT=elements.iterator();
           
             while(itT.hasNext()){
                 System.out.println("Iterating through elements");
              int s=itT.next();
                 int ik=s;
                 String val=dat.getValueCategorical(att, ik);
                ArrayList<Double> attVal=new ArrayList<>();
                attVal.add((double)map.cattAtt.get(att).getValue0().get(val));
                System.out.println(attVal.get(0));
                this.ruleMap.put(att, attVal);
                break;
             }
         }
       }

        TIntHashSet RSupport=new TIntHashSet(dat.numExamples);
       
         for(int i=0;i<dat.numExamples;i++){
             itL=ruleMap.keySet().iterator();
             int contained=1;
             while(itL.hasNext()){
                 int attr=itL.next();
                 
                 ArrayList<Double> attrVal=ruleMap.get(attr);
              
               if(!map.cattAtt.containsKey(attr)){
                   double val=dat.getValue(attr, i);
                 if(val>=attrVal.get(1) && val<=attrVal.get(3))
                        continue;
                 else{
                    contained=0;
                    break;
                 }
               }
               else{
                   String cat=map.cattAtt.get(attr).getValue1().get((int)(double)attrVal.get(0));
                   String realCat=dat.getValueCategorical(attr, i);
                   if(cat.contentEquals(realCat))
                       continue;
                   else{
                       contained=0;
                       break;
                   }
               }
           }

             if(contained==0)
                 continue;
             else{
                 RSupport.add(i);
             }
         }  
         
         if(RSupport.size()!=elements.size()){
             System.out.println("Rule size mismatch...");
             System.out.println("CLUS rule size: "+elements.size());
             System.out.println("Validation size: "+RSupport.size());
         }
       
         it=RSupport.iterator();
                 int ok=1;
                 
        while(it.hasNext()){
            int elem=it.next();
          if(!elements.contains(elem)){
              System.out.println("Not contained element in rule: "+map.idExample.get(elem));
              ok=0;
          }
        }
        
        it=elements.iterator();

        
        while(it.hasNext()){
            int elem=it.next();
            if(!RSupport.contains(elem)){
                System.out.println("Not contained element in validation: "+map.idExample.get(elem));
                ok=0;
            }
        }
        
        if(ok==0){
            System.out.println("Rule string");
        System.out.println(this.rule);
         it=elements.iterator();
        while(it.hasNext())
            System.out.print(map.idExample.get(it.next())+" ");
        System.exit(-1);
        }
  }
    
    void clearRuleMap(){
        ruleMap.clear();
    }
    
    void addRule(String _rule){
        rule=_rule;
    }
    
    void addElement(String element, Mappings map){
        if(map.exampleId.containsKey(element))
            elements.add(map.exampleId.get(element));
    }
    
    void checkElements(Mappings map, DataSetCreator dat){
        Iterator<Integer> it; 
        HashSet<Integer> toRemove=new HashSet<>();
          
        TIntIterator attrsIterator = ruleAtts.iterator();
        while(attrsIterator.hasNext()){
            int att=attrsIterator.next();
               if(!map.cattAtt.containsKey(att)){
               TIntIterator iterator = elements.iterator();
                   while (iterator.hasNext()) {
                        int elem = iterator.next();
                          double val=dat.getValue(att, elem);
                         if(val==Double.POSITIVE_INFINITY){
                             toRemove.add(elem);
                         }
                    }
               }
               else{
                   TIntIterator iterator = elements.iterator();
                  
                    while (iterator.hasNext()) {
                        int elem = iterator.next();
                          String attrVal=dat.getValueCategorical(att, elem);
                        if(!map.cattAtt.get(att).getValue0().containsKey(attrVal))
                                toRemove.add(elem);
                    }
               }
          }
            
            it=toRemove.iterator();
            
            while(it.hasNext()){
                elements.remove(it.next());
            }
        
    }
    
    Rule(Rule RNeg, DataSetCreator dat, Mappings map){
           
        elements=new TIntHashSet(dat.numExamples);
        for(int i=0;i<dat.numExamples;i++){
            if(!RNeg.elements.contains(i)){
                         TIntIterator it=RNeg.ruleAtts.iterator();
                         while(it.hasNext()){
                             int at=it.next();
                             if(!map.catAttInd.contains(at)){
                                if(dat.getValue(at, i)!=Double.POSITIVE_INFINITY){
                                    elements.add(i);
                                break;
                                }
                            }
                            else{
                                  Set<String> catVal=map.cattAtt.get(at).getValue0().keySet();
                                if(catVal.contains(dat.getValueCategorical(at, i))){
                                    elements.add(i);
                                    break;
                                }
                            }
                         }
                    }
           }
           
           numElements=elements.size();
           ruleAtts=new TIntHashSet(RNeg.ruleAtts);
           rule=RNeg.rule;
    }
    
    public int checkConstraints(ArrayList<ArrayList<ArrayList<String>>> importantAttributes, int view, int constType ,Mappings map){  
        
        ArrayList<ArrayList<String>> constraints = importantAttributes.get(view);
        
        if(constraints.size()==0)
            return 1;
        
        TIntIterator it = this.ruleAtts.iterator();
        
        int s1=0;
        
       if(constType==2){ 
        for(int i=0;i<constraints.size();i++){
            s1=1;
            for(int j=0;j<constraints.get(i).size();j++){
                if(constraints.get(i).get(j).equals(""))
                    continue;
                if(!ruleAtts.contains(map.attId.get(constraints.get(i).get(j)))){
                    s1=0;
                    break;
                } 
            }
            if(s1==1)
                return 1;
        }
        return 0;
       }
       else if(constType==1){
            for(int i=0;i<constraints.size();i++){
                for(int j=0;j<constraints.get(i).size();j++){
                      if(constraints.get(i).get(j).equals(""))
                                 return 1;
                    if(ruleAtts.contains(map.attId.get(constraints.get(i).get(j)))){
                        return 1;
                 } 
                }
         }
        return 0;
       }
        
        return 1;
    }
    
}
