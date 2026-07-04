import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.time.LocalDate;
import java.util.*;

public class Controller {
    
    private Port port;
    private int timeStep = 0;
    private LocalDate currentDate = LocalDate.of(2025, 1, 1);
    private Random random = new Random();
    private ObservableList<String> eventLog = FXCollections.observableArrayList();
    
    @FXML private Label dayLabel;
    @FXML private Label dateLabel;
    @FXML private Label totalPenaltyLabel;
    @FXML private Label processedCountLabel;
    @FXML private Label queueLengthLabel;
    @FXML private Label avgWaitTimeLabel;
    @FXML private Label avgUnloadTimeLabel;
    @FXML private Label avgQueueLengthLabel;
    @FXML private Label maxWaitTimeLabel;
    @FXML private Label maxUnloadDelayLabel;
    @FXML private Label busyCranesLabel;
    @FXML private Label weatherLabel;
    @FXML private ComboBox<String> filterCombo;
    @FXML private ListView<String> eventListView;
    @FXML private GridPane portLayout;
    
    @FXML private TableView<Ship> waitingQueueTable;
    @FXML private TableView<Ship> unloadedShipsTable;
    @FXML private TableView<StatRow> statsTable;
    
    @FXML private TableColumn<Ship, Integer> colWaitId;
    @FXML private TableColumn<Ship, String> colWaitName;
    @FXML private TableColumn<Ship, String> colWaitCargo;
    @FXML private TableColumn<Ship, String> colWaitStatus;
    @FXML private TableColumn<Ship, String> colWaitType;
    @FXML private TableColumn<Ship, Double> colWaitWeight;
    @FXML private TableColumn<Ship, Double> colWaitDays;
    @FXML private TableColumn<Ship, Double> colWaitPenalty;
    @FXML private TableColumn<Ship, Integer> colWaitPlanned;
    
    @FXML private TableColumn<Ship, Integer> colDoneId;
    @FXML private TableColumn<Ship, String> colDoneName;
    @FXML private TableColumn<Ship, String> colDoneCargo;
    @FXML private TableColumn<Ship, String> colDoneType;
    @FXML private TableColumn<Ship, Double> colDoneWeight;
    @FXML private TableColumn<Ship, Double> colDoneWait;
    @FXML private TableColumn<Ship, Double> colDonePenalty;
    @FXML private TableColumn<Ship, Double> colDoneUnloadTime;
    
    @FXML private TableColumn<StatRow, String> colStatsType;
    @FXML private TableColumn<StatRow, Integer> colStatsTotal;
    @FXML private TableColumn<StatRow, Integer> colStatsUnloaded;
    @FXML private TableColumn<StatRow, Integer> colStatsWaiting;
    @FXML private TableColumn<StatRow, Double> colStatsPenalty;
    
    @FXML
    public void initialize() {
        filterCombo.setItems(FXCollections.observableArrayList(
            "Все суда",
            "В очереди",
            "Разгружены",
            "Танкеры",
            "Сухогрузы"
        ));
        filterCombo.setValue("Все суда");
        
        setupTables();
        initSimulation();
        refreshAll();
        updatePortLayout();
        updateEventLog("Симуляция запущена");
    }
    
    private void setupTables() {
        // Очередь ожидания
        colWaitId.setCellValueFactory(new PropertyValueFactory<Ship, Integer>("id"));
        colWaitName.setCellValueFactory(new PropertyValueFactory<Ship, String>("name"));
        colWaitCargo.setCellValueFactory(new PropertyValueFactory<Ship, String>("cargoTypeName"));
        colWaitWeight.setCellValueFactory(new PropertyValueFactory<Ship, Double>("weight"));
        colWaitDays.setCellValueFactory(new PropertyValueFactory<Ship, Double>("actualWaitDays"));
        colWaitPlanned.setCellValueFactory(new PropertyValueFactory<Ship, Integer>("plannedStayDays"));
        colWaitPenalty.setCellValueFactory(new PropertyValueFactory<Ship, Double>("totalPenalty"));
        colWaitStatus.setCellValueFactory(new PropertyValueFactory<Ship, String>("status"));
        colWaitType.setCellValueFactory(new PropertyValueFactory<Ship, String>("shipTypeName"));
        
        // Разгруженные суда
        colDoneId.setCellValueFactory(new PropertyValueFactory<Ship, Integer>("id"));
        colDoneName.setCellValueFactory(new PropertyValueFactory<Ship, String>("name"));
        colDoneCargo.setCellValueFactory(new PropertyValueFactory<Ship, String>("cargoTypeName"));
        colDoneType.setCellValueFactory(new PropertyValueFactory<Ship, String>("shipTypeName"));
        colDoneWeight.setCellValueFactory(new PropertyValueFactory<Ship, Double>("weight"));
        colDoneWait.setCellValueFactory(new PropertyValueFactory<Ship, Double>("actualWaitDays"));
        colDonePenalty.setCellValueFactory(new PropertyValueFactory<Ship, Double>("totalPenalty"));
        colDoneUnloadTime.setCellValueFactory(new PropertyValueFactory<Ship, Double>("actualUnloadDays"));
        
        // Статистика по типам грузов
        colStatsType.setCellValueFactory(new PropertyValueFactory<StatRow, String>("type"));
        colStatsTotal.setCellValueFactory(new PropertyValueFactory<StatRow, Integer>("total"));
        colStatsUnloaded.setCellValueFactory(new PropertyValueFactory<StatRow, Integer>("unloaded"));
        colStatsWaiting.setCellValueFactory(new PropertyValueFactory<StatRow, Integer>("waiting"));
        colStatsPenalty.setCellValueFactory(new PropertyValueFactory<StatRow, Double>("avgPenalty"));
    }
    
    private void initSimulation() {
        port = new Port();
        
        port.addCrane(new Crane.BulkCrane(1, 350));
        port.addCrane(new Crane.BulkCrane(2, 350));
        port.addCrane(new Crane.LiquidCrane(3, 300));
        port.addCrane(new Crane.LiquidCrane(4, 300));
        port.addCrane(new Crane.ContainerCrane(5, 400));
        port.addCrane(new Crane.ContainerCrane(6, 400));
        
        addRandomShip(true);
        addRandomShip(false);
        addRandomShip(true);
        addRandomShip(false);
        
        refreshAll();
    }
    
    private void addRandomShip(boolean isTanker) {
        String[] names = {"Альбатрос", "Бриз", "Волна", "Горизонт", "Дельфин", 
                          "Енисей", "Жемчужина", "Заря", "Изумруд", "Корвет",
                          "Скала", "Прибой", "Шторм", "Тайфун", "Маяк"};
        String[] cargoTypes = {Constants.CARGO_BULK, Constants.CARGO_LIQUID, Constants.CARGO_CONTAINER};
        String[] bulkNames = {"Зерно", "Уголь", "Песок", "Руда"};
        String[] liquidNames = {"Нефть", "Химикаты", "СПГ", "Мазут"};
        String[] containerNames = {"Стандартные", "Рефрижераторные", "Открытые"};
        
        String name = names[random.nextInt(names.length)] + " " + (port.getAllShips().size() + 1);
        String cargoType = cargoTypes[random.nextInt(cargoTypes.length)];
        double weight = 500 + random.nextDouble() * 1500;
        int stayDays = 3 + random.nextInt(4);
        double penalty = 1500 + random.nextDouble() * 3500;
        LocalDate arrival = currentDate;
        
        Cargo cargo;
        if (cargoType.equals(Constants.CARGO_BULK)) {
            String cargoName = bulkNames[random.nextInt(bulkNames.length)];
            cargo = new Cargo.BulkCargo(cargoName, weight);
        } else if (cargoType.equals(Constants.CARGO_LIQUID)) {
            String cargoName = liquidNames[random.nextInt(liquidNames.length)];
            cargo = new Cargo.LiquidCargo(cargoName, weight);
        } else {
            String cargoName = containerNames[random.nextInt(containerNames.length)];
            cargo = new Cargo.ContainerCargo(cargoName, weight);
        }
        
        Ship ship;
        if (isTanker) {
            ship = new Ship.TankerShip(name, cargo, arrival, stayDays, penalty);
        } else {
            ship = new Ship.DryCargoShip(name, cargo, arrival, stayDays, penalty);
        }
        
        port.addShip(ship);
        updateEventLog("Прибыло судно " + ship.getName() + " (" + ship.getShipTypeName() + ", " + cargo.getTypeName() + ")");
        refreshAll();
    }
    
    @FXML
    public void nextDay() {
        currentDate = currentDate.plusDays(1);
        timeStep++;
        
        if (random.nextDouble() < 0.4) {
            addRandomShip(random.nextBoolean());
        }
        
        port.processDay(currentDate);
        
        updateEventLog("День " + timeStep + " завершён. Погода: " + 
                      (port.isBadWeather() ? "Плохая" : "Хорошая"));
        
        dayLabel.setText(String.valueOf(timeStep));
        dateLabel.setText(currentDate.toString());
        refreshAll();
        updatePortLayout();
    }
    
    @FXML
    public void addShip() {
        addRandomShip(random.nextBoolean());
        refreshAll();
        updatePortLayout();
    }
    
    private void refreshAll() {
        totalPenaltyLabel.setText(String.format("%.2f", port.getTotalPenalty()));
        processedCountLabel.setText(String.valueOf(port.getTotalShipsProcessed()));
        queueLengthLabel.setText(String.valueOf(port.getWaitingQueue().size()));
        avgWaitTimeLabel.setText(String.format("%.1f", port.getAverageWaitTime()));
        avgUnloadTimeLabel.setText(String.format("%.1f", port.getAverageUnloadTime()));
        avgQueueLengthLabel.setText(String.format("%.1f", port.getAverageQueueLength()));
        maxWaitTimeLabel.setText(String.format("%.1f", port.getMaxWaitTime()));
        maxUnloadDelayLabel.setText(String.format("%.1f", port.getMaxUnloadDelay()));
        
        int busyCranes = port.getBusyCranesCount();
        busyCranesLabel.setText(busyCranes + "/" + port.getCranes().size());
        
        weatherLabel.setText(port.isBadWeather() ? "Плохая" : "Хорошая");
        weatherLabel.setStyle(port.isBadWeather() ? 
            "-fx-text-fill: #ef5350; -fx-font-weight: bold;" : 
            "-fx-text-fill: #4fc3f7; -fx-font-weight: bold;");
        
        updateWaitingQueueTable();
        updateUnloadedTable();
        updateStatsTable();
    }
    
    private void updateWaitingQueueTable() {
        ObservableList<Ship> filtered = FXCollections.observableArrayList();
        String filter = filterCombo.getValue();
        
        for (int i = 0; i < port.getWaitingQueue().size(); i++) {
            Ship ship = port.getWaitingQueue().get(i);
            boolean add = false;
            if ("Все суда".equals(filter)) {
                add = true;
            } else if ("В очереди".equals(filter) && !ship.isUnloaded()) {
                add = true;
            } else if ("Разгружены".equals(filter) && ship.isUnloaded()) {
                add = true;
            } else if ("Танкеры".equals(filter) && ship instanceof Ship.TankerShip) {
                add = true;
            } else if ("Сухогрузы".equals(filter) && ship instanceof Ship.DryCargoShip) {
                add = true;
            }
            if (add) filtered.add(ship);
        }
        
        waitingQueueTable.setItems(filtered);
    }
    
    private void updateUnloadedTable() {
        unloadedShipsTable.setItems(
            FXCollections.observableArrayList(port.getUnloadedShips())
        );
    }
    
    private void updateStatsTable() {
        ObservableList<StatRow> stats = FXCollections.observableArrayList();
        String[] types = {Constants.CARGO_BULK, Constants.CARGO_LIQUID, Constants.CARGO_CONTAINER};
        String[] typeNames = {"Сыпучие", "Жидкие", "Контейнеры"};
        
        for (int t = 0; t < types.length; t++) {
            String type = types[t];
            int total = 0;
            int unloaded = 0;
            int waiting = 0;
            double totalPenalty = 0;
            
            List<Ship> allShips = port.getAllShips();
            for (int i = 0; i < allShips.size(); i++) {
                Ship ship = allShips.get(i);
                if (ship.getCargoType().equals(type)) {
                    total++;
                    if (ship.isUnloaded()) {
                        unloaded++;
                        totalPenalty += ship.getTotalPenalty();
                    } else {
                        waiting++;
                    }
                }
            }
            
            double avgPenalty = unloaded > 0 ? totalPenalty / unloaded : 0;
            stats.add(new StatRow(typeNames[t], total, unloaded, waiting, avgPenalty));
        }
        statsTable.setItems(stats);
    }
    
    @FXML
    public void applyFilter() {
        updateWaitingQueueTable();
    }
    
    private void updatePortLayout() {
        portLayout.getChildren().clear();
        int col = 0;
        int row = 0;
        
        List<Crane> cranes = port.getCranes();
        for (int i = 0; i < cranes.size(); i++) {
            Crane crane = cranes.get(i);
            
            Rectangle rect = new Rectangle(80, 30);
            rect.setArcWidth(10);
            rect.setArcHeight(10);
            
            if (crane.isBusy()) {
                if (crane.isWeatherAffected()) {
                    rect.setFill(Color.rgb(255, 152, 0));
                } else {
                    rect.setFill(Color.rgb(76, 175, 80));
                }
            } else {
                rect.setFill(Color.rgb(158, 158, 158));
            }
            
            String progressText = "";
            if (crane.isBusy()) {
                progressText = "\n" + String.format("%.0f%%", crane.getWorkProgress() * 100);
            }
            
            Label label = new Label("Кран " + crane.getId() + "\n" + 
                                   crane.getCargoTypeName() + progressText);
            label.setTextFill(Color.WHITE);
            label.setStyle("-fx-font-size: 10px; -fx-alignment: center;");
            
            VBox cell = new VBox(rect, label);
            cell.setAlignment(javafx.geometry.Pos.CENTER);
            cell.setSpacing(3);
            
            portLayout.add(cell, col, row);
            col++;
            if (col > 2) {
                col = 0;
                row++;
            }
        }
    }
    
    private void updateEventLog(String message) {
        eventLog.add(0, message);
        if (eventLog.size() > 30) {
            eventLog.remove(eventLog.size() - 1);
        }
        eventListView.setItems(eventLog);
    }
    
    @FXML
    public void resetSimulation() {
        initSimulation();
        timeStep = 0;
        currentDate = LocalDate.of(2025, 1, 1);
        dayLabel.setText("0");
        dateLabel.setText(currentDate.toString());
        eventLog.clear();
        updateEventLog("Симуляция сброшена");
        refreshAll();
        updatePortLayout();
    }
    
    public static class StatRow {
        private String type;
        private int total;
        private int unloaded;
        private int waiting;
        private double avgPenalty;
        
        public StatRow(String type, int total, int unloaded, int waiting, double avgPenalty) {
            this.type = type;
            this.total = total;
            this.unloaded = unloaded;
            this.waiting = waiting;
            this.avgPenalty = avgPenalty;
        }
        
        public String getType() { return type; }
        public int getTotal() { return total; }
        public int getUnloaded() { return unloaded; }
        public int getWaiting() { return waiting; }
        public double getAvgPenalty() { return avgPenalty; }
    }
}