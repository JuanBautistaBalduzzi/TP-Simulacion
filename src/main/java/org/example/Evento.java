package org.example;

import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class Evento {
    protected Double tiempoInicial;
    protected Double tiempoFinal;
    protected Map<String,Integer> variablesControl;
    protected Map<String,Double> estado;
    private Map<String,String> fdps;

    public Evento(Double tiempoInicial, Double tiempoFinal, Map<String, Integer> variablesControl, Map<String, Double> estado, Map<String, String> fdps) {
        this.tiempoInicial = tiempoInicial;
        this.tiempoFinal = tiempoFinal;
        this.variablesControl = variablesControl;
        this.estado = estado;
        this.fdps = fdps;
    }



    protected Double getValueFromFdp(String fdp) {
        Argument argument = new Argument("R =" + String.valueOf(Math.random()));
        Expression expresion = new Expression(fdps.get(fdp),argument);
        return expresion.calculate();
    }
    protected void actualizarEstado(Map<String,Double> flujoEntrada) {
        estado.keySet().forEach(key ->
                estado.replace(key, flujoEntrada.getOrDefault(key, 0D) + estado.get(key)));
    }
    protected void sumarEstado(String key, Double value) {
        estado.replace(key,estado.get(key)+ value);
    }

    protected abstract Map<String,Double> resolveEvent();
}

