package org.example;

import java.util.HashMap;
import java.util.Map;

public class DeltaT extends Evento {
    public DeltaT( Map<String, Integer> variablesControl) {
        super(0D,365D*5, variablesControl, inicializarEstado(), inicializarFDPS());
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
            sumarEstado("CT",variablesControl.get("NM")*100D);
            sumarEstado("CT",variablesControl.get("NB")*70D);
        }
        return construirResultados();
    }
    private static Map<String, String> inicializarFDPS() {
        Map<String, String> fdps = new HashMap<>();
        fdps.put("TEMPVER","15*R + 20");
        fdps.put("TEMPOTOÑ","15*R + 10");
        fdps.put("TEMPINV","15*R");
        fdps.put("TEMPPRIM","14*R + 18");
        fdps.put("LLUVVER","(ln(-R+1)/-0.5686)-1");
        fdps.put("LLUVINV","(ln(-R+1)/-0.6304)-1");
        fdps.put("LLUVOTOÑ","(ln(-R+1)/-0.7073)-1");
        fdps.put("LLUVPRIM","[ln(-R+1)/-0.6170]-1");
        fdps.put("AUVER","2.38/((1/R-1)^(1/11))+11");
        fdps.put("AUINV","3.74/((1/R-1)^(1/9.5))+9.5");
        fdps.put("AUPRIM","3.44/((1/R-1)^(1/6))+6");
        fdps.put("AUOTOÑ","4.47-1.72*ln(1/R-1)");
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
        resultado.put("CTM",estado.get("CT")/(tiempoFinal/30));
        resultado.put("PPP",estado.get("PP")/estado.get("PT")*100);
        resultado.put("TOMP",((estado.get("TO")/60)/(tiempoFinal*6/30)));
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
    private Integer repartidoresTotales(){
        return variablesControl.values().stream().mapToInt(x->x).sum();
    }

    private Integer presentes(Integer repartidores, Double porcentajeAusencia) {
        Integer ausentes = 0;
        for (int i = 0; i<repartidores; i++) {
            if(Math.random()< porcentajeAusencia/100){
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
