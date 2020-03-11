/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package redescriptionmining;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.hash.TIntHashSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.math3.distribution.BinomialDistribution;

/**
 *
 * @author Matej Mihelcic
 * @institution Rudjer Boskovic Institute, Zagreb, Croatia
 * @mail matmih1@gmail.com
 * @description class containing various functions implementing Jaccard index between rule support sets, redescription support sets etc.
 */
public class Jacard {
    
    double JS=0.0;
    int intersectSize=0;
    
    Jacard(){
        JS=0.0;
        intersectSize=0;
    }
    
    void initialize(){
        intersectSize=0;
        JS=0.0;
    }
    
    double computeJacard(Rule r1, Rule r2){
        int intersectionCount=0;
        
        TIntIterator iterator = r1.elements.iterator();
        
        while(iterator.hasNext()){
            int elem=iterator.next();
            if(r2.elements.contains(elem))
                intersectionCount++;
        }
        JS=intersectionCount/((double)(r1.elements.size()-intersectionCount+r2.elements.size()));
        intersectSize=intersectionCount;
        return JS;
    }
    
    
    
    double computeJacardPess(Rule r1, Rule r2, DataSetCreator dataset, Mappings map, int mode){
        int intersectionCount=0;
        r1.closeInterval(dataset, map);
        r2.closeInterval(dataset, map);
        TIntHashSet uniTmp=new TIntHashSet(dataset.numExamples);
        TIntIterator iterator = r1.elements.iterator();
        
        while(iterator.hasNext()){
            int elem=iterator.next();
            uniTmp.add(elem);
            if(r2.elements.contains(elem))
                intersectionCount++;
        }
        
        iterator=r2.elements.iterator();
        
         while(iterator.hasNext()){
            int elem=iterator.next();
            uniTmp.add(elem);
        }
        
         ArrayList<TIntHashSet> Missings=new ArrayList<>();

            Missings.add(new TIntHashSet(dataset.numExamples));
            Missings.add(new TIntHashSet(dataset.numExamples));

         for(int i=0;i<dataset.numExamples;i++){
             Iterator<Integer> itL=r1.ruleMap.keySet().iterator();
             Iterator<Integer> itR=r2.ruleMap.keySet().iterator();
             int contained=1, contained1=1, missing=0, fnc=1, missing1=0, fnc1=1;
            
        if(mode==0 || mode==1){  
             while(itL.hasNext()){
                 int attr=itL.next();
                 
                 ArrayList<Double> attrVal=r1.ruleMap.get(attr);
                 
               if(!map.cattAtt.containsKey(attr)){
                   double val=dataset.getValue(attr, i);
                 if(val>=attrVal.get(1) && val<=attrVal.get(3))
                        continue;
                 else if(val==Double.POSITIVE_INFINITY){
                     missing=1;
                     contained=0;
                 }    
                 else{
                    contained=0;
                    fnc=0;
                    break;
                 }
               }
               else{
                   String cat=map.cattAtt.get(attr).getValue1().get((int)(double)attrVal.get(0));
                   String realCat=dataset.getValueCategorical(attr, i);
                   if(cat.contentEquals(realCat))
                       continue;
                   else if(!(map.cattAtt.get(attr).getValue0().keySet().contains(cat))){
                       missing=1;
                   }
                   else{
                       contained=0;
                       break;
                   }
               }
             }
        }
        else{
             contained=0;
                    if(!r1.elements.contains(i)){
                        TIntIterator it=r1.ruleAtts.iterator();
                        while(it.hasNext()){
                            int at=it.next();
                            if(!map.cattAtt.keySet().contains(at)){
                                if(dataset.getValue(at, i)!=Double.POSITIVE_INFINITY){
                                    contained=1;
                                    fnc=0;
                                break;
                                }
                            }
                            else{
                                if(map.cattAtt.get(at).getValue0().keySet().contains(dataset.getValueCategorical(at, i))){
                                    contained=1;
                                    fnc=0;
                                    break;
                                }
                            }
                        }
                        if(contained==0){
                            missing=1;
                        }
                    }      
        }
             
            if(mode==0 || mode==2){ 
             while(itR.hasNext()){
                 int attr=itR.next();
                 
                 ArrayList<Double> attrVal=r2.ruleMap.get(attr);
                 
               if(!map.cattAtt.containsKey(attr)){
                   double val=dataset.getValue(attr, i);
                 if(val>=attrVal.get(1) && val<=attrVal.get(3))
                        continue;
                 else if(val==Double.POSITIVE_INFINITY){
                     missing1=1;
                     contained1=0;
                 }    
                 else{
                    contained1=0;
                    fnc1=0;
                    break;
                 }
               }
               else{
                   String cat=map.cattAtt.get(attr).getValue1().get((int)(double)attrVal.get(0));
                   String realCat=dataset.getValueCategorical(attr, i);
                   if(cat.contentEquals(realCat))
                       continue;
                   else if(!(map.cattAtt.get(attr).getValue0().keySet().contains(cat))){
                       missing1=1;
                   }
                   else{
                       contained1=0;
                       break;
                   }
               }
             }
            }
            else{
                 contained1=0;
                    if(!r2.elements.contains(i)){
                        TIntIterator it=r2.ruleAtts.iterator();
                        while(it.hasNext()){
                            int at=it.next();
                            if(!map.cattAtt.keySet().contains(at)){
                                if(dataset.getValue(at, i)!=Double.POSITIVE_INFINITY){
                                    contained1=1;
                                    fnc1=0;
                                break;
                                }
                            }
                            else{
                                if(map.cattAtt.get(at).getValue0().keySet().contains(dataset.getValueCategorical(at, i))){
                                    contained1=1;
                                    fnc1=0;
                                    break;
                                }
                            }
                        }
                        if(contained1==0){
                            missing1=1;
                        }
                    }  
            }
 
             if(missing==1 && fnc==1)
                 Missings.get(0).add(i);

             if(missing1==1 && fnc1==1)
                 Missings.get(1).add(i);
         }

         TIntIterator it=Missings.get(0).iterator();
              
              while(it.hasNext()){
                  int elem=it.next();
                  uniTmp.add(elem);
              } 
              
             it=Missings.get(1).iterator();
              
              while(it.hasNext()){
                  int elem=it.next();
                  uniTmp.add(elem);
              }
  
           double JSPess=intersectionCount/((double)uniTmp.size());
          JS=JSPess;

        r1.clearRuleMap(); r2.clearRuleMap();

        intersectSize=intersectionCount;
        return JS;
    }
    
       double computeJacard(Rule r1, Rule r2, DataSetCreator dat, Mappings map,int mode){
        int intersectionCount=0;
       
        if(mode==0){
        TIntIterator iterator = r1.elements.iterator();
        
        while(iterator.hasNext()){
            int elem=iterator.next();
            if(r2.elements.contains(elem))
                intersectionCount++;
        }
     
        JS=intersectionCount/((double)(r1.elements.size()-intersectionCount+r2.elements.size()));
        intersectSize=intersectionCount;
        }
        else if(mode==1){
            int count=0;
           for(int i=0;i<dat.numExamples;i++){
               int contained=0;
            if(!r2.elements.contains(i)){
                         TIntIterator it=r2.ruleAtts.iterator();
                         while(it.hasNext()){
                             int at=it.next();
                             if(!map.catAttInd.contains(at)){
                                if(dat.getValue(at, i)!=Double.POSITIVE_INFINITY){
                                    contained=1;
                                break;
                                }
                            }
                            else{
                                  Set<String> catVal=map.cattAtt.get(at).getValue0().keySet();
                                if(catVal.contains(dat.getValueCategorical(at, i))){
                                    contained=1;
                                    break;
                                }
                            }
                         }
                    }
            if(contained==1){
                if(r1.elements.contains(i)){
                    intersectionCount++;
                }
                count++;
            }
           }
   
        JS=intersectionCount/((double)(r1.elements.size()-intersectionCount+count));
        intersectSize=intersectionCount;
        
        }
        else if(mode==2){
            int count=0;
			
           for(int i=0;i<dat.numExamples;i++){
               int contained=0;
            if(!r1.elements.contains(i)){
                         TIntIterator it=r1.ruleAtts.iterator();
                         while(it.hasNext()){
                             int at=it.next();
                             if(!map.catAttInd.contains(at)){
                                if(dat.getValue(at, i)!=Double.POSITIVE_INFINITY){
                                    contained=1;
                                break;
                                }
                            }
                            else{
                                Set<String> catVal=map.cattAtt.get(at).getValue0().keySet();
                                if(catVal.contains(dat.getValueCategorical(at, i))){
                                    contained=1;
                                    break;
                                }
                            }
                         }
                    }
            if(contained==1){
                if(r2.elements.contains(i))
                    intersectionCount++;
                count++;
            }
           }

        JS=intersectionCount/((double)(r2.elements.size()-intersectionCount+count));
        intersectSize=intersectionCount;
            
        }
        
        return JS;
    }
    
       double computeJacardGen(Redescription R, Rule r2, DataSetCreator dat, Mappings map,int mode){
        int intersectionCount=0;
       
        if(mode==0){
        TIntIterator iterator = R.elements.iterator();
        
        while(iterator.hasNext()){
            int elem=iterator.next();
            if(r2.elements.contains(elem))
                intersectionCount++;
        }
     
        JS=intersectionCount/((double)(R.elementsUnion.size()-intersectionCount+r2.elements.size()));
        intersectSize=intersectionCount;
        }
        else if(mode==1){
            int count=0;
           for(int i=0;i<dat.numExamples;i++){
               int contained=0;
            if(!r2.elements.contains(i)){
                         TIntIterator it=r2.ruleAtts.iterator();
                         while(it.hasNext()){
                             int at=it.next();
                             if(!map.catAttInd.contains(at)){
                                if(dat.getValue(at, i)!=Double.POSITIVE_INFINITY){
                                    contained=1;
                                break;
                                }
                            }
                            else{
                                  Set<String> catVal=map.cattAtt.get(at).getValue0().keySet();
                                if(catVal.contains(dat.getValueCategorical(at, i))){
                                    contained=1;
                                    break;
                                }
                            }
                         }
                    }
            if(contained==1){
                if(R.elements.contains(i)){
                    intersectionCount++;
                }
                count++;
            }
           }
        
        JS=intersectionCount/((double)(R.elementsUnion.size()-intersectionCount+count));
        intersectSize=intersectionCount;
        
        }
        
        return JS;
    }
       
       double computeJacardGenRefine(Redescription R, Rule r2, int view, DataSetCreator dat, Mappings map,int mode){
        int intersectionCount=0;
       
        if(mode==0){
            
            R.closeInterval(dat, map);
            ArrayList<TIntHashSet> supports = R.computeElementsGen(dat, map);
            TIntHashSet d = new TIntHashSet(), dunion = new TIntHashSet();
            TIntIterator it=supports.get(0).iterator();
            
            for(int k=0;k<supports.size();k++)
                if(k!=view){
                    it = supports.get(k).iterator();
                    break;
                }
            
            for(int k=0;k<supports.size();k++)
                if(k!=view)
                    dunion.addAll(supports.get(k));
            
            while(it.hasNext()){
                int elem = it.next();
                int contained = 1;
                
                for(int k=0;k<supports.size();k++){
                    if(k!=view){
                        if(!supports.get(k).contains(elem)){
                            contained = 0;
                            break;
                        }
                    }
                }
                
                if(contained == 1 && r2.elements.contains(elem))
                    intersectionCount++;
            }
            
           
     
        JS=intersectionCount/((double)(dunion.size()-intersectionCount+r2.elements.size()));
        intersectSize=intersectionCount;
        }
        else if(mode==1){
            
             R.closeInterval(dat, map);
            ArrayList<TIntHashSet> supports = R.computeElementsGen(dat, map);
            TIntHashSet d = new TIntHashSet(), dunion = new TIntHashSet();
            TIntIterator it1=supports.get(0).iterator();
            
            for(int k=0;k<supports.size();k++)
                if(k!=view){
                    it1 = supports.get(k).iterator();
                    break;
                }
            
              for(int k=0;k<supports.size();k++)
                if(k!=view)
                    dunion.addAll(supports.get(k));
            
            while(it1.hasNext()){
                int elem = it1.next();
                int contained = 1;
                
                for(int k=0;k<supports.size();k++){
                    if(k!=view){
                        if(!supports.get(k).contains(elem)){
                            contained = 0;
                            break;
                        }
                    }
                }
                
                if(contained == 1)
                    d.add(elem);
            }
            
            
            int count=0;
           for(int i=0;i<dat.numExamples;i++){
               int contained=0;
            if(!r2.elements.contains(i)){
                         TIntIterator it=r2.ruleAtts.iterator();
                         while(it.hasNext()){
                             int at=it.next();
                             if(!map.catAttInd.contains(at)){
                                if(dat.getValue(at, i)!=Double.POSITIVE_INFINITY){
                                    contained=1;
                                break;
                                }
                            }
                            else{
                                  Set<String> catVal=map.cattAtt.get(at).getValue0().keySet();
                                if(catVal.contains(dat.getValueCategorical(at, i))){
                                    contained=1;
                                    break;
                                }
                            }
                         }
                    }
            if(contained==1){
                if(d.contains(i)){
                    intersectionCount++;
                }
                count++;
            }
           }
        
        JS=intersectionCount/((double)(dunion.size()-intersectionCount+count));
        intersectSize=intersectionCount;
        
        }
        
        return JS;
    }
    
    double computePval(Rule r1, Rule r2, DataSetCreator dat){
        double pVal=1.0;
           
            double prob=((double)(r1.elements.size()*r2.elements.size()))/(dat.numExamples*dat.numExamples);
            BinomialDistribution dist=new BinomialDistribution(dat.numExamples,prob);
            pVal=1.0-dist.cumulativeProbability(intersectSize);
        
        return pVal;
    }
    
        double computePval(Rule r1, Rule r2, DataSetCreator dat, Mappings map, int mode){
        double pVal=1.0;
            
            if(mode==1){
                int count=0;
           for(int i=0;i<dat.numExamples;i++){
               int contained=0;
            if(!r2.elements.contains(i)){
                         TIntIterator it=r2.ruleAtts.iterator();
                         while(it.hasNext()){
                             int at=it.next();
                             if(!map.catAttInd.contains(at)){
                                if(dat.getValue(at, i)!=Double.POSITIVE_INFINITY){
                                    contained=1;
                                break;
                                }
                            }
                            else{
                                if(map.cattAtt.get(at).getValue0().keySet().contains(dat.getValueCategorical(at, i))){
                                    contained=1;
                                    break;
                                }
                            }
                         }
                    }
            if(contained==1){
                count++;
            }
           }
           
             double prob=((double)(r1.elements.size()* count))/(dat.numExamples*dat.numExamples);
            BinomialDistribution dist=new BinomialDistribution(dat.numExamples,prob);
            pVal=1.0-dist.cumulativeProbability(intersectSize);
           
            }
            
            if(mode==2){
                int count=0;
           for(int i=0;i<dat.numExamples;i++){
               int contained=0;
            if(!r1.elements.contains(i)){
                         TIntIterator it=r1.ruleAtts.iterator();
                         while(it.hasNext()){
                             int at=it.next();
                             if(!map.catAttInd.contains(at)){
                                if(dat.getValue(at, i)!=Double.POSITIVE_INFINITY){
                                    contained=1;
                                break;
                                }
                            }
                            else{
                                if(map.cattAtt.get(at).getValue0().keySet().contains(dat.getValueCategorical(at, i))){
                                    contained=1;
                                    break;
                                }
                            }
                         }
                    }
            if(contained==1){
                count++;
            }
           }
           
            double prob=((double)(count*r2.elements.size()))/(dat.numExamples*dat.numExamples);
            BinomialDistribution dist=new BinomialDistribution(dat.numExamples,prob);
            pVal=1.0-dist.cumulativeProbability(intersectSize);
           
            }  
        return pVal;
    }
    
        
    double computePvalGenDisj(Redescription R, Rule r2, DataSetCreator dat, Mappings map, int view, int mode){
         double pVal=1.0;
         int intersectSize1 = 0;
         
        if(mode==0){
        
            double elemSize=1.0;
            double numExamples=1.0;
            ArrayList<TIntHashSet> elems=R.computeElementsGen(dat, map);
            
                elems.get(view).addAll(r2.elements);

               TIntIterator it = elems.get(0).iterator();
               int contained = 1;
               
               while(it.hasNext()){
                   int elem = it.next();
                   contained = 1;
                   
                   for(int j=1;j<(dat.W2indexs.size()+1);j++)
                       if(!elems.get(j).contains(elem))
                           contained = 0;
                   if(contained == 1)
                       intersectSize1++;
               }
                
            for(int i=0;i<R.viewElementsLists.size();i++){
                if(R.viewElementsLists.get(i).size()>0){
                elemSize*=elems.get(i).size();
                numExamples*=dat.numExamples;
                }
            }
                       
            double prob=elemSize/numExamples;
            BinomialDistribution dist=new BinomialDistribution(dat.numExamples,prob);
            pVal=1.0-dist.cumulativeProbability(intersectSize1);
        }
        
            if(mode==1){
                int count=0;
            TIntHashSet eNeg=new TIntHashSet(dat.numExamples);
           for(int i=0;i<dat.numExamples;i++){
               int contained=0;
            if(!r2.elements.contains(i)){
                         TIntIterator it=r2.ruleAtts.iterator();
                         while(it.hasNext()){
                             int at=it.next();
                             if(!map.catAttInd.contains(at)){
                                if(dat.getValue(at, i)!=Double.POSITIVE_INFINITY){
                                    contained=1;
                                break;
                                }
                            }
                            else{
                                if(map.cattAtt.get(at).getValue0().keySet().contains(dat.getValueCategorical(at, i))){
                                    contained=1;
                                    break;
                                }
                            }
                         }
                    }
            if(contained==1){
                count++;
                eNeg.add(i);
            }
           }
           
            double elemSize=1.0;
            double numExamples=1.0;
            ArrayList<TIntHashSet> elems=R.computeElementsGen(dat, map);
            elems.get(view).addAll(eNeg);
            
             TIntIterator it = elems.get(0).iterator();
               int contained = 1;
            
             while(it.hasNext()){
                   int elem = it.next();
                   contained = 1;
                   
                   for(int j=1;j<(dat.W2indexs.size()+1);j++)
                       if(!elems.get(j).contains(elem))
                           contained = 0;
                   if(contained == 1)
                       intersectSize1++;
               }
            
            for(int i=0;i<R.viewElementsLists.size();i++){
                if(R.viewElementsLists.get(i).size()>0){
                elemSize*=elems.get(i).size();
                numExamples*=dat.numExamples;
                }
            }
            
            double prob=elemSize/(numExamples);
            BinomialDistribution dist=new BinomialDistribution(dat.numExamples,prob);
            pVal=1.0-dist.cumulativeProbability(intersectSize1);
           
            }
             
        return pVal;
    }    
        
    double computePvalGen(Redescription R, Rule r2, DataSetCreator dat, Mappings map, int mode){
        double pVal=1.0;

        if(mode==0){
        
            double elemSize=1.0;
            double numExamples=1.0;
            ArrayList<TIntHashSet> elems=R.computeElementsGen(dat, map);

            for(int i=0;i<R.viewElementsLists.size();i++){
                if(R.viewElementsLists.get(i).size()>0){
                elemSize*=elems.get(i).size();
                numExamples*=dat.numExamples;
                }
            }
            
            elemSize*=r2.elements.size();
            numExamples*=dat.numExamples;
            
            double prob=elemSize/numExamples;
            BinomialDistribution dist=new BinomialDistribution(dat.numExamples,prob);
            pVal=1.0-dist.cumulativeProbability(intersectSize);
        }
        
            if(mode==1){
                int count=0;

           for(int i=0;i<dat.numExamples;i++){
               int contained=0;
            if(!r2.elements.contains(i)){
                         TIntIterator it=r2.ruleAtts.iterator();
                         while(it.hasNext()){
                             int at=it.next();
                             if(!map.catAttInd.contains(at)){
                                if(dat.getValue(at, i)!=Double.POSITIVE_INFINITY){
                                    contained=1;
                                break;
                                }
                            }
                            else{
                                if(map.cattAtt.get(at).getValue0().keySet().contains(dat.getValueCategorical(at, i))){
                                    contained=1;
                                    break;
                                }
                            }
                         }
                    }
            if(contained==1){
                count++;
            }
           }
           
            double elemSize=1.0;
            double numExamples=1.0;
            ArrayList<TIntHashSet> elems=R.computeElementsGen(dat, map);
            for(int i=0;i<R.viewElementsLists.size();i++){
                if(R.viewElementsLists.get(i).size()>0){
                elemSize*=elems.get(i).size();
                numExamples*=dat.numExamples;
                }
            }
            
            elemSize*=count;
            numExamples*=dat.numExamples;
           
            double prob=elemSize/(numExamples);
            BinomialDistribution dist=new BinomialDistribution(dat.numExamples,prob);
            pVal=1.0-dist.cumulativeProbability(intersectSize);
           
            }
             
        return pVal;
    }    
    
    double computePvalGenRefine(Redescription R, Rule r2, int view, DataSetCreator dat, Mappings map, int mode){
        double pVal=1.0;

        if(mode==0){
        
            double elemSize=1.0;
            double numExamples=1.0;
            ArrayList<TIntHashSet> elems=R.computeElementsGen(dat, map);

            for(int i=0;i<R.viewElementsLists.size();i++){
                if(i!=view){
                elemSize*=elems.get(i).size();
                numExamples*=dat.numExamples;
                }
            }
            
            elemSize*=r2.elements.size();
            numExamples*=dat.numExamples;
            
            double prob=elemSize/numExamples;
            BinomialDistribution dist=new BinomialDistribution(dat.numExamples,prob);
            pVal=1.0-dist.cumulativeProbability(intersectSize);
        }
        
            if(mode==1){
                int count=0;
           for(int i=0;i<dat.numExamples;i++){
               int contained=0;
            if(!r2.elements.contains(i)){
                         TIntIterator it=r2.ruleAtts.iterator();
                         while(it.hasNext()){
                             int at=it.next();
                             if(!map.catAttInd.contains(at)){
                                if(dat.getValue(at, i)!=Double.POSITIVE_INFINITY){
                                    contained=1;
                                break;
                                }
                            }
                            else{
                                if(map.cattAtt.get(at).getValue0().keySet().contains(dat.getValueCategorical(at, i))){
                                    contained=1;
                                    break;
                                }
                            }
                         }
                    }
            if(contained==1){
                count++;
            }
           }
           
            double elemSize=1.0;
            double numExamples=1.0;
            ArrayList<TIntHashSet> elems=R.computeElementsGen(dat, map);
            for(int i=0;i<R.viewElementsLists.size();i++){
                if(i!=view){
                elemSize*=elems.get(i).size();
                numExamples*=dat.numExamples;
                }
            }
            
            elemSize*=count;
            numExamples*=dat.numExamples;
           
            double prob=elemSize/(numExamples);
            BinomialDistribution dist=new BinomialDistribution(dat.numExamples,prob);
            pVal=1.0-dist.cumulativeProbability(intersectSize);
           
            }
             
        return pVal;
    }    
    
    
    
    double computeRedescriptionElementJacard(Redescription R1, Redescription R2){
        
        int intersectionCount=0;
        
        TIntIterator it=R1.elements.iterator();
        
        while(it.hasNext()){
            int s=it.next();
           if(R2.elements.contains(s)){
                    intersectionCount++; 
        }
       }

        JS=intersectionCount/((double)(R1.elements.size()-intersectionCount+R2.elements.size()));
        intersectSize=intersectionCount;
        
        return JS;
    }
    
    double computeRedescriptionRuleElementJacard(Redescription R1, Rule r, TIntHashSet LRElem, TIntHashSet RRElem, int side, int negated, DataSetCreator dat, Mappings map, ApplicationSettings appSet){
        JS=0.0;
        int intersectionCount=0, intersectionCount1=0,intersectionCount2=0;
        TIntHashSet elements=null;
        TIntHashSet elements1=null;
        
        if(side==0){
            elements=RRElem;
            elements1=LRElem;
        }
        else if(side==1){
            elements=LRElem;
            elements1=RRElem;
        }
        
        if(negated==0){
           TIntIterator iterator=r.elements.iterator();
           
              while(iterator.hasNext()){
               int num=iterator.next();
               boolean r1cont=R1.elements.contains(num);
               if(elements.contains(num) && !r1cont)
                   intersectionCount++;
               else if((elements1.contains(num) && !elements.contains(num))  || r1cont)
                   intersectionCount1++;
               else if(!elements1.contains(num) && !elements.contains(num) && !r1cont)
                   intersectionCount2++;
           }
           
           JS=intersectionCount/((double)(elements.size()-R1.elements.size()+r.elements.size()-intersectionCount1-intersectionCount));         
           if(intersectionCount==0)
               JS=0.0;               
        }
        else if(negated==1){
            int count=0;
           for(int i=0;i<dat.numExamples;i++){
               int contained=0;
            if(!r.elements.contains(i)){
                         TIntIterator it=r.ruleAtts.iterator();
                         while(it.hasNext()){
                             int at=it.next();
                             if(!map.catAttInd.contains(at)){
                                if(dat.getValue(at, i)!=Double.POSITIVE_INFINITY){
                                    contained=1;
                                break;
                                }
                            }
                            else{
                                  Set<String> catVal=map.cattAtt.get(at).getValue0().keySet();
                                if(catVal.contains(dat.getValueCategorical(at, i))){
                                    contained=1;
                                    break;
                                }
                            }
                         }
                    }
            if(contained==1){
                int num=i;
               boolean r1cont=R1.elements.contains(num);
               if(elements.contains(num) && !r1cont)
                   intersectionCount++;
               else if((elements1.contains(num) && !elements.contains(num))  || r1cont)
                   intersectionCount1++;
               else if(!elements1.contains(num) && !elements.contains(num) && !r1cont)
                   intersectionCount2++;
               
                count++;
            }
           }
           
           JS=intersectionCount/((double)(elements.size()-R1.elements.size()+count-intersectionCount1-intersectionCount));  
           
           if(intersectionCount==0)
               JS=0.0;
        }

        
        if(intersectionCount>appSet.maxSupport)
            JS=0.0;
        
        return JS;
    }
    
    int computeGenInterCount(Redescription R1, ArrayList<TIntHashSet> viewElems, int side){
        int intersectionCount=0;
        int toSkip=0;
        TIntIterator iterator1=viewElems.get(0).iterator();
        if(side==0){
            iterator1=viewElems.get(1).iterator();
            toSkip=1;
        }
                while(iterator1.hasNext()){
                    int contained=1;
                    int elem=iterator1.next();
                       for(int k=0;k<viewElems.size();k++)
                            if(side!=k && k!=toSkip){
                              if(!viewElems.get(k).contains(elem)){
                                  contained=0;
                                  break;
                              }
                        }
                       
                       if(contained==1 && !R1.elements.contains(elem))
                           intersectionCount++;  
                  }
                
                return intersectionCount;
    }
    
    double computeRedescriptionRuleElementJacardGen(Redescription R1, Rule r, ArrayList<TIntHashSet> viewElems, int side, int negated, DataSetCreator dat, Mappings map, ApplicationSettings appSet, int interCountE){
        JS=0.0;
        int intersectionCount=0, intersectionCount1=0,intersectionCount2=0;
        
        if(R1.viewElementsLists.get(side).size()==0)
            return 0.0;
        
        if(interCountE==0)
            return 0.0;
        
        intersectionCount2=interCountE;

        if(negated==0){
           TIntIterator iterator=r.elements.iterator();
           
              while(iterator.hasNext()){
                  int contained=1;
                  int notContained=1;
               int num=iterator.next();
               for(int k=0;k<viewElems.size();k++)
                   if(k!=side){
                       if(viewElems.get(k).contains(num))
                           notContained=0;
                       else
                           contained=0;
                   }
               
               if(contained==1 && !R1.elements.contains(num))
                   intersectionCount++;
               if(notContained==1 && !R1.elements.contains(num) && !viewElems.get(side).contains(num))
                   intersectionCount1++;
           }
   
           JS=intersectionCount/((double)(intersectionCount1+intersectionCount2));         
           if(intersectionCount==0)
               JS=0.0;              
        }
        else if(negated==1){
            int count=0;
           for(int i=0;i<dat.numExamples;i++){
               int contained=0;
            if(!r.elements.contains(i)){
                         TIntIterator it=r.ruleAtts.iterator();
                         while(it.hasNext()){
                             int at=it.next();
                             if(!map.catAttInd.contains(at)){
                                if(dat.getValue(at, i)!=Double.POSITIVE_INFINITY){
                                    contained=1;
                                break;
                                }
                            }
                            else{
                                  Set<String> catVal=map.cattAtt.get(at).getValue0().keySet();
                                if(catVal.contains(dat.getValueCategorical(at, i))){
                                    contained=1;
                                    break;
                                }
                            }
                         }
                    }
            if(contained==1){
                int num=i;
                
                int contained1=1;
                int notContained=1;
               for(int k=0;k<viewElems.size();k++)
                   if(k!=side){
                       if(viewElems.get(k).contains(num))
                           notContained=0;
                       else
                           contained1=0;
                   }
               
               if(contained1==1 && !R1.elements.contains(num))
                   intersectionCount++;
               if(notContained==1 && !R1.elements.contains(num))
                   intersectionCount1++; 
            }
           }
           
           JS=intersectionCount/((double)(intersectionCount1+intersectionCount2));      
         
           if(intersectionCount==0)
               JS=0.0;
        }
  
        if(intersectionCount+R1.elements.size()>appSet.maxSupport)
            JS=0.0;
        
        return JS;
    }
    
    
    double computeRedescriptionRuleElementJacardGenPess(Redescription R1, Rule r, ArrayList<TIntHashSet> viewElems, int side, int negated, DataSetCreator dat, Mappings map, ApplicationSettings appSet, int interCountE){
        JS=0.0;
        int intersectionCount=0, intersectionCount1=0,intersectionCount2=0;
        
        TIntHashSet missings= new TIntHashSet(dat.numExamples);
        
        if(R1.viewElementsLists.get(side).size()==0)
            return 0.0;
        
        if(interCountE==0)
            return 0.0;
        
        intersectionCount2=interCountE;

        
        r.closeInterval(dat, map);
        
        for(int i=0;i<dat.numExamples;i++){
             Iterator<Integer> itL=r.ruleMap.keySet().iterator();
             int contained=1, missing=0, fnc=1;
           
        if(negated==0){  
             while(itL.hasNext()){
                 int attr=itL.next();
                 
                 ArrayList<Double> attrVal=r.ruleMap.get(attr);
              
               if(!map.cattAtt.containsKey(attr)){
                   double val=dat.getValue(attr, i);
                 if(val>=attrVal.get(1) && val<=attrVal.get(3))
                        continue;
                 else if(val==Double.POSITIVE_INFINITY){
                     missing=1;
                     contained=0;
                 }    
                 else{
                    contained=0;
                    fnc=0;
                    break;
                 }
               }
               else{
                   String cat=map.cattAtt.get(attr).getValue1().get((int)(double)attrVal.get(0));
                   String realCat=dat.getValueCategorical(attr, i);
                   if(cat.contentEquals(realCat))
                       continue;
                   else if(!(map.cattAtt.get(attr).getValue0().keySet().contains(cat))){
                       missing=1;
                   }
                   else{
                       contained=0;
                       break;
                   }
               }
             }
        }
        else{
             contained=0;
                    if(!r.elements.contains(i)){
                        TIntIterator it=r.ruleAtts.iterator();
                        while(it.hasNext()){
                            int at=it.next();
                            if(!map.cattAtt.keySet().contains(at)){
                                if(dat.getValue(at, i)!=Double.POSITIVE_INFINITY){
                                    contained=1;
                                    fnc=0;
                                break;
                                }
                            }
                            else{
                                if(map.cattAtt.get(at).getValue0().keySet().contains(dat.getValueCategorical(at, i))){
                                    contained=1;
                                    fnc=0;
                                    break;
                                }
                            }
                        }
                        if(contained==0){
                            missing=1;
                        }
                    }      
        }
		
		if(missing==1 && fnc==1)
                missings.add(i);		 
				 }
        

        if(negated==0){
           TIntIterator iterator=r.elements.iterator();
           
              while(iterator.hasNext()){
                  int contained=1;
                  int notContained=1;
               int num=iterator.next();
               for(int k=0;k<viewElems.size();k++)
                   if(k!=side){
                       if(viewElems.get(k).contains(num))
                           notContained=0;
                       else
                           contained=0;
                   }
               
               if(contained==1 && !R1.elements.contains(num))
                   intersectionCount++;
               if(notContained==1 && !R1.elements.contains(num) && !viewElems.get(side).contains(num))
                   intersectionCount1++;
           }
   
           JS=intersectionCount/((double)(intersectionCount1+intersectionCount2));         
           if(intersectionCount==0)
               JS=0.0;              
        }
        else if(negated==1){
            int count=0;
           for(int i=0;i<dat.numExamples;i++){
               int contained=0;
            if(!r.elements.contains(i)){
                         TIntIterator it=r.ruleAtts.iterator();
                         while(it.hasNext()){
                             int at=it.next();
                             if(!map.catAttInd.contains(at)){
                                if(dat.getValue(at, i)!=Double.POSITIVE_INFINITY){
                                    contained=1;
                                break;
                                }
                            }
                            else{
                                  Set<String> catVal=map.cattAtt.get(at).getValue0().keySet();
                                if(catVal.contains(dat.getValueCategorical(at, i))){
                                    contained=1;
                                    break;
                                }
                            }
                         }
                    }
            if(contained==1){
                int num=i;
                
                int contained1=1;
                int notContained=1;
               for(int k=0;k<viewElems.size();k++)
                   if(k!=side){
                       if(viewElems.get(k).contains(num))
                           notContained=0;
                       else
                           contained1=0;
                   }
               
               if(contained1==1 && !R1.elements.contains(num))
                   intersectionCount++;
               if(notContained==1 && !R1.elements.contains(num))
                   intersectionCount1++; 
            }
           }
           
           JS=intersectionCount/((double)(intersectionCount1+intersectionCount2+missings.size()));      
         
           if(intersectionCount==0)
               JS=0.0;
        }
  
        if(intersectionCount+R1.elements.size()>appSet.maxSupport)
            JS=0.0;
        
        return JS;
    }
    
    double computeAttributeJacard(Redescription R1, Redescription R2, DataSetCreator dat){
        int intersectionCount=0;

        ArrayList<TIntHashSet> attrL=R1.computeAttributes(R1.viewElementsLists, dat);
        ArrayList<TIntHashSet> attrL1=R2.computeAttributes(R2.viewElementsLists, dat);
         
      for(int k=0;k<attrL.size();k++){  
        TIntIterator it=attrL.get(k).iterator();
        
        while(it.hasNext()){
            if(attrL1.get(k).contains(it.next()))
                intersectionCount++;
        }
      }

      int attrLSize=0,attrL1Size=0;
      
      for(int k=0;k<attrL.size();k++)
          attrLSize+=attrL.get(k).size();
      
      for(int k=0;k<attrL1.size();k++)
          attrL1Size+=attrL1.get(k).size();
      
        JS=intersectionCount/((double)attrLSize+attrL1Size-intersectionCount);
        intersectSize=intersectionCount;
        
        return JS;
        
    }
    
}
