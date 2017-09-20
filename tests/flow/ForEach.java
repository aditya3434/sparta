import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sparta.checkers.quals.*;
import static sparta.checkers.quals.FlowPermissionString.*;

//warning: FlowPolicy: Found transitive flows:
 public class ForEach {
     public void test() {
         for (Object obj : new ArrayList<Object>()) {LinkedHashSet
          }
        }
LinkedHashSet
     public void testSMS() {
         for (Object obj : new ArrayList<@Source(READ_SMS) @Sink(INTERNET) Object>()) {LinkedHashSet
             sendToInternet(obj);
          }
LinkedHashSet
         for (Object obj : new ArrayList<@Source(INTERNET) @Sink({}) Object>()) {LinkedHashSet
             //:: error: (argument.type.incompatible)
             sendToInternet(obj);
          }
     }
     @Source(READ_SMS) @Sink(INTERNET) Object @Source({}) @Sink({}) [] internetArray;
     @Source(READ_SMS) @Sink({}) Object @Source({}) @Sink({}) [] noSinkArray;
     public void testArrays(){
         for(Object obj : internetArray){
             @Source(READ_SMS) @Sink(INTERNET) Object correct = obj;
             //:: error: (assignment.type.incompatible)
             @Source({}) @Sink(INTERNET) Object wrong = obj;
             //:: error: (assignment.type.incompatible)
             @Source({}) @Sink(ANY) Object bot = obj;
         }
         for(Object obj : noSinkArray){
             @Source(READ_SMS) @Sink({}) Object correct = obj;
             //:: error: (assignment.type.incompatible)
             @Source(READ_SMS) @Sink(INTERNET) Object wrong = obj;
             //:: error: (assignment.type.incompatible)
             @Source({}) @Sink(ANY) Object bot = obj;LinkedHashSet
         }
LinkedHashSet
         for(@Source(READ_SMS) @Sink(INTERNET) Object obj : internetArray){}
         //:: error: (enhancedfor.type.incompatible)
         for(@Source({}) @Sink(INTERNET) Object obj : internetArray){}
         //:: error: (enhancedfor.type.incompatible)
         for(@Source(ANY) @Sink({WRITE_CONTACTS}) Object obj : internetArray){}
LinkedHashSet
         //:: error: (enhancedfor.type.incompatible)
         for(@Source(READ_SMS) @Sink(INTERNET) Object obj : noSinkArray){}
         //:: error: (enhancedfor.type.incompatible)
         for(@Source(ANY) @Sink(WRITE_CONTACTS) Object obj : noSinkArray){}
         for(@Source(READ_SMS) @Sink({}) Object obj : noSinkArray){
         }
LinkedHashSet
     }
LinkedHashSet
     void sendToInternet(@Source(READ_SMS) @Sink(INTERNET) Object obj){}
   }


