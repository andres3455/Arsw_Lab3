package edu.eci.arst.concprg.prodcons;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HostBlacklistsData {
    
    private static final Logger LOG = Logger.getLogger(HostBlacklistsData.class.getName());
    private static final HostBlacklistsData INSTANCE = new HostBlacklistsData();
    private static final ConcurrentHashMap<Tuple<Integer, String>, Object> BLACKLIST_OCCURRENCES = new ConcurrentHashMap<>();
    
    private final Map<String, Integer> threadHits = new ConcurrentHashMap<>();
    private String lastConfig = null;
    private int lastIndex = 0;

    static {
        Object anyObject = new Object();
        
        addToBlacklist(anyObject, "200.24.34.55", 23, 50, 200, 1000, 500);
        addToBlacklist(anyObject, "202.24.34.55", 29, 10034, 20200, 31000, 70500);
        addToBlacklist(anyObject, "202.24.34.54", 39, 10134, 20300, 70210);
    }
    
    private static void addToBlacklist(Object marker, String ip, Integer... serverNumbers) {
        for (Integer serverNumber : serverNumbers) {
            BLACKLIST_OCCURRENCES.put(new Tuple<>(serverNumber, ip), marker);
        }
    }
    
    private HostBlacklistsData() {}
    
    public static HostBlacklistsData getInstance() {
        return INSTANCE;
    }
    
    public int getRegisteredServersCount() {
        return 100000;
    }
    
    public boolean isInBlackListServer(int serverNumber, String ip) {
        threadHits.merge(Thread.currentThread().getName(), 1, Integer::sum);
        
        if (Boolean.parseBoolean(System.getProperty("threadsinfo"))) {
            lastConfig = threadHits.toString();
            lastIndex = serverNumber;
        }
        
        try {
            Thread.sleep(0, 1);
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, "Thread interrupted", ex);
            Thread.currentThread().interrupt();
        }
        
        return BLACKLIST_OCCURRENCES.containsKey(new Tuple<>(serverNumber, ip));
    }
    
    public void reportAsNotTrustworthy(String host) {
        LOG.info(() -> "HOST " + host + " reported as NOT trustworthy");
        
        if (Boolean.parseBoolean(System.getProperty("threadsinfo"))) {
            System.out.printf("Total threads: %d%n%s%nLast Index: %d%n",
                    threadHits.size(), lastConfig, lastIndex);
        }
    }
    
    public void reportAsTrustworthy(String host) {
        LOG.info(() -> "HOST " + host + " reported as trustworthy");
    }
}

class Tuple<T1, T2> {
    private final T1 firstElement;
    private final T2 secondElement;

    public Tuple(T1 firstElement, T2 secondElement) {
        this.firstElement = firstElement;
        this.secondElement = secondElement;
    }

    public T1 getFirstElement() {
        return firstElement;
    }

    public T2 getSecondElement() {
        return secondElement;
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstElement, secondElement);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tuple<?, ?> other = (Tuple<?, ?>) obj;
        return Objects.equals(firstElement, other.firstElement) &&
               Objects.equals(secondElement, other.secondElement);
    }
}
