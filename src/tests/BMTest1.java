package tests;

public class BMTest1 {

   public static void main (String argv[]) {

     BMDriver bmt = new BMDriver();
     boolean dbstatus;
     
     dbstatus = bmt.runTests();

     if (dbstatus != true) {
       System.err.println ("Error encountered during buffer manager tests:\n");
       Runtime.getRuntime().exit(1);
     }

     Runtime.getRuntime().exit(0);
   }
}