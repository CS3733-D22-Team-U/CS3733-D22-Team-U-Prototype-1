package edu.wpi.cs3733.D22.teamU.frontEnd.controllers;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextArea;
import edu.wpi.cs3733.D22.teamU.BackEnd.Equipment.Equipment;
import edu.wpi.cs3733.D22.teamU.BackEnd.Request.Request;
import edu.wpi.cs3733.D22.teamU.BackEnd.Udb;
import edu.wpi.cs3733.D22.teamU.DBController;
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
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.SneakyThrows;

public class EquipmentDeliverySystemController extends ServiceController {

	@FXML
	TabPane tabPane;
	@FXML
	TableColumn<EquipmentUI, String> nameCol;
	@FXML
	TableColumn<EquipmentUI, Integer> inUse;
	@FXML
	TableColumn<EquipmentUI, Integer> available;
	@FXML
	TableColumn<EquipmentUI, Integer> total;
	@FXML
	TableView<EquipmentUI> table;
	@FXML
	VBox requestHolder;
	@FXML
	Text requestText;
	@FXML
	Button clearButton;
	@FXML
	Button submitButton;
	@FXML
	TableColumn<EquipmentUI, String> activeReqID;
	@FXML
	TableColumn<EquipmentUI, String> activeReqName;
	@FXML
	TableColumn<EquipmentUI, Integer> activeReqAmount;
	@FXML
	TableColumn<EquipmentUI, String> activeReqType;
	@FXML
	TableColumn<EquipmentUI, String> activeReqDestination;
	@FXML
	TableColumn<EquipmentUI, String> activeDate;
	@FXML
	TableColumn<EquipmentUI, String> activeTime;
	@FXML
	TableColumn<EquipmentUI, Integer> activePriority;

	@FXML
	TableView<EquipmentUI> activeRequestTable;
	@FXML
	VBox inputFields;

	ObservableList<EquipmentUI> equipmentUI = FXCollections.observableArrayList();
	ObservableList<JFXCheckBox> checkBoxes = FXCollections.observableArrayList();
	ObservableList<JFXTextArea> checkBoxesInput = FXCollections.observableArrayList();

	ObservableList<EquipmentUI> equipmentUIRequests = FXCollections.observableArrayList();
	Udb udb = DBController.udb;

	private static final SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@SneakyThrows
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);
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
		activeReqName.setCellValueFactory(new PropertyValueFactory<>("id"));
		activeReqName.setCellValueFactory(new PropertyValueFactory<>("equipmentName"));
		activeReqAmount.setCellValueFactory(new PropertyValueFactory<>("requestAmount"));
		activeReqAmount.setCellValueFactory(new PropertyValueFactory<>("type"));
		activeDate.setCellValueFactory(new PropertyValueFactory<>("destination"));
		activeDate.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
		activeTime.setCellValueFactory(new PropertyValueFactory<>("requestTime"));
		activeRequestTable.setItems(getActiveRequestList());
	}

	private ObservableList<EquipmentUI> newRequest(
			String id,
			String name,
			int amount,
			String destination,
			String date,
			String time,
			int priority) {
		equipmentUIRequests.add(new EquipmentUI(id, name, amount, destination, date, time, priority));
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
			double rand = Math.random() * 100;
			equipmentUIRequests.add(
					new EquipmentUI(
							(int) rand + "",
							request.getName(),
							request.getAmount(),
							"lab1",
							request.getDate(),
							request.getTime(),
							1));
		}
		return equipmentUIRequests;
	}

	@Override
	public void addRequest() {
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
								"Test123",
								checkBoxes.get(i).getText(),
								requestAmount,
								"lab1",
								sdf3.format(timestamp).substring(0, 10),
								sdf3.format(timestamp).substring(11),
								1));
				try {
					udb.requestImpl.add(
							new Request(
									"Test123",
									checkBoxes.get(i).getText(), // TODO Have random ID and enter Room Destination
									Integer.parseInt(checkBoxesInput.get(i).getText()),
									"EQUIPMENT",
									"GENERIC ROOM",
									sdf3.format(timestamp).substring(0, 10),
									sdf3.format(timestamp).substring(11),
									1)

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

	@Override
	public void removeRequest() {
	}

	@Override
	public void updateRequest() {
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
}
