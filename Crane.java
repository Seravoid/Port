public abstract class Crane {
    private int id;
    private boolean busy;
    private Ship currentShip;
    private double workProgress;
    private boolean weatherAffected;
    protected double baseUnloadRate;
    protected double currentUnloadRate;
    
    public Crane(int id, double baseUnloadRate) {
        this.id = id;
        this.baseUnloadRate = baseUnloadRate;
        this.currentUnloadRate = baseUnloadRate;
        this.busy = false;
        this.currentShip = null;
        this.workProgress = 0;
        this.weatherAffected = false;
    }
    
    public int getId() { return id; }
    public boolean isBusy() { return busy; }
    public Ship getCurrentShip() { return currentShip; }
    public double getWorkProgress() { return workProgress; }
    public boolean isWeatherAffected() { return weatherAffected; }
    public double getBaseUnloadRate() { return baseUnloadRate; }
    public double getCurrentUnloadRate() { return currentUnloadRate; }
    
    public void setWeatherAffected(boolean weatherAffected) {
        this.weatherAffected = weatherAffected;
        if (weatherAffected) {
            this.currentUnloadRate = baseUnloadRate * 0.7;
        } else {
            this.currentUnloadRate = baseUnloadRate;
        }
    }
    
    public void startUnload(Ship ship) {
        this.busy = true;
        this.currentShip = ship;
        this.workProgress = 0;
    }
    
    public boolean unloadDay() {
        if (!busy || currentShip == null) return false;
        
        double dailyRate = currentUnloadRate;
        double remaining = currentShip.getWeight() * (1 - workProgress);
        
        if (remaining <= dailyRate) {
            currentShip.setUnloaded(true);
            currentShip.setActualUnloadDays(
                currentShip.getActualUnloadDays() + (remaining / dailyRate)
            );
            this.busy = false;
            this.currentShip = null;
            this.workProgress = 0;
            return true;
        } else {
            workProgress += dailyRate / currentShip.getWeight();
            currentShip.setActualUnloadDays(
                currentShip.getActualUnloadDays() + 1
            );
            return false;
        }
    }
    
    public String getStatus() {
        if (busy) {
            String weatherMark = weatherAffected ? " (погода)" : "";
            return "Разгружает " + currentShip.getName() + 
                   " (" + String.format("%.1f", workProgress * 100) + "%)" + weatherMark;
        }
        return "Свободен";
    }
    
    public String getCurrentShipName() {
        return currentShip != null ? currentShip.getName() : "-";
    }
    
    public String getProgressText() {
        if (!busy) return "-";
        return String.format("%.0f%%", workProgress * 100);
    }
    
    public abstract String getCargoTypeName();
    public abstract boolean canHandle(Ship ship);
    
    
    public static class BulkCrane extends Crane {
        public BulkCrane(int id, double unloadRate) {
            super(id, unloadRate);
        }
        
        @Override
        public String getCargoTypeName() {
            return "Сыпучие";
        }
        
        @Override
        public boolean canHandle(Ship ship) {
            return ship.getCargoType().equals(Constants.CARGO_BULK);
        }
    }
    
    public static class LiquidCrane extends Crane {
        public LiquidCrane(int id, double unloadRate) {
            super(id, unloadRate);
        }
        
        @Override
        public String getCargoTypeName() {
            return "Жидкие";
        }
        
        @Override
        public boolean canHandle(Ship ship) {
            return ship.getCargoType().equals(Constants.CARGO_LIQUID);
        }
    }
    
    public static class ContainerCrane extends Crane {
        public ContainerCrane(int id, double unloadRate) {
            super(id, unloadRate);
        }
        
        @Override
        public String getCargoTypeName() {
            return "Контейнеры";
        }
        
        @Override
        public boolean canHandle(Ship ship) {
            return ship.getCargoType().equals(Constants.CARGO_CONTAINER);
        }
    }
}