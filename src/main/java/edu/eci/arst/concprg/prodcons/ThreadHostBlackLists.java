package edu.eci.arst.concprg.prodcons;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Acer
 */
public class ThreadHostBlackLists extends Thread {
    private int a;
    private int b;
    private HostBlacklistsDataSourceFacade skds;
    private int checkedListsCount = 0;
    private int ocurrencesCount = 0;
    private String ipaddress;
    private final int BLACK_LIST_ALARM_COUNT = 5;
    private BlackListController controlador;
    private List<Integer> blacklistedServers; // Lista de servidores en blacklist

    public ThreadHostBlackLists(int a, int b, HostBlacklistsDataSourceFacade skds, String ipaddress, BlackListController controlador) {
        this.a = a;
        this.b = b;
        this.skds = skds;
        this.ipaddress = ipaddress;
        this.controlador = controlador;
        this.blacklistedServers = new ArrayList<>(); // Inicializa la lista
    }

    @Override
    public void run() {  
        for (int i = a; i <= b && ocurrencesCount < BLACK_LIST_ALARM_COUNT; i++) {
            checkedListsCount++;
            if (controlador.validate()) {
                break;
            }
            if (skds.isInBlackListServer(i, ipaddress)) {
                blacklistedServers.add(i); // Agregar el servidor a la lista
                if (controlador.IncrementOcurrence()) {
                    ocurrencesCount++;
                } else {
                    break;
                }
            }
        }
    }

    public synchronized int getOcurrencesCount() {
        return ocurrencesCount;
    }

    public int getCheckedListsCount() {
        return checkedListsCount;
    }

    public List<Integer> getBlackListedServers() {
        return blacklistedServers; 
    }
}
