/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package redescriptionmining;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.hash.TIntHashSet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
/**
 *
 * @author Matej Mihelcic
 * @institution Rudjer Boskovic Institute, Zagreb, Croatia
 * @mail matmih1@gmail.com
 * @description class containing rule reader definition and the related functions
 */
public class RuleReader {
    ArrayList<Rule> rules=null;
    int newRuleIndex, newRuleIndexF;
    RuleReader(){
        rules=new ArrayList<Rule>();
        newRuleIndex=0;
    }
    
    int hasNewAtt(Rule r1, Rule r2, int NumNA){ 
        int tmp=0;
        
         TIntIterator it=r1.ruleAtts.iterator();

        while(it.hasNext()){
            Integer at=it.next();
            if(!r2.ruleAtts.contains(at))
                tmp++;
        }
        
         if(tmp==0 && r1.ruleAtts.size()==r2.ruleAtts.size() && r1.numElements>r2.numElements)
            return 1;

          if(tmp==0 && r1.ruleAtts.size()!=r2.ruleAtts.size())
            return 1;

        if(tmp>=NumNA)
            return 1;
        else
        return 0;
    }
    
    int addnewRules(RuleReader NRules, int NumNA){
        newRuleIndex=rules.size();
        int index=0;
        
        if(rules.size()==0){
            for(int i=0;i<NRules.rules.size();i++){
                rules.add(NRules.rules.get(i));
                System.out.println("addnewRules: "+NRules.rules.get(i).rule);
            }
            return NRules.rules.size();
        }
        
        for(int i=0;i<NRules.rules.size();i++){
            int hasNewAttribute=1;
            for(int j=0;j<rules.size();j++){
                if(hasNewAtt(NRules.rules.get(i),rules.get(j),NumNA)==0){
                    hasNewAttribute=0;
                    break;
                }   
            }
                if(hasNewAttribute==1){
                    rules.add(NRules.rules.get(i));
                    index++;
                }
        }
        return index;
    }

      void filterRules(int oldIndex){
        
          TIntHashSet ruleIndex = new TIntHashSet();
          Jacard js = new Jacard();

          for(int i=oldIndex;i<rules.size();i++){
              if(ruleIndex.contains(i))
                  continue;
              for(int j=i+1;j<rules.size();j++){
                  if(ruleIndex.contains(j))
                      continue;
                    if(js.computeJacard(rules.get(i), rules.get(j))>=0.9){
                        ruleIndex.add(j);
                    }
              }
          }
          
          System.out.println("To filter out: "+ruleIndex.size());
          
          for(int i=rules.size()-1;i>=oldIndex;i--)
              if(ruleIndex.contains(i)){
                  rules.remove(i);
              }
        
    }
      
      
      void removeDuplicates(int oldIndex){
        
          TIntHashSet ruleIndex = new TIntHashSet();
          Jacard js = new Jacard();

          for(int i=oldIndex;i<rules.size();i++){
              if(ruleIndex.contains(i))
                  continue;
              for(int j=i+1;j<rules.size();j++){
                  if(ruleIndex.contains(j))
                      continue;
                    if(js.computeJacard(rules.get(i), rules.get(j)) == 1.0){
                        TIntHashSet at1 = rules.get(i).ruleAtts;
                        TIntHashSet at2 = rules.get(j).ruleAtts;
                        
                        int inter = 0;
                        
                        TIntIterator it = at1.iterator();
                        int el;
                        while(it.hasNext()){
                            el = it.next();
                            if(at2.contains(el))
                                inter++;
                        }
                        
                        double jsAt = (inter)/((double)(rules.get(i).ruleAtts.size()+rules.get(j).ruleAtts.size()-inter));
                        
                        if(jsAt == 1.0)
                        ruleIndex.add(j);
                    }
              }
          }
          
          System.out.println("To filter out: "+ruleIndex.size());
          
          for(int i=rules.size()-1;i>=oldIndex;i--)
              if(ruleIndex.contains(i)){
                  rules.remove(i);
              }
        
    }
    
    
    void removeElements(int oldIndex){
        
        for(int i=0;i<oldIndex;i++)
            if(!rules.get(i).elements.isEmpty())
                rules.get(i).elements.clear();
        
    }
    
       int addnewRulesC(RuleReader NRules, int NumNA, int changeIndex){
           
           System.out.println("Adding new rules to set...");
           
           if(changeIndex==1)
        newRuleIndex=rules.size();
           
        int index=0;

        if(rules.size()==0){
            for(int i=0;i<NRules.rules.size();i++)
                rules.add(NRules.rules.get(i));
            return NRules.rules.size();
        }

        for(int i=0;i<NRules.rules.size();i++){
            int hasNewAttribute=1;
            for(int j=0;j<rules.size();j++){
                if(hasNewAtt(NRules.rules.get(i),rules.get(j),NumNA)==0){
                    hasNewAttribute=0;
                    break;
                }
            }
                if(hasNewAttribute==1){
                    rules.add(NRules.rules.get(i));
                    index++;
                }
                if(i%100==0)
                    if(i!=0)
                    System.out.println(i/100);
        }
        return index;
    }
       
       void removeRulesCF(){
           
           for(int i=rules.size()-1;i>=newRuleIndexF;i--)
               rules.remove(i);
           
       }
       
         int addnewRulesCF(RuleReader NRules, int NumNA){
           
           System.out.println("Adding new rules to set...");

        newRuleIndexF=rules.size();
           
        int index=0;

        if(rules.size()==0){
            for(int i=0;i<NRules.rules.size();i++)
                rules.add(NRules.rules.get(i));
            return NRules.rules.size();
        }

        for(int i=0;i<NRules.rules.size();i++){
            int hasNewAttribute=1;
            for(int j=0;j<rules.size();j++){
                if(hasNewAtt(NRules.rules.get(i),rules.get(j),NumNA)==0){
                    hasNewAttribute=0;
                    break;
                }
            }
                if(hasNewAttribute==1){
                    rules.add(NRules.rules.get(i));
                    index++;
                }
                if(i%100==0)
                    if(i!=0)
                    System.out.println(i/100);
        }
        return index;
    }
       
    void setSize(){
        for(int i=0;i<rules.size();i++)
            if(rules.get(i).elements.size()!=0)
            rules.get(i).numElements=rules.get(i).elements.size();
    }

    void reduceRuleSet(ArrayList<ArrayList<ArrayList<String>>> importantAttributes, Mappings map, ApplicationSettings appset){
        int contains=0;
        ArrayList<Integer> toRemove=new ArrayList<>();
        
        for(int i=(rules.size()-1);i>=0;i--){
            contains=0;
            for(int j=0;j<importantAttributes.size();j++){
            if(rules.get(i).ruleAtts.contains(map.attId.get(importantAttributes.get(j))))
                contains=1;
                 break;
            }
            
            if(contains==0)
                rules.remove(i);
            else System.out.println("Sadrzi atribut!");
        }     
    }
    
    void extractRules(String inputFile, Mappings map, DataSetCreator dat, ApplicationSettings appset, int bagging){
        File input=new File(inputFile);
        BufferedReader reader;
       
        try {
        reader= new BufferedReader(new FileReader(input));
        String line=null;
        int lineNum=0,found=0,endRule=1,examples=0;
        String rule=null;
        String ex=null;
        while ((line = reader.readLine()) != null){
        
                    if(line.contentEquals("Rules Model")){
                    found=1;
                }
                
                if(found==1){
                    lineNum++;
                }
                
                if(lineNum==4){
                    found=0;
                    String lTmp=line;
                    if(lTmp.split(" ")[0].equals("IF")){
                        endRule=0;
                        rule=new String(line.substring(3));
                    }
                    else if(lTmp.split(" ")[0].equals("THEN")){
                        endRule=1; 
                        rules.add(new Rule(rule,map));
                        
                        if(bagging == 1){
                            rules.get(rules.size()-1).ConstructRuleBagging(rule, map);
                            rules.get(rules.size()-1).addElementsBagging(map, dat);
                        }
                        continue;
                    }
                    else if(endRule==0){
                        rule=rule.concat(" ");
                        rule=rule.concat(line.substring(3));
                    }
                    else if(line.contains("Covered examples:") && bagging == 0){
                        examples=1;
                    }
                    else if(examples==1 && bagging == 0){
                        
                        if(line.contentEquals("")){
                            examples=0;
                            rules.get(rules.size()-1).checkElements(map, dat);
                            if(rules.get(rules.size()-1).elements.isEmpty() || rules.get(rules.size()-1).elements.size()<appset.minSupport || rules.get(rules.size()-1).numElements==-1){
                                rules.remove(rules.size()-1);
                            }
                            continue;
                        }
                        
                        String tok[]=line.split(" ");
                        if(tok.length>1){
                            ex=new String(tok[1]);
                        }
                        else{
                            examples=0;
                            rules.get(rules.size()-1).checkElements(map, dat);
                            if(rules.get(rules.size()-1).elements.isEmpty() || rules.get(rules.size()-1).elements.size()<appset.minSupport || rules.get(rules.size()-1).numElements==-1){
                                rules.remove(rules.size()-1);
                            }
                            continue;
                        }
                        
                       if(ex.length()>10){
                        if(!ex.substring(ex.length()-10, ex.length()-1).contains("new-Init")){
                        rules.get(rules.size()-1).addElement(ex,map);
                        }
                       }
                       else
                          rules.get(rules.size()-1).addElement(ex,map);  
                    } 
                }
        }
        if(rules.size()!=0){
            rules.get(rules.size()-1).checkElements(map, dat);
                            if(rules.get(rules.size()-1).elements.isEmpty() || rules.get((rules.size()-1)).elements.size()<appset.minSupport|| rules.get(rules.size()-1).numElements==-1){
                                rules.remove(rules.size()-1);
                            }
            }
        reader.close();
        }
        catch(IOException ioe)
        {
             System.err.println("IOException: " + ioe.getMessage());
        }
    }
    
      void extractRulesMod(String inputFile, Mappings map, DataSetCreator dat, ApplicationSettings appset, int bagging){
        File input=new File(inputFile);
        BufferedReader reader;
       
        try {
        reader= new BufferedReader(new FileReader(input));
        String line=null;
        int lineNum=0,found=0,endRule=1,examples=0;
        String rule=null;
        String ex=null;
        while ((line = reader.readLine()) != null){
                    if(line.contentEquals("Rules Model")){
                    found=1;
                }
                
                if(found==1){
                    lineNum++;
                }
                
                if(lineNum==4){
                    found=0;
                    String lTmp=line;
                    if(lTmp.split(" ")[0].equals("IF")){
                        endRule=0;
                        rule=new String(line.substring(3));
                    }
                    else if(lTmp.split(" ")[0].equals("THEN")){
                        endRule=1; 
                        rules.add(new Rule(rule,map));
                        
                        if(bagging == 1){
                            rules.get(rules.size()-1).ConstructRuleBagging(rule, map);
                            rules.get(rules.size()-1).addElementsBagging(map, dat);
                        }
                        continue;
                    }
                    else if(endRule==0){
                        rule=rule.concat(" ");
                        rule=rule.concat(line.substring(3));
                    }
                    else if(line.contains("Covered examples:") && bagging == 0){
                        examples=1;
                    }
                    else if(examples==1 && bagging == 0){
                        
                        if(line.contentEquals("")){
                            examples=0;
                            int removed = 0;
                            rules.get(rules.size()-1).checkElements(map, dat);
                            if(rules.get(rules.size()-1).elements.isEmpty() || rules.get(rules.size()-1).elements.size()<appset.minSupport || rules.get(rules.size()-1).numElements==-1){
                                rules.remove(rules.size()-1);
                                removed = 1;
                                continue;
                            }
                            
                            int check = 0;
                            
                            String r = rules.get(rules.size()-1).rule;
                            
                            String tmp[] = r.split("AND");
                            
                            for(int i=0;i<tmp.length;i++){
                                if(tmp[i].contains(">")){
                                     String t[]=tmp[i].split(" > ");
                                         check = 1;
                                         break;
                                }
                            }
                            
                            if(check == 0 && removed == 0){
                                  rules.remove(rules.size()-1);
                                  removed = 1;
                            }
                                
                            
                            continue;
                        }
                        
                        String tok[]=line.split(" ");
                        if(tok.length>1){
                            ex=new String(tok[1]);
                        }
                        else{
                            examples=0;
                            rules.get(rules.size()-1).checkElements(map, dat);
                            if(rules.get(rules.size()-1).elements.isEmpty() || rules.get(rules.size()-1).elements.size()<appset.minSupport || rules.get(rules.size()-1).numElements==-1){
                                rules.remove(rules.size()-1);
                            }
                            continue;
                        }
                        
                       if(ex.length()>10){
                        if(!ex.substring(ex.length()-10, ex.length()-1).contains("new-Init")){
                        rules.get(rules.size()-1).addElement(ex,map);
                        }
                       }
                       else
                          rules.get(rules.size()-1).addElement(ex,map);  
                    } 
                }
        }
        if(rules.size()!=0){
            rules.get(rules.size()-1).checkElements(map, dat);
                            if(rules.get(rules.size()-1).elements.isEmpty() || rules.get((rules.size()-1)).elements.size()<appset.minSupport|| rules.get(rules.size()-1).numElements==-1){
                                rules.remove(rules.size()-1);
                            }
            }
        reader.close();
        }
        catch(IOException ioe)
        {
             System.err.println("IOException: " + ioe.getMessage());
        }
    }
    
   int findCutoff(int numExamples, double startPercentage, double endPercentage, int StartIndex, int EndIndex, int[] minCovElem, int[] maxCovElem, int minSupp, int maxSupp, int numRules){
        int nelMin=(int)(numExamples*startPercentage);
        int nelMax=(int) (numExamples*endPercentage);

        int ruleCount=0;
        int minCovElemT=minSupp;
        int maxCovElemT=numExamples;

        if(nelMin<minSupp)
            nelMin=minSupp;
        
        if(nelMin>minSupp)
               minCovElemT=nelMin;
        
        int step=(nelMax-nelMin)/20;
        
        if(step==0)
            step=1;
        
        if(nelMax<minSupp)
            return -1;

        while(true){
            for(int i=StartIndex;i<EndIndex;i++){
                if(rules.get(i).numElements>=startPercentage*numExamples && rules.get(i).numElements<=endPercentage*numExamples && rules.get(i).numElements>=minCovElemT && rules.get(i).numElements<=maxCovElemT)
                    ruleCount++;
            }
            
            if(ruleCount==0)
                return -1;

            if(ruleCount>numRules){
                ruleCount=0;
                minCovElemT+=step;
            }
            else break;
        }
        minCovElem[0]=minCovElemT;
        maxCovElem[0]=maxCovElemT;
        return 0;
    }

}
