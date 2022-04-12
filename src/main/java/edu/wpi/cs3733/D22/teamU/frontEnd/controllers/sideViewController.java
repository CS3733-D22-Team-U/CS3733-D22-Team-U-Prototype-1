package edu.wpi.cs3733.D22.teamU.frontEnd.controllers;

import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import edu.wpi.cs3733.D22.teamU.frontEnd.services.equipmentDelivery.EquipmentUI;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class sideViewController extends ServiceController {

  public MenuItem lower2;
  @FXML JFXHamburger hamburger;
  @FXML VBox vBoxPane;
  @FXML Pane pane;
  @FXML Pane assistPane;
  @FXML SplitMenuButton chooseFloor;
  @FXML Rectangle recLower2;
  @FXML Rectangle recLower1;
  @FXML Rectangle recLevel1;
  @FXML Rectangle recLevel2;
  @FXML Rectangle recLevel3;
  @FXML Rectangle recLevel4;
  @FXML Rectangle recLevel5;
  @FXML TableColumn<EquipmentUI, String> location;
  @FXML TableColumn<EquipmentUI, String> locationType;
  @FXML TableColumn<EquipmentUI, String> equipmentName;

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
          pane.setDisable(!pane.isDisable());
          if (pane.isDisable()) {
            hamburger.setPrefWidth(200);
            pane.setEffect(new GaussianBlur(10));
            assistPane.setDisable(true);
          } else {
            pane.setEffect(null);
            hamburger.setPrefWidth(77);
            assistPane.setDisable(false);
          }
        });
  }

  public void lower(ActionEvent actionEvent) {
    disable();
    MenuItem mi = (MenuItem) actionEvent.getSource();
    switch (mi.getId()) {
      case "lower2":
        recLower2.setVisible(true);
        break;
    }
    switch (mi.getId()) {
      case "lower1":
        recLower1.setVisible(true);
        break;
    }
    switch (mi.getId()) {
      case "level1":
        recLevel1.setVisible(true);
        break;
    }
    switch (mi.getId()) {
      case "level2":
        recLevel2.setVisible(true);
        break;
    }
    switch (mi.getId()) {
      case "level3":
        recLevel3.setVisible(true);
        break;
    }
    switch (mi.getId()) {
      case "level4":
        recLevel4.setVisible(true);
        break;
    }
    switch (mi.getId()) {
      case "level5":
        recLevel5.setVisible(true);
        break;
    }
  }

  public void disable() {
    recLower2.setVisible(false);
    recLower1.setVisible(false);
    recLevel1.setVisible(false);
    recLevel2.setVisible(false);
    recLevel3.setVisible(false);
    recLevel4.setVisible(false);
    recLevel5.setVisible(false);
  }

  private void setLocationEquipment() {}

  @Override
  public void addRequest() {}

  @Override
  public void removeRequest() {}

  @Override
  public void updateRequest() {}
}
