package org.example;

import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        Map<String,Integer> control = new HashMap<>();
        control.put("NM",10);
        control.put("NB",8);
        System.out.println("Variables de Control: " + control);
        System.out.println("Resultado: " +new DeltaT(control).resolveEvent().toString());
    }
}
