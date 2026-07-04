import java.time.LocalDate;
import java.util.*;

public class Port {
    private List<Ship> allShips;
    private List<Ship> waitingQueue;
    private List<Ship> unloadedShips;
    private List<Crane> cranes;
    private double totalPenalty;
    private int totalShipsProcessed;
    private double maxQueueLength;
    private double totalWaitTime;
    private double totalUnloadTime;
    private double maxWaitTime;
    private double maxUnloadDelay;
    private Random random;
    private boolean badWeather;
    
    public Port() {
        this.allShips = new ArrayList<Ship>();
        this.waitingQueue = new ArrayList<Ship>();
        this.unloadedShips = new ArrayList<Ship>();
        this.cranes = new ArrayList<Crane>();
        this.totalPenalty = 0;
        this.totalShipsProcessed = 0;
        this.maxQueueLength = 0;
        this.totalWaitTime = 0;
        this.totalUnloadTime = 0;
        this.maxWaitTime = 0;
        this.maxUnloadDelay = 0;
        this.random = new Random();
        this.badWeather = false;
    }
    
    public void addCrane(Crane crane) {
        cranes.add(crane);
    }
    
    public void addShip(Ship ship) {
        allShips.add(ship);
        waitingQueue.add(ship);
        updateQueueStats();
    }
    
    public void processDay(LocalDate date) {
        badWeather = random.nextDouble() < 0.4;
        
        for (int i = 0; i < cranes.size(); i++) {
            Crane crane = cranes.get(i);
            crane.setWeatherAffected(badWeather && crane.isBusy());
        }
        
        for (int i = 0; i < waitingQueue.size(); i++) {
            Ship ship = waitingQueue.get(i);
            if (!ship.isUnloaded()) {
                ship.setActualWaitDays(ship.getActualWaitDays() + 1);
            }
        }
        
        assignShipsToCranes();
        
        for (int i = 0; i < cranes.size(); i++) {
            Crane crane = cranes.get(i);
            if (crane.isBusy()) {
                crane.unloadDay();
            }
        }
        
        assignShipsToCranes();
        
        updateQueueStats();
        checkCompletedShips();
    }
    
    private void assignShipsToCranes() {
        for (int i = 0; i < cranes.size(); i++) {
            Crane crane = cranes.get(i);
            if (crane.isBusy()) continue;
            
            for (int j = 0; j < waitingQueue.size(); j++) {
                Ship ship = waitingQueue.get(j);
                if (!ship.isUnloaded() && crane.canHandle(ship)) {
                    crane.startUnload(ship);
                    waitingQueue.remove(j);
                    crane.setWeatherAffected(badWeather);
                    break;
                }
            }
        }
    }
    
    private void updateQueueStats() {
        int queueSize = waitingQueue.size();
        if (queueSize > maxQueueLength) {
            maxQueueLength = queueSize;
        }
    }
    
    private void checkCompletedShips() {
        for (int i = allShips.size() - 1; i >= 0; i--) {
            Ship ship = allShips.get(i);
            if (ship.isUnloaded() && !unloadedShips.contains(ship)) {
                unloadedShips.add(ship);
                totalShipsProcessed++;
                totalPenalty += ship.getTotalPenalty();
                totalWaitTime += ship.getActualWaitDays();
                totalUnloadTime += ship.getActualUnloadDays();
                
                if (ship.getActualWaitDays() > maxWaitTime) {
                    maxWaitTime = ship.getActualWaitDays();
                }
                double delay = (ship.getActualWaitDays() + ship.getActualUnloadDays()) - ship.getPlannedStayDays();
                if (delay > maxUnloadDelay) {
                    maxUnloadDelay = delay;
                }
                
                waitingQueue.remove(ship);
            }
        }
    }
    
    public List<Ship> getWaitingQueue() { return waitingQueue; }
    public List<Ship> getUnloadedShips() { return unloadedShips; }
    public List<Crane> getCranes() { return cranes; }
    public List<Ship> getAllShips() { return allShips; }
    public double getTotalPenalty() { return totalPenalty; }
    public int getTotalShipsProcessed() { return totalShipsProcessed; }
    public double getMaxQueueLength() { return maxQueueLength; }
    public double getTotalWaitTime() { return totalWaitTime; }
    public double getTotalUnloadTime() { return totalUnloadTime; }
    public double getMaxWaitTime() { return maxWaitTime; }
    public double getMaxUnloadDelay() { return maxUnloadDelay; }
    public boolean isBadWeather() { return badWeather; }
    
    public double getAverageWaitTime() {
        return totalShipsProcessed > 0 ? totalWaitTime / totalShipsProcessed : 0;
    }
    
    public double getAverageUnloadTime() {
        return totalShipsProcessed > 0 ? totalUnloadTime / totalShipsProcessed : 0;
    }
    
    public double getAverageQueueLength() {
        return totalShipsProcessed > 0 ? maxQueueLength : 0;
    }
    
    public int getBusyCranesCount() {
        int count = 0;
        for (int i = 0; i < cranes.size(); i++) {
            if (cranes.get(i).isBusy()) count++;
        }
        return count;
    }
}