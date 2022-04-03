package edu.wpi.cs3733.D22.teamU.frontEnd.controllers;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import edu.wpi.cs3733.D22.teamU.BackEnd.Equipment.Equipment;
import edu.wpi.cs3733.D22.teamU.BackEnd.Request.Request;
import edu.wpi.cs3733.D22.teamU.BackEnd.Udb;
import edu.wpi.cs3733.D22.teamU.DBController;
import edu.wpi.cs3733.D22.teamU.frontEnd.Uapp;
import edu.wpi.cs3733.D22.teamU.frontEnd.services.IService;
import edu.wpi.cs3733.D22.teamU.frontEnd.services.equipmentDelivery.EquipmentUI;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.SneakyThrows;

public class EquipmentDeliverySystemController implements Initializable, IService {

  @FXML JFXHamburger hamburger;
  @FXML VBox vBoxPane;
  @FXML TabPane tabPane;
  @FXML Pane backgroundPane;
  @FXML AnchorPane anchor;
  @FXML Pane assistPane;
  @FXML TableColumn<EquipmentUI, String> nameCol;
  @FXML TableColumn<EquipmentUI, Integer> inUse;
  @FXML TableColumn<EquipmentUI, Integer> available;
  @FXML TableColumn<EquipmentUI, Integer> total;
  @FXML TableView<EquipmentUI> table;
  @FXML VBox requestHolder;
  @FXML Text requestText;
  @FXML Button clearButton;
  @FXML Button submitButton;
  @FXML TableColumn<EquipmentUI, String> activeReqName;
  @FXML TableColumn<EquipmentUI, Integer> activeReqAmount;
  @FXML TableColumn<EquipmentUI, String> activeDate;
  @FXML TableColumn<EquipmentUI, String> activeTime;
  @FXML TableView<EquipmentUI> activeRequestTable;
  @FXML VBox inputFields;

  ObservableList<EquipmentUI> equipmentUI = FXCollections.observableArrayList();
  ObservableList<JFXCheckBox> checkBoxes = FXCollections.observableArrayList();
  ObservableList<JFXTextArea> checkBoxesInput = FXCollections.observableArrayList();

  ObservableList<EquipmentUI> equipmentUIRequests = FXCollections.observableArrayList();
  Udb udb = DBController.udb;

  private static final SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @SneakyThrows
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    HamburgerBasicCloseTransition closeTransition = new HamburgerBasicCloseTransition(hamburger);

    closeTransition.setRate(-1);
    hamburger.addEventHandler(
        MouseEvent.MOUSE_CLICKED,
        e -> {
          closeTransition.setRate(closeTransition.getRate() * -1);
          closeTransition.play();
          vBoxPane.setVisible(!vBoxPane.isVisible());
          tabPane.setDisable(!tabPane.isDisable());
          if (tabPane.isDisable()) {
            hamburger.setPrefWidth(200);
            tabPane.setEffect(new GaussianBlur(10));
            assistPane.setDisable(true);
          } else {
            tabPane.setEffect(null);
            hamburger.setPrefWidth(77);
            assistPane.setDisable(false);
          }
        });
    setUpAllEquipment();
    setUpActiveRequests();
    for (Node checkBox : requestHolder.getChildren()) {
      checkBoxes.add((JFXCheckBox) checkBox);
    }

    for (Node textArea : inputFields.getChildren()) {
      checkBoxesInput.add((JFXTextArea) textArea);
    }

    for (int i = 0; i < checkBoxesInput.size(); i++) {
      int finalI = i;
      checkBoxesInput
          .get(i)
          .disableProperty()
          .bind(
              Bindings.createBooleanBinding(
                  () -> !checkBoxes.get(finalI).isSelected(),
                  checkBoxes.stream().map(CheckBox::selectedProperty).toArray(Observable[]::new)));
    }
    clearButton
        .disableProperty()
        .bind(
            Bindings.createBooleanBinding(
                () -> checkBoxes.stream().noneMatch(JFXCheckBox::isSelected),
                checkBoxes.stream().map(CheckBox::selectedProperty).toArray(Observable[]::new)));

    submitButton
        .disableProperty()
        .bind(
            Bindings.createBooleanBinding(
                () -> checkBoxes.stream().noneMatch(JFXCheckBox::isSelected),
                checkBoxes.stream().map(CheckBox::selectedProperty).toArray(Observable[]::new)));
  }

  private void setUpAllEquipment() {
    nameCol.setCellValueFactory(new PropertyValueFactory<EquipmentUI, String>("equipmentName"));
    inUse.setCellValueFactory(new PropertyValueFactory<EquipmentUI, Integer>("amountInUse"));
    available.setCellValueFactory(
        new PropertyValueFactory<EquipmentUI, Integer>("amountAvailable"));
    total.setCellValueFactory(new PropertyValueFactory<EquipmentUI, Integer>("totalAmount"));
    table.setItems(getEquipmentList());
  }

  private void setUpActiveRequests() {
    activeReqName.setCellValueFactory(new PropertyValueFactory<>("equipmentName"));
    activeReqAmount.setCellValueFactory(new PropertyValueFactory<>("requestAmount"));
    activeDate.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
    activeTime.setCellValueFactory(new PropertyValueFactory<>("requestTime"));
    activeRequestTable.setItems(getActiveRequestList());
  }

  private ObservableList<EquipmentUI> newRequest(
      String name, int amount, String date, String time) {
    equipmentUIRequests.add(new EquipmentUI(name, amount, date, time));
    return equipmentUIRequests;
  }

  private ObservableList<EquipmentUI> getEquipmentList() {
    equipmentUI.clear();
    for (Equipment equipment : udb.EquipmentImpl.EquipmentList) {
      equipmentUI.add(
          new EquipmentUI(
              equipment.getName(),
              equipment.getInUse(),
              equipment.getAvailable(),
              equipment.getAmount()));
    }

    return equipmentUI;
  }

  private ObservableList<EquipmentUI> getActiveRequestList() {
    for (Request request : udb.requestImpl.requestList) {
      equipmentUIRequests.add(
          new EquipmentUI(
              request.getName(), request.getAmount(), request.getDate(), request.getTime()));
    }
    return equipmentUIRequests;
  }

  public void submitRequest() {
    String request = "Your request for : ";

    String endRequest = " has been placed successfully";
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    int requestAmount = 0;
    for (int i = 0; i < checkBoxes.size(); i++) {
      if (checkBoxes.get(i).isSelected()) {
        String inputString = "";
        if (checkBoxesInput.get(i).getText().trim().equals("")) {
          inputString = "0";
        } else {
          inputString = checkBoxesInput.get(i).getText().trim();
        }

        requestAmount = Integer.parseInt(inputString);

        request += requestAmount + " " + checkBoxes.get(i).getText() + "(s), ";

        activeRequestTable.setItems(
            newRequest(
                checkBoxes.get(i).getText(),
                requestAmount,
                sdf3.format(timestamp).substring(0, 10),
                sdf3.format(timestamp).substring(11)));
        try {
          udb.requestImpl.add(new Request("Test123",checkBoxes.get(i).getText(), //TODO Have random ID and enter Room Destination
                          Integer.parseInt(checkBoxesInput.get(i).getText()), "EQUIPMENT",
                          "GENERIC ROOM",
                          sdf3.format(timestamp).substring(0, 10),
                          sdf3.format(timestamp).substring(11)
                          )

                  /*
              "csvTables/TowerEquipmentRequests.csv",
              checkBoxes.get(i).getText(),
              Integer.parseInt(checkBoxesInput.get(i).getText()),
              sdf3.format(timestamp).substring(0, 10),
              sdf3.format(timestamp).substring(11)
              */
          );

        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    requestText.setText(request + endRequest);
    requestText.setVisible(true);
    new Thread(
            () -> {
              try {
                Thread.sleep(3500); // milliseconds
                Platform.runLater(
                    () -> {
                      requestText.setVisible(false);
                    });
              } catch (InterruptedException ie) {
              }
            })
        .start();
  }

  public void clearRequest() {
    for (JFXCheckBox checkBox : checkBoxes) {
      checkBox.setSelected(false);
    }
    requestText.setText("Cleared Requests!");
    requestText.setVisible(true);
    new Thread(
            () -> {
              try {
                Thread.sleep(1500); // milliseconds
                Platform.runLater(
                    () -> {
                      requestText.setVisible(false);
                    });
              } catch (InterruptedException ie) {
              }
            })
        .start();
  }

  @Override
  public void addRequest() {}

  @Override
  public void removeRequest() {}

  @Override
  public void updateRequest() {}

  @Override
  public void displayRequest() {}

  public void toHome(ActionEvent actionEvent) throws IOException {
    Scene scene = Uapp.getScene("edu/wpi/cs3733/D22/teamU/views/HomePage.fxml");
    Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
    appStage.setScene(scene);
    appStage.show();
  }

  @Override
  public void toEquipmentDelivery(ActionEvent actionEvent) throws IOException {}

  public void toLabRequest(ActionEvent actionEvent) throws IOException {
    Scene scene = Uapp.getScene("edu/wpi/cs3733/D22/teamU/views/labRequestServices.fxml");
    Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
    appStage.setScene(scene);
    appStage.show();
  }

  public void toMealDelivery(ActionEvent actionEvent) throws IOException {
    Scene scene = Uapp.getScene("edu/wpi/cs3733/D22/teamU/views/mealDelivery.fxml");
    Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
    appStage.setScene(scene);
    appStage.show();
  }

  public void toGiftAndFloral(ActionEvent actionEvent) throws IOException {
    Scene scene = Uapp.getScene("edu/wpi/cs3733/D22/teamU/views/giftFloralService.fxml");
    Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
    appStage.setScene(scene);
    appStage.show();
  }

  public void toLaundry(ActionEvent actionEvent) throws IOException {
    Scene scene = Uapp.getScene("edu/wpi/cs3733/D22/teamU/views/laundryService.fxml");
    Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
    appStage.setScene(scene);
    appStage.show();
  }

  public void toMedicineDelivery(ActionEvent actionEvent) throws IOException {
    Scene scene = Uapp.getScene("edu/wpi/cs3733/D22/teamU/views/medicineDelivery.fxml");
    Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
    appStage.setScene(scene);
    appStage.show();
  }

  public void toMap(ActionEvent actionEvent) throws IOException {
    Scene scene = Uapp.getScene("edu/wpi/cs3733/D22/teamU/views/map.fxml");
    Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
    appStage.setScene(scene);
    appStage.show();
  }
}
