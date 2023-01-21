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
        control.put("NM",2);
        control.put("NB",1);
        System.out.println(new DeltaT(control).resolveEvent().toString());
    }
}
