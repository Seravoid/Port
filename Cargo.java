public abstract class Cargo {
    protected String name;
    protected String typeCode;
    protected double weight;
    
    public Cargo(String name, String typeCode, double weight) {
        this.name = name;
        this.typeCode = typeCode;
        this.weight = weight;
    }
    
    public String getName() { return name; }
    public String getTypeCode() { return typeCode; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    
    public abstract String getTypeName();
    

    
    public static class BulkCargo extends Cargo {
        public BulkCargo(String name, double weight) {
            super(name, Constants.CARGO_BULK, weight);
        }
        
        @Override
        public String getTypeName() {
            return "Сыпучие (" + name + ")";
        }
    }
    
    public static class LiquidCargo extends Cargo {
        public LiquidCargo(String name, double weight) {
            super(name, Constants.CARGO_LIQUID, weight);
        }
        
        @Override
        public String getTypeName() {
            return "Жидкие (" + name + ")";
        }
    }
    
    public static class ContainerCargo extends Cargo {
        public ContainerCargo(String name, double weight) {
            super(name, Constants.CARGO_CONTAINER, weight);
        }
        
        @Override
        public String getTypeName() {
            return "Контейнеры (" + name + ")";
        }
    }
}