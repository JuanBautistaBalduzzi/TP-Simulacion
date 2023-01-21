package org.example;

import org.mariuszgromada.math.mxparser.Expression;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class EventoEvento extends Evento {
    private Double tiempoProximoPedido;
    private Double tiempo;

    private List<Double> tiempoComprometidoMotos = new ArrayList<>();
    private List<Double> tiempoComprometidoBici = new ArrayList<>();

    public EventoEvento( Map<String, Integer> variablesControl) {
        super(0D, 8D, variablesControl, inicializarEstado(), inicializarFDPS());
    }
    private static Map<String, String> inicializarFDPS() {
        Map<String, String> fdps = new HashMap<>();
        fdps.put("IA","8+R");
        fdps.put("KM","6+R");
        return fdps;
    }
    private static Map<String, Double> inicializarEstado(){
        Map<String,Double> estado = new HashMap<>();
        estado.put("PP",0D);
        estado.put("TO",0D);
        estado.put("CT",0D);
        estado.put("PT",0D);

        return  estado;
    }
    private void inicializarTiempos() {
        tiempoComprometidoMotos.stream().limit(variablesControl.get("NM")).collect(Collectors.toList());

        for(int i =0; i<variablesControl.get("NM") ; i++){
            tiempoComprometidoMotos.add(0D);
        }
        for(int i =0; i<variablesControl.get("NB") ; i++){
            tiempoComprometidoBici.add(0D);
        }
        tiempoProximoPedido= tiempoInicial;
        tiempo=tiempoInicial;
    }
    protected Map<String, Double> resolveEvent() {
        inicializarTiempos();
        while (tiempoProximoPedido < tiempoFinal) {
            tiempo = tiempoProximoPedido;
            tiempoProximoPedido += getValueFromFdp("IA");
            sumarEstado("PT",1D);

            Double distancia = getValueFromFdp("KM");
            if(distancia <= 5 && tiempoComprometidoBici.size() == 0) {
                pedidoEnBici(distancia);
            }
            else pedidoEnMoto(distancia);

        }

        return construirResultados();
    }

    private void pedidoEnMoto(Double distancia){
        Double minimoTCMoto = getMinMotos();
        Double tiempoPreparacion = 20 + distancia * 3;

        if(minimoTCMoto + tiempoPreparacion - tiempo < 60 ) {
            sumarEstado("CT",75D);
            actualizarTiempoComprometido(tiempoComprometidoMotos,minimoTCMoto,tiempoPreparacion);
            return;
        }
        if(Math.random() < 0.2) {
            sumarEstado("CT",75D);
            actualizarTiempoComprometido(tiempoComprometidoMotos,minimoTCMoto,tiempoPreparacion);
        }
        else sumarEstado("PP",1D);

    }

    private  void  actualizarTiempoComprometido(List<Double> vehiculos, Double tiempoARemplazar, Double tardanza) {
        if(tiempoARemplazar >= tiempo ){
            vehiculos.set(vehiculos.indexOf(tiempoARemplazar),tiempoARemplazar + tardanza);
            return;
        }
        sumarEstado("TO", tiempo - tiempoARemplazar);
        vehiculos.set(vehiculos.indexOf(tiempoARemplazar),tiempo + tardanza);
    }

    private void pedidoEnBici(Double distancia){
        Double minimoTCBici = getMinBicis();
        Double tiempoPreparacion = 20 + distancia * 3;

        if(minimoTCBici + tiempoPreparacion - tiempo < 60 ) {
            sumarEstado("CT",50D);
            actualizarTiempoComprometido(tiempoComprometidoBici,minimoTCBici,tiempoPreparacion);
        }

        Double minimoTCMoto = getMinMotos();
        if(minimoTCMoto + 20 + distancia * 1.5 - tiempo < 60) {
            pedidoEnMoto(distancia);
            return;
        }
        if(Math.random() < 0.2) {
            sumarEstado("CT",50D);
            actualizarTiempoComprometido(tiempoComprometidoBici,minimoTCBici,tiempoPreparacion);
        }
        else sumarEstado("PP",1D);


    }

    private Map<String,Double> construirResultados()
    {
        return estado;
    }

    private Double getMinTime() {
        if(tiempoComprometidoBici.size() == 0) {
            return getMinMotos();
        }

        return Math.min(getMinMotos(),getMinBicis());

    }

    private Double getMinMotos() {
        return tiempoComprometidoMotos.stream().min(Double::compare).orElse(0D);
    }

    private Double getMinBicis() {
        return tiempoComprometidoMotos.stream().min(Double::compare).orElse(0D);
    }

}
