package edu.eci.arst.concprg.prodcons;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT = 5;
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
    private final HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();
    
    public List<Integer> checkHost(String ipAddress, int numThreads) {
        int totalServers = skds.getRegisteredServersCount();
        int rangePerThread = totalServers / numThreads;
        int remainder = totalServers % numThreads;
        
        List<ThreadHostBlackLists> threads = new ArrayList<>();
        BlackListController controller = new BlackListController();
        
        for (int i = 0; i < numThreads; i++) {
            int start = i * rangePerThread + Math.min(i, remainder);
            int end = start + rangePerThread + (i < remainder ? 1 : 0) - 1;
            
            ThreadHostBlackLists thread = new ThreadHostBlackLists(start, end, skds, ipAddress, controller);
            threads.add(thread);
            thread.start();
        }
        
        List<Integer> blackListOccurrences = new ArrayList<>();
        int occurrencesCount = 0;
        int checkedListsCount = 0;
        
        for (ThreadHostBlackLists thread : threads) {
            try {
                thread.join();
                occurrencesCount += thread.getOcurrencesCount();
                checkedListsCount += thread.getCheckedListsCount();
                blackListOccurrences.addAll(thread.getBlackListedServers());
            } catch (InterruptedException e) {
                LOG.log(Level.SEVERE, "Thread interrupted", e);
            }
        }
        
        if (occurrencesCount >= BLACK_LIST_ALARM_COUNT) {
            skds.reportAsNotTrustworthy(ipAddress);
        } else {
            skds.reportAsTrustworthy(ipAddress);
        }
        
        LOG.log(Level.INFO, "Checked Black Lists: {0} of {1}", new Object[]{checkedListsCount, totalServers});
        
        return blackListOccurrences;
    }
}
