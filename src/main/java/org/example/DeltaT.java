package org.example;

import java.util.HashMap;
import java.util.Map;

public class DeltaT extends Evento {
    public DeltaT( Map<String, Integer> variablesControl) {
        super(0D,1D, variablesControl, inicializarEstado(), inicializarFDPS());
    }

    protected Map<String, Double> resolveEvent() {
        Double tiempo =tiempoInicial;
        while (tiempo<tiempoFinal) {
            tiempo+=1;

            String estacion = estacionSegunDia(tiempo % 365);
            Double lluvia = getValueFromFdp("LLUV" + estacion);
            Double probabilidadAusencia = getValueFromFdp("AU" + estacion);

            Map<String, Integer> variablesDeControlEE = repartidoresDelDia(lluvia, probabilidadAusencia);
            variablesDeControlEE.put("TEMP", getValueFromFdp("TEMP" + estacion).intValue());
            Map<String, Double> flujoEntrada = new EventoEvento(variablesDeControlEE).resolveEvent(); // pasarle las variables de control
            actualizarEstado(flujoEntrada);
        }
        return construirResultados();
    }
    private static Map<String, String> inicializarFDPS() {
        Map<String, String> fdps = new HashMap<>();
        fdps.put("TEMPVER","20+R");
        fdps.put("LLUVVER","20+R");
        fdps.put("AUVER","R");
        return fdps;
    }
    private static Map<String, Double> inicializarEstado(){
        Map<String,Double> estado = new HashMap<>();
        estado.put("PP",0D);
        estado.put("CT",0D);
        estado.put("TO",0D);
        estado.put("PT",0D);

        return  estado;
    }
    private Map<String,Double> construirResultados()
    {
        Map<String,Double> resultado = new HashMap<String, Double>();
        resultado.put("CT",estado.get("CT"));
        resultado.put("PPP",estado.get("PP")/estado.get("PT")*100);
        resultado.put("TOM",estado.get("TO")/tiempoFinal/30);
        return resultado;
    }

    private Map<String,Integer> repartidoresDelDia(Double lluvia, Double ausencia) {
        Map<String,Integer> repartidoresDelDia= new HashMap<String, Integer>();
        repartidoresDelDia.put("NM",presentes(variablesControl.get("NM"),ausencia ));
        if(lluvia>0) {
            repartidoresDelDia.put("NB",0);
            return  repartidoresDelDia;
        }
        repartidoresDelDia.put("NB",presentes(variablesControl.get("NB"),ausencia ));
        return repartidoresDelDia;

    }

    private Integer presentes(Integer repartidores, Double porcentajeAusencia) {
        Integer ausentes = 0;
        for (int i = 0; i<repartidores; i++) {
            if(Math.random()< porcentajeAusencia){
                ausentes++;
            }
        }
        return repartidores-ausentes;
    }

    private String estacionSegunDia(Double dia) {
        if (dia < 79 || dia >= 354) {
            return "VER";
        }

        if(dia >= 79 && dia < 170) {
            return "OTOÑ";
        }

        if (dia >= 170 && dia < 261) {
            return "INV";
        }
        else return "PRIM";
    }

}
