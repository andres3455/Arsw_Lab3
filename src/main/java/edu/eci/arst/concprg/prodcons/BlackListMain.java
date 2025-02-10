package edu.eci.arst.concprg.prodcons;

import java.util.List;

/**
 * Clase principal para probar la detección de servidores en listas negras.
 */
public class BlackListMain {
    
    private static final String TARGET_IP = "202.24.34.55";
    private static final int THREAD_COUNT = 50;

    public static void main(String[] args) {
        HostBlackListsValidator validator = new HostBlackListsValidator();
        List<Integer> blackListOccurrences = validator.checkHost(TARGET_IP, THREAD_COUNT);

        System.out.println(String.format("El host %s fue encontrado en las siguientes listas negras: %s", 
                                          TARGET_IP, blackListOccurrences));

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        System.out.println(String.format("Número de núcleos disponibles: %d", availableProcessors));
    }
}
