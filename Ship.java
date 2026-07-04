import java.time.LocalDate;

public abstract class Ship {
    private static int nextId = 1;
    private int id;
    private String name;
    protected Cargo cargo;
    private LocalDate arrivalDate;
    private int plannedStayDays;
    private double penaltyPerDay;
    private boolean unloaded;
    private double actualWaitDays;
    private double actualUnloadDays;
    
    public Ship(String name, Cargo cargo, LocalDate arrivalDate, 
                int plannedStayDays, double penaltyPerDay) {
        this.id = nextId++;
        this.name = name;
        this.cargo = cargo;
        this.arrivalDate = arrivalDate;
        this.plannedStayDays = plannedStayDays;
        this.penaltyPerDay = penaltyPerDay;
        this.unloaded = false;
        this.actualWaitDays = 0;
        this.actualUnloadDays = 0;
    }
    
    public int getId() { return id; }
    public String getName() { return name; }
    public Cargo getCargo() { return cargo; }
    public LocalDate getArrivalDate() { return arrivalDate; }
    public int getPlannedStayDays() { return plannedStayDays; }
    public double getPenaltyPerDay() { return penaltyPerDay; }
    public boolean isUnloaded() { return unloaded; }
    public void setUnloaded(boolean unloaded) { this.unloaded = unloaded; }
    public double getActualWaitDays() { return actualWaitDays; }
    public void setActualWaitDays(double actualWaitDays) { this.actualWaitDays = actualWaitDays; }
    public double getActualUnloadDays() { return actualUnloadDays; }
    public void setActualUnloadDays(double actualUnloadDays) { this.actualUnloadDays = actualUnloadDays; }
    
    public String getCargoType() { return cargo.getTypeCode(); }
    public String getCargoTypeName() { return cargo.getTypeName(); }
    public double getWeight() { return cargo.getWeight(); }
    
    public double getTotalPenalty() {
        double extraDays = (actualWaitDays + actualUnloadDays) - plannedStayDays;
        return extraDays > 0 ? extraDays * penaltyPerDay : 0;
    }
    
    public String getStatus() {
        if (unloaded) return "Разгружено";
        if (actualWaitDays > 0) return "Ожидает разгрузки";
        return "Прибыло";
    }
    
    public abstract String getShipTypeName();
    
    
    public static class TankerShip extends Ship {
        public TankerShip(String name, Cargo cargo, LocalDate arrivalDate, 
                          int plannedStayDays, double penaltyPerDay) {
            super(name, cargo, arrivalDate, plannedStayDays, penaltyPerDay);
        }
        
        @Override
        public String getShipTypeName() {
            return "Танкер";
        }
    }
    
    public static class DryCargoShip extends Ship {
        public DryCargoShip(String name, Cargo cargo, LocalDate arrivalDate, 
                            int plannedStayDays, double penaltyPerDay) {
            super(name, cargo, arrivalDate, plannedStayDays, penaltyPerDay);
        }
        
        @Override
        public String getShipTypeName() {
            return "Сухогруз";
        }
    }
}