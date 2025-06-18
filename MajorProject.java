package application;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;

public class Main extends Application {

    // Backend data structures (mostly similar to original)
    static HashMap<String, Integer> menu = new HashMap<>();
    static HashMap<String, Integer> itemCost = new HashMap<>();
    static HashMap<String, Integer> prepTime = new HashMap<>();
    static ObservableList<String> menuItems = FXCollections.observableArrayList(); // Use ObservableList for UI
    static HashMap<String, List<String>> itemIngredients = new HashMap<>();

    static HashMap<String, Integer> inventory = new HashMap<>();
    static double totalRevenue = 0;
    static double totalCostOfGoodsSold = 0;

    static HashMap<String, List<String>> customerOrders = new HashMap<>();
    static HashMap<String, Integer> unpaidBills = new HashMap<>();

    static HashMap<Integer, String> tableReservations = new HashMap<>();
    static ObservableList<String> feedbackList = FXCollections.observableArrayList(); // Use ObservableList for UI
    static HashSet<String> uniqueCustomers = new HashSet<>();

    static int totalOrdersPlaced = 0;

    static HashMap<String, String> employees = new HashMap<>();

    // Current customer logged in
    private String currentCustomer = "";

    // UI Components (declared globally for access from different methods)
    private TabPane mainTabPane;
    private ListView<String> menuListView;
    private ListView<String> customerOrderListView;
    private Label currentBillLabel;
    private TextField orderItemField;
    private TextField reserveTableField;
    private ListView<String> customerReservedTablesListView;
    private TextArea feedbackTextArea;
    private ListView<String> adminFeedbackListView;
    private ListView<Map.Entry<String, Integer>> inventoryListView;
    private TextField inventoryItemNameField;
    private TextField inventoryQuantityField;
    private Label financialReportLabel;
    private ListView<Map.Entry<String, Integer>> pendingPaymentsListView;
    private Label operationalMetricsLabel;
    private ListView<Map.Entry<String, String>> employeesListView;
    private TextField employeeNameField;
    private TextField employeePositionField;
    private VBox adminContent; // Declare adminContent as a class field

    @Override
    public void start(Stage primaryStage) {
        initializeData(); // Initialize all data
        primaryStage.setTitle("Fast Food Restaurant Management System");

        mainTabPane = new TabPane();
        mainTabPane.getTabs().addAll(createLoginTab(), createCustomerTab(), createAdminTab());
        mainTabPane.getSelectionModel().select(0); // Start on the Login tab

        Scene scene = new Scene(mainTabPane, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // --- Tab Creation Methods ---

    private Tab createLoginTab() {
        Tab loginTab = new Tab("Login");
        loginTab.setClosable(false);

        VBox loginLayout = new VBox(20);
        loginLayout.setAlignment(Pos.CENTER);
        loginLayout.setPadding(new Insets(50));

        Label titleLabel = new Label("Fast Food Restaurant");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button customerLoginButton = new Button("Customer Login");
        customerLoginButton.setMinSize(200, 50);
        customerLoginButton.setOnAction(e -> showCustomerLoginDialog());

        Button adminLoginButton = new Button("Admin Login");
        adminLoginButton.setMinSize(200, 50);
        adminLoginButton.setOnAction(e -> showAdminLoginDialog());

        // New: Exit Application Button
        Button exitApplicationButton = new Button("Exit Application");
        exitApplicationButton.setMinSize(200, 50);
        exitApplicationButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;"); // Red background for exit
        exitApplicationButton.setOnAction(e -> Platform.exit()); // This will close the application

        loginLayout.getChildren().addAll(titleLabel, customerLoginButton, adminLoginButton, exitApplicationButton); // Add the new button
        loginTab.setContent(loginLayout);
        return loginTab;
    }

    private Tab createCustomerTab() {
        Tab customerTab = new Tab("Customer Menu");
        customerTab.setClosable(false);
        customerTab.disableProperty().set(true); // Initially disabled

        BorderPane customerLayout = new BorderPane();
        customerLayout.setPadding(new Insets(10));

        // Left: Menu View
        VBox menuSection = new VBox(10);
        menuSection.setPadding(new Insets(10));
        menuSection.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5;");
        Label menuLabel = new Label("Our Menu:");
        menuLabel.setStyle("-fx-font-weight: bold;");
        menuListView = new ListView<>(menuItems);
        menuListView.setPrefHeight(250);
        menuSection.getChildren().addAll(menuLabel, menuListView);
        customerLayout.setLeft(menuSection);
        BorderPane.setMargin(menuSection, new Insets(0, 10, 0, 0));

        // Center: Ordering and Bill
        VBox centerSection = new VBox(15);
        centerSection.setPadding(new Insets(10));
        centerSection.setAlignment(Pos.TOP_CENTER);

        // Order section
        HBox orderInput = new HBox(10);
        orderInput.setAlignment(Pos.CENTER_LEFT);
        Label orderLabel = new Label("Order Item:");
        orderItemField = new TextField();
        orderItemField.setPromptText("Enter item name");
        Button placeOrderButton = new Button("Place Order");
        placeOrderButton.setOnAction(e -> placeOrderFX());
        orderInput.getChildren().addAll(orderLabel, orderItemField, placeOrderButton);

        Label currentOrderLabel = new Label("Your Current Order:");
        currentOrderLabel.setStyle("-fx-font-weight: bold;");
        customerOrderListView = new ListView<>();
        customerOrderListView.setPrefHeight(150);

        currentBillLabel = new Label("Current Bill: 0");
        currentBillLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Button payBillButton = new Button("View & Pay Bill");
        payBillButton.setOnAction(e -> viewAndPayBillFX());
        Button processOrderButton = new Button("Process Order");
        processOrderButton.setOnAction(e -> processOrdersFX());

        centerSection.getChildren().addAll(orderInput, currentOrderLabel, customerOrderListView, currentBillLabel,
                new HBox(10, payBillButton, processOrderButton));

        // Reservation section
        VBox reservationSection = new VBox(10);
        reservationSection.setPadding(new Insets(10));
        reservationSection.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5;");
        Label reservationLabel = new Label("Table Reservations:");
        reservationLabel.setStyle("-fx-font-weight: bold;");
        customerReservedTablesListView = new ListView<>(); // To show current customer's reservations
        customerReservedTablesListView.setPrefHeight(100);

        HBox reserveInput = new HBox(10);
        reserveInput.setAlignment(Pos.CENTER_LEFT);
        Label tableNumLabel = new Label("Table #:");
        reserveTableField = new TextField();
        reserveTableField.setPromptText("Enter table number");
        Button reserveButton = new Button("Reserve Table");
        reserveButton.setOnAction(e -> reserveTableFX());
        Button cancelReservationButton = new Button("Cancel Reservation");
        cancelReservationButton.setOnAction(e -> cancelTableReservationFX());
        reserveInput.getChildren().addAll(tableNumLabel, reserveTableField, reserveButton, cancelReservationButton);
        reservationSection.getChildren().addAll(reservationLabel, customerReservedTablesListView, reserveInput);

        // Feedback section
        VBox feedbackSection = new VBox(10);
        feedbackSection.setPadding(new Insets(10));
        feedbackSection.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5;");
        Label feedbackTitle = new Label("Submit Feedback:");
        feedbackTitle.setStyle("-fx-font-weight: bold;");
        feedbackTextArea = new TextArea();
        feedbackTextArea.setPromptText("Enter your feedback here...");
        feedbackTextArea.setWrapText(true);
        feedbackTextArea.setPrefHeight(80);
        Button submitFeedbackButton = new Button("Submit Feedback");
        submitFeedbackButton.setOnAction(e -> submitFeedbackFX());
        feedbackSection.getChildren().addAll(feedbackTitle, feedbackTextArea, submitFeedbackButton);

        // Logout Button for Customer (already present)
        Button customerLogoutButton = new Button("Logout");
        customerLogoutButton.setMaxWidth(Double.MAX_VALUE);
        customerLogoutButton.setOnAction(e -> logoutUser("customer"));

        customerLayout.setCenter(new VBox(20, centerSection, reservationSection, feedbackSection, customerLogoutButton));

        customerTab.setContent(customerLayout);
        return customerTab;
    }

    private Tab createAdminTab() {
        Tab adminTab = new Tab("Admin Panel");
        adminTab.setClosable(false);
        adminTab.disableProperty().set(true); // Initially disabled

        BorderPane adminLayout = new BorderPane();
        adminLayout.setPadding(new Insets(10));

        // Left Side Menu for Admin Options
        VBox adminMenu = new VBox(10);
        adminMenu.setPadding(new Insets(10));
        adminMenu.setAlignment(Pos.TOP_LEFT);
        adminMenu.setPrefWidth(200);

        Button viewFeedbackBtn = new Button("View Feedback");
        viewFeedbackBtn.setMaxWidth(Double.MAX_VALUE);
        viewFeedbackBtn.setOnAction(e -> showAdminView("feedback"));

        Button manageInventoryBtn = new Button("Manage Inventory");
        manageInventoryBtn.setMaxWidth(Double.MAX_VALUE);
        manageInventoryBtn.setOnAction(e -> showAdminView("inventory"));

        Button viewFinancialsBtn = new Button("View Financials");
        viewFinancialsBtn.setMaxWidth(Double.MAX_VALUE);
        viewFinancialsBtn.setOnAction(e -> showAdminView("financials"));

        Button checkPendingPaymentsBtn = new Button("Pending Payments");
        checkPendingPaymentsBtn.setMaxWidth(Double.MAX_VALUE);
        checkPendingPaymentsBtn.setOnAction(e -> showAdminView("pendingPayments"));

        Button viewOperationalMetricsBtn = new Button("Operational Metrics");
        viewOperationalMetricsBtn.setMaxWidth(Double.MAX_VALUE);
        viewOperationalMetricsBtn.setOnAction(e -> showAdminView("operationalMetrics"));

        Button manageEmployeesBtn = new Button("Manage Employees");
        manageEmployeesBtn.setMaxWidth(Double.MAX_VALUE);
        manageEmployeesBtn.setOnAction(e -> showAdminView("employees"));

        // New: Logout Button for Admin (Added here in the adminMenu)
        Button adminLogoutButton = new Button("Logout");
        adminLogoutButton.setMaxWidth(Double.MAX_VALUE);
        adminLogoutButton.setOnAction(e -> logoutUser("admin"));


        adminMenu.getChildren().addAll(viewFeedbackBtn, manageInventoryBtn, viewFinancialsBtn,
                checkPendingPaymentsBtn, viewOperationalMetricsBtn, manageEmployeesBtn, adminLogoutButton); // Add adminLogoutButton
        adminLayout.setLeft(adminMenu);
        BorderPane.setMargin(adminMenu, new Insets(0, 10, 0, 0));

        // Initialize adminContent as a class field here
        adminContent = new VBox(20);
        adminContent.setPadding(new Insets(10));
        adminContent.setAlignment(Pos.TOP_CENTER);
        adminLayout.setCenter(adminContent); // Set it as the center of the BorderPane

        adminTab.setContent(adminLayout); // Set the BorderPane as the content of the tab

        // Now, add the listener. When the tab becomes selected,
        // adminLayout.getCenter() will no longer be null.
        adminTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // When admin tab is selected
                // Set default view to feedback when admin tab is opened
                showAdminView("feedback");
            }
        });

        return adminTab;
    }

    private void showAdminView(String viewName) {
        // Now adminContent is a class field, so we can directly use it
        adminContent.getChildren().clear(); // Clear previous view

        switch (viewName) {
            case "feedback":
                adminContent.getChildren().add(getFeedbackView());
                break;
            case "inventory":
                adminContent.getChildren().add(getInventoryView());
                updateInventoryListView();
                break;
            case "financials":
                adminContent.getChildren().add(getFinancialsView());
                updateFinancials();
                break;
            case "pendingPayments":
                adminContent.getChildren().add(getPendingPaymentsView());
                updatePendingPaymentsListView();
                break;
            case "operationalMetrics":
                adminContent.getChildren().add(getOperationalMetricsView());
                updateOperationalMetrics();
                break;
            case "employees":
                adminContent.getChildren().add(getEmployeesView());
                updateEmployeesListView(); // Also update employees list when showing view
                break;
        }
    }

    // Helper methods to get the VBox for each admin view
    private VBox getFeedbackView() {
        VBox feedbackView = new VBox(10);
        Label feedbackViewLabel = new Label("Customer Feedback");
        feedbackViewLabel.setStyle("-fx-font-weight: bold;");
        adminFeedbackListView = new ListView<>(feedbackList); // Ensure it's always bound to the latest feedbackList
        feedbackView.getChildren().addAll(feedbackViewLabel, adminFeedbackListView);
        return feedbackView;
    }

    private VBox getInventoryView() {
        VBox inventoryView = new VBox(10);
        Label inventoryViewLabel = new Label("Manage Inventory");
        inventoryViewLabel.setStyle("-fx-font-weight: bold;");
        inventoryListView = new ListView<>();
        inventoryListView.setPrefHeight(200);
        // updateInventoryListView() will be called when this view is shown via showAdminView
        HBox inventoryActions = new HBox(10);
        inventoryItemNameField = new TextField();
        inventoryItemNameField.setPromptText("Ingredient");
        inventoryQuantityField = new TextField();
        inventoryQuantityField.setPromptText("Quantity");
        Button addStockBtn = new Button("Add Stock");
        addStockBtn.setOnAction(e -> manageInventoryFX("add"));
        Button deductStockBtn = new Button("Deduct Stock");
        deductStockBtn.setOnAction(e -> manageInventoryFX("deduct"));
        inventoryActions.getChildren().addAll(inventoryItemNameField, inventoryQuantityField, addStockBtn, deductStockBtn);
        inventoryView.getChildren().addAll(inventoryViewLabel, inventoryListView, inventoryActions);
        return inventoryView;
    }

    private VBox getFinancialsView() {
        VBox financialsView = new VBox(10);
        Label financialsViewLabel = new Label("Financial Report");
        financialsViewLabel.setStyle("-fx-font-weight: bold;");
        financialReportLabel = new Label();
        // updateFinancials() will be called when this view is shown via showAdminView
        financialsView.getChildren().addAll(financialsViewLabel, financialReportLabel);
        return financialsView;
    }

    private VBox getPendingPaymentsView() {
        VBox pendingPaymentsView = new VBox(10);
        Label pendingPaymentsViewLabel = new Label("Pending Customer Payments");
        pendingPaymentsViewLabel.setStyle("-fx-font-weight: bold;");
        pendingPaymentsListView = new ListView<>();
        // updatePendingPaymentsListView() will be called when this view is shown via showAdminView
        pendingPaymentsView.getChildren().addAll(pendingPaymentsViewLabel, pendingPaymentsListView);
        return pendingPaymentsView;
    }

    private VBox getOperationalMetricsView() {
        VBox operationalMetricsView = new VBox(10);
        Label operationalMetricsViewLabel = new Label("Operational Metrics");
        operationalMetricsViewLabel.setStyle("-fx-font-weight: bold;");
        operationalMetricsLabel = new Label();
        // updateOperationalMetrics() will be called when this view is shown via showAdminView
        operationalMetricsView.getChildren().addAll(operationalMetricsViewLabel, operationalMetricsLabel);
        return operationalMetricsView;
    }

    private VBox getEmployeesView() {
        VBox employeesView = new VBox(10);
        Label employeesViewLabel = new Label("Manage Employees");
        employeesViewLabel.setStyle("-fx-font-weight: bold;");
        employeesListView = new ListView<>();
        // updateEmployeesListView() will be called when this view is shown via showAdminView
        HBox employeeActions = new HBox(10);
        employeeNameField = new TextField();
        employeeNameField.setPromptText("Employee Name");
        employeePositionField = new TextField();
        employeePositionField.setPromptText("Position");
        Button addEmployeeBtn = new Button("Add Employee");
        addEmployeeBtn.setOnAction(e -> manageEmployeesFX("add"));
        Button removeEmployeeBtn = new Button("Remove Employee");
        removeEmployeeBtn.setOnAction(e -> manageEmployeesFX("remove"));
        employeeActions.getChildren().addAll(employeeNameField, employeePositionField, addEmployeeBtn, removeEmployeeBtn);
        employeesView.getChildren().addAll(employeesViewLabel, employeesListView, employeeActions);
        return employeesView;
    }


    // --- Login Dialogs ---

    private void showCustomerLoginDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Customer Login");
        dialog.setHeaderText("Welcome, Customer!");
        dialog.setContentText("Please enter your name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (name.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Login Error", "Name cannot be empty.");
                return;
            }
            currentCustomer = name.trim();
            uniqueCustomers.add(currentCustomer);
            customerOrders.putIfAbsent(currentCustomer, new ArrayList<>());
            unpaidBills.putIfAbsent(currentCustomer, 0);

            // Enable Customer tab and disable Login tab
            mainTabPane.getTabs().get(1).disableProperty().set(false);
            mainTabPane.getTabs().get(0).disableProperty().set(true);
            mainTabPane.getSelectionModel().select(1); // Switch to Customer tab
            updateCustomerUI(); // Update all customer-related UI elements
        });
    }

    private void showAdminLoginDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Admin Login");
        dialog.setHeaderText("Admin Authentication");

        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Admin Password");

        grid.add(new Label("Password:"), 0, 0);
        grid.add(passwordField, 1, 0);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                if ("admin123".equals(passwordField.getText())) { // Simple hardcoded password for example
                    // Enable Admin tab and disable Login tab
                    mainTabPane.getTabs().get(2).disableProperty().set(false);
                    mainTabPane.getTabs().get(0).disableProperty().set(true);
                    mainTabPane.getSelectionModel().select(2); // Switch to Admin tab
                } else {
                    showAlert(Alert.AlertType.ERROR, "Login Failed", "Incorrect password.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    // --- Logout Functionality ---
    private void logoutUser(String role) {
        if (role.equals("customer")) {
            // Check for unpaid bills before logging out
            if (unpaidBills.getOrDefault(currentCustomer, 0) > 0) {
                Alert confirmLogout = new Alert(Alert.AlertType.CONFIRMATION);
                confirmLogout.setTitle("Outstanding Bill");
                confirmLogout.setHeaderText("You have an outstanding bill!");
                confirmLogout.setContentText("You have an outstanding bill of " + unpaidBills.get(currentCustomer) + ".\nDo you want to pay now before logging out?");

                Optional<ButtonType> result = confirmLogout.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    viewAndPayBillFX(); // Attempt to pay the bill
                    if (unpaidBills.get(currentCustomer) > 0) { // If still unpaid after attempting to pay
                        showAlert(Alert.AlertType.WARNING, "Logout Aborted", "Please settle your bill before logging out.");
                        return; // Prevent logout
                    }
                } else {
                    showAlert(Alert.AlertType.WARNING, "Logout Aborted", "Please settle your bill before logging out.");
                    return; // Prevent logout if user chose not to pay
                }
            }
            clearCustomerReservation(currentCustomer); // Clear reservations on logout
            currentCustomer = ""; // Clear current customer
            mainTabPane.getTabs().get(1).disableProperty().set(true); // Disable customer tab
        } else if (role.equals("admin")) {
            mainTabPane.getTabs().get(2).disableProperty().set(true); // Disable admin tab
        }

        // Re-enable login tab and switch to it
        mainTabPane.getTabs().get(0).disableProperty().set(false);
        mainTabPane.getSelectionModel().select(0);
        showAlert(Alert.AlertType.INFORMATION, "Logged Out", "You have been successfully logged out.");
    }


    // --- Customer Actions (JavaFX wrappers for backend logic) ---

    private void placeOrderFX() {
        if (currentCustomer == null || currentCustomer.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please login as a customer first.");
            return;
        }

        if (unpaidBills.get(currentCustomer) > 0 && (customerOrders.get(currentCustomer) == null || customerOrders.get(currentCustomer).isEmpty())) {
            showAlert(Alert.AlertType.WARNING, "Pending Bill", "You have a previous unpaid bill of " + unpaidBills.get(currentCustomer) + ". Please pay before placing new items.");
            return;
        }

        String item = orderItemField.getText().trim();
        if (item.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter an item to order.");
            return;
        }

        String originalItem = item; // Store original for water bottle check
        if (item.equalsIgnoreCase("Water Bottle")) {
            ChoiceDialog<String> waterTypeDialog = new ChoiceDialog<>("Cool", "Cool", "Normal");
            waterTypeDialog.setTitle("Water Bottle Type");
            waterTypeDialog.setHeaderText("Choose Water Bottle Type");
            waterTypeDialog.setContentText("Cool or Normal?");
            Optional<String> waterTypeResult = waterTypeDialog.showAndWait();
            if (waterTypeResult.isPresent()) {
                item = waterTypeResult.get() + " Water Bottle";
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Order Cancelled", "Water bottle order cancelled.");
                return;
            }
        }

        if (menu.containsKey(item)) {
            if (!checkInventory(item)) {
                showAlert(Alert.AlertType.ERROR, "Out of Stock", "Sorry, " + item + " cannot be prepared due to insufficient ingredients.");
                return;
            }

            customerOrders.get(currentCustomer).add(item);
            unpaidBills.put(currentCustomer, unpaidBills.get(currentCustomer) + menu.get(item));
            totalCostOfGoodsSold += itemCost.get(item);
            updateInventory(item);

            showAlert(Alert.AlertType.INFORMATION, "Order Placed", item + " added to your order. Estimated prep time: " + prepTime.get(item) + " mins.");

            // Extra cheese implementation
            if (originalItem.equalsIgnoreCase("Pizza") || originalItem.equalsIgnoreCase("Burger") || originalItem.equalsIgnoreCase("Sandwich")) {
                Alert confirmCheese = new Alert(Alert.AlertType.CONFIRMATION);
                confirmCheese.setTitle("Extra Cheese");
                confirmCheese.setHeaderText("Add Extra Cheese?");
                confirmCheese.setContentText("Would you like to add extra cheese for 20?");
                Optional<ButtonType> cheeseResult = confirmCheese.showAndWait();
                if (cheeseResult.isPresent() && cheeseResult.get() == ButtonType.OK) {
                    if (inventory.getOrDefault("Cheese", 0) >= 1) {
                        unpaidBills.put(currentCustomer, unpaidBills.get(currentCustomer) + 20);
                        totalCostOfGoodsSold += 5; // Cost of cheese to restaurant
                        inventory.put("Cheese", inventory.get("Cheese") - 1);
                        showAlert(Alert.AlertType.INFORMATION, "Extra Cheese", "Extra cheese added.");
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Out of Stock", "Sorry, unable to add extra cheese, out of stock.");
                    }
                }
            }
            totalOrdersPlaced++;
            updateCustomerUI(); // Refresh UI after order
            orderItemField.clear(); // Clear the input field
        } else {
            showAlert(Alert.AlertType.ERROR, "Item Not Found", "Item '" + item + "' not available on the menu.");
        }
    }

    private void reserveTableFX() {
        if (currentCustomer == null || currentCustomer.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please login as a customer first.");
            return;
        }

        int tableNumber;
        try {
            tableNumber = Integer.parseInt(reserveTableField.getText().trim());
            if (tableNumber <= 0) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Table number must be a positive integer.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid table number.");
            return;
        }

        if (tableReservations.containsKey(tableNumber)) {
            showAlert(Alert.AlertType.WARNING, "Reservation Failed", "Sorry, table " + tableNumber + " is already reserved by " + tableReservations.get(tableNumber) + ".");
        } else {
            tableReservations.put(tableNumber, currentCustomer);
            showAlert(Alert.AlertType.INFORMATION, "Reservation Successful", "Table " + tableNumber + " reserved successfully for " + currentCustomer + "!");
            updateCustomerUI(); // Refresh UI after reservation
        }
        reserveTableField.clear();
    }

    private void viewAndPayBillFX() {
        if (currentCustomer == null || currentCustomer.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please login as a customer first.");
            return;
        }

        int billAmount = unpaidBills.get(currentCustomer);
        StringBuilder billDetails = new StringBuilder();
        List<String> orderItems = customerOrders.get(currentCustomer);

        if (billAmount == 0 && (orderItems == null || orderItems.isEmpty())) {
            showAlert(Alert.AlertType.INFORMATION, "Bill Status", "You have no pending bill or orders.");
            return;
        }

        if (orderItems != null && !orderItems.isEmpty()) {
            billDetails.append("Items ordered:\n");
            for (String item : orderItems) {
                billDetails.append("- ").append(item).append(" (").append(menu.get(item)).append(")\n");
            }
        } else {
            billDetails.append("No items currently in your order.\n");
        }
        billDetails.append("\nTotal Bill: ").append(billAmount);

        Alert billAlert = new Alert(Alert.AlertType.CONFIRMATION);
        billAlert.setTitle("Bill Details for " + currentCustomer);
        billAlert.setHeaderText(null);
        billAlert.setContentText(billDetails.toString() + "\n\nDo you want to pay the bill now?");

        if (billAmount > 0) {
            Optional<ButtonType> result = billAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                totalRevenue += billAmount;
                unpaidBills.put(currentCustomer, 0);
                customerOrders.get(currentCustomer).clear();
                showAlert(Alert.AlertType.INFORMATION, "Payment Successful", "Payment successful. Thank you!");
                updateCustomerUI(); // Refresh UI after payment
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Payment Pending", "Bill not paid. Please pay before exiting the customer menu.");
            }
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Bill Status", "No amount due.");
        }
    }

    private void cancelTableReservationFX() {
        if (currentCustomer == null || currentCustomer.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please login as a customer first.");
            return;
        }

        List<Integer> customerReservedTables = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : tableReservations.entrySet()) {
            if (entry.getValue().equals(currentCustomer)) {
                customerReservedTables.add(entry.getKey());
            }
        }

        if (customerReservedTables.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Reservations", "You do not have any active table reservation to cancel.");
            return;
        }

        ChoiceDialog<Integer> cancelDialog = new ChoiceDialog<>(null, customerReservedTables);
        cancelDialog.setTitle("Cancel Reservation");
        cancelDialog.setHeaderText("Your Currently Reserved Tables:");
        cancelDialog.setContentText("Select the table number you wish to cancel:");

        Optional<Integer> result = cancelDialog.showAndWait();
        result.ifPresent(tableToCancel -> {
            Alert confirmCancel = new Alert(Alert.AlertType.CONFIRMATION);
            confirmCancel.setTitle("Confirm Cancellation");
            confirmCancel.setHeaderText("Cancel Table " + tableToCancel + "?");
            confirmCancel.setContentText("Are you sure you want to cancel reservation for table " + tableToCancel + "?");

            Optional<ButtonType> confirmResult = confirmCancel.showAndWait();
            if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
                tableReservations.remove(tableToCancel);
                showAlert(Alert.AlertType.INFORMATION, "Cancellation Successful", "Table " + tableToCancel + " reservation cancelled for " + currentCustomer + ".");
                updateCustomerUI(); // Refresh UI
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Cancellation Aborted", "Table reservation not cancelled.");
            }
        });
    }

    private void submitFeedbackFX() {
        String fb = feedbackTextArea.getText().trim();
        if (fb.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Feedback cannot be empty.");
            return;
        }
        feedbackList.add(fb);
        showAlert(Alert.AlertType.INFORMATION, "Feedback Submitted", "Thank you for your valuable feedback!");
        feedbackTextArea.clear();
    }

    private void processOrdersFX() {
        if (currentCustomer == null || currentCustomer.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please login as a customer first.");
            return;
        }
        List<String> currentOrder = customerOrders.get(currentCustomer);
        if (currentOrder == null || currentOrder.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Orders", "No orders to process for " + currentCustomer + ".");
            return;
        }
        int maxPrepTime = 0;
        for (String item : currentOrder) {
            maxPrepTime = Math.max(maxPrepTime, prepTime.getOrDefault(item, 0));
        }
        showAlert(Alert.AlertType.INFORMATION, "Order Processing", "Your order is being prepared. It will be ready in approximately " + maxPrepTime + " minutes!");
    }

    // --- Admin Actions (JavaFX wrappers) ---

    private void manageInventoryFX(String action) {
        String ing = inventoryItemNameField.getText().trim();
        int qty;
        try {
            qty = Integer.parseInt(inventoryQuantityField.getText().trim());
            if (qty < 0) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Quantity cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter a valid quantity.");
            return;
        }

        if (ing.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Ingredient name cannot be empty.");
            return;
        }

        if (action.equals("add")) {
            inventory.put(ing, inventory.getOrDefault(ing, 0) + qty);
            showAlert(Alert.AlertType.INFORMATION, "Stock Added", qty + " units of " + ing + " added. New stock: " + inventory.get(ing));
        } else if (action.equals("deduct")) {
            int currentQty = inventory.getOrDefault(ing, 0);
            if (currentQty >= qty) {
                inventory.put(ing, currentQty - qty);
                showAlert(Alert.AlertType.INFORMATION, "Stock Deducted", qty + " units of " + ing + " deducted. New stock: " + inventory.get(ing));
            } else {
                showAlert(Alert.AlertType.WARNING, "Insufficient Stock", "Cannot deduct " + qty + " units. Only " + currentQty + " available for " + ing + ".");
            }
        }
        updateInventoryListView();
        inventoryItemNameField.clear();
        inventoryQuantityField.clear();
    }

    private void manageEmployeesFX(String action) {
        String name = employeeNameField.getText().trim();
        String position = employeePositionField.getText().trim();

        if (name.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Employee name cannot be empty.");
            return;
        }

        if (action.equals("add")) {
            if (position.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Employee position cannot be empty for adding.");
                return;
            }
            if (employees.containsKey(name)) {
                showAlert(Alert.AlertType.WARNING, "Employee Exists", "Employee with this name already exists. Consider updating their position instead.");
            } else {
                employees.put(name, position);
                showAlert(Alert.AlertType.INFORMATION, "Employee Added", name + " added as " + position + ".");
            }
        } else if (action.equals("remove")) {
            if (employees.containsKey(name)) {
                employees.remove(name);
                showAlert(Alert.AlertType.INFORMATION, "Employee Removed", name + " removed from employee list.");
            } else {
                showAlert(Alert.AlertType.WARNING, "Employee Not Found", "Employee " + name + " not found.");
            }
        }
        updateEmployeesListView();
        employeeNameField.clear();
        employeePositionField.clear();
    }

    // --- UI Update Methods ---

    private void updateCustomerUI() {
        // Update current orders list
        if (customerOrders.containsKey(currentCustomer)) {
            customerOrderListView.setItems(FXCollections.observableArrayList(customerOrders.get(currentCustomer)));
        } else {
            customerOrderListView.setItems(FXCollections.observableArrayList());
        }

        // Update bill label
        currentBillLabel.setText("Current Bill: " + unpaidBills.getOrDefault(currentCustomer, 0));

        // Update customer's reserved tables
        ObservableList<String> customerTables = FXCollections.observableArrayList();
        for (Map.Entry<Integer, String> entry : tableReservations.entrySet()) {
            if (entry.getValue().equals(currentCustomer)) {
                customerTables.add("Table " + entry.getKey());
            }
        }
        customerReservedTablesListView.setItems(customerTables);
    }

    private void updateInventoryListView() {
        ObservableList<Map.Entry<String, Integer>> inventoryData = FXCollections.observableArrayList(inventory.entrySet());
        inventoryListView.setItems(inventoryData);
        inventoryListView.setCellFactory(lv -> new ListCell<Map.Entry<String, Integer>>() {
            @Override
            protected void updateItem(Map.Entry<String, Integer> item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getKey() + ": " + item.getValue());
            }
        });
    }

    private void updateFinancials() {
        double netProfit = totalRevenue - totalCostOfGoodsSold;
        String financialSummary = "Total Revenue: " + String.format("%.2f", totalRevenue) + "\n" +
                                  "Total Cost of Goods Sold (COGS): " + String.format("%.2f", totalCostOfGoodsSold) + "\n" +
                                  (netProfit >= 0 ? "Net Profit: " : "Net Loss: ") + String.format("%.2f", Math.abs(netProfit));
        financialReportLabel.setText(financialSummary);
    }

    private void updatePendingPaymentsListView() {
        ObservableList<Map.Entry<String, Integer>> pendingData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : unpaidBills.entrySet()) {
            if (entry.getValue() > 0) {
                pendingData.add(entry);
            }
        }
        pendingPaymentsListView.setItems(pendingData);
        pendingPaymentsListView.setCellFactory(lv -> new ListCell<Map.Entry<String, Integer>>() {
            @Override
            protected void updateItem(Map.Entry<String, Integer> item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : "Customer: " + item.getKey() + ", Amount Due: " + item.getValue());
            }
        });
    }

    private void updateOperationalMetrics() {
        String metrics = "Total Unique Customers: " + uniqueCustomers.size() + "\n" +
                         "Total Orders Placed (Items added to order): " + totalOrdersPlaced + "\n" +
                         "Current Active Table Reservations: " + tableReservations.size();
        operationalMetricsLabel.setText(metrics);
    }

    private void updateEmployeesListView() {
        ObservableList<Map.Entry<String, String>> employeeData = FXCollections.observableArrayList(employees.entrySet());
        employeesListView.setItems(employeeData);
        employeesListView.setCellFactory(lv -> new ListCell<Map.Entry<String, String>>() {
            @Override
            protected void updateItem(Map.Entry<String, String> item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : "Name: " + item.getKey() + ", Position: " + item.getValue());
            }
        });
    }

    // --- Helper Methods (from original code, adapted) ---

    static boolean checkInventory(String item) {
        if (itemIngredients.containsKey(item)) {
            for (String ing : itemIngredients.get(item)) {
                if (inventory.getOrDefault(ing, 0) < 1) {
                    return false;
                }
            }
        }
        return true;
    }

    static void updateInventory(String item) {
        if (itemIngredients.containsKey(item)) {
            for (String ing : itemIngredients.get(item)) {
                inventory.put(ing, inventory.getOrDefault(ing, 0) - 1);
            }
        }
    }

    static void clearCustomerReservation(String customerName) {
        Iterator<Map.Entry<Integer, String>> iterator = tableReservations.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, String> entry = iterator.next();
            if (entry.getValue().equals(customerName)) {
                System.out.println("Releasing table " + entry.getKey() + " for " + customerName + "."); // For console debug
                iterator.remove(); // Safely remove the reservation
            }
        }
    }


    // Initializes all static data
    private void initializeData() {
        initializeMenu();
        initializeInventory();
        initializeEmployees();
    }

    static void initializeMenu() {
        menu.put("Pizza", 150);
        itemCost.put("Pizza", 60);
        prepTime.put("Pizza", 20);
        itemIngredients.put("Pizza", Arrays.asList("Cheese", "Dough", "Sauce"));

        menu.put("Burger", 100);
        itemCost.put("Burger", 40);
        prepTime.put("Burger", 15);
        itemIngredients.put("Burger", Arrays.asList("Bun", "Patty", "Cheese"));

        menu.put("Pasta", 120);
        itemCost.put("Pasta", 50);
        prepTime.put("Pasta", 25);
        itemIngredients.put("Pasta", Arrays.asList("Pasta", "Sauce"));

        menu.put("Sandwich", 80);
        itemCost.put("Sandwich", 30);
        prepTime.put("Sandwich", 10);
        itemIngredients.put("Sandwich", Arrays.asList("Bread", "Lettuce", "Tomato"));

        menu.put("Fries", 70);
        itemCost.put("Fries", 25);
        prepTime.put("Fries", 10);
        itemIngredients.put("Fries", Arrays.asList("Potato", "Salt"));

        menu.put("Cool Water Bottle", 25);
        itemCost.put("Cool Water Bottle", 10);
        prepTime.put("Cool Water Bottle", 1);
        itemIngredients.put("Cool Water Bottle", Collections.singletonList("Water"));

        menu.put("Normal Water Bottle", 20);
        itemCost.put("Normal Water Bottle", 8);
        prepTime.put("Normal Water Bottle", 1);
        itemIngredients.put("Normal Water Bottle", Collections.singletonList("Water"));

        menuItems.addAll(menu.keySet()); // Populate ObservableList
    }

    static void initializeInventory() {
        inventory.put("Cheese", 50);
        inventory.put("Dough", 30);
        inventory.put("Sauce", 40);
        inventory.put("Bun", 40);
        inventory.put("Patty", 30);
        inventory.put("Pasta", 30);
        inventory.put("Bread", 30);
        inventory.put("Lettuce", 20);
        inventory.put("Tomato", 25);
        inventory.put("Potato", 50);
        inventory.put("Salt", 100);
        inventory.put("Water", 100);
    }

    static void initializeEmployees() {
        employees.put("Laskshmi", "Chef");
        employees.put("Roopa", "Waiter");
        employees.put("Yashu", "Manager");
        employees.put("Hima", "Cashier");
    }

    // Helper for showing alerts
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
