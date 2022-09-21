package org.example.controller;

import org.example.dao.VendingMachinePersistenceException;
import org.example.dto.Change;
import org.example.dto.Item;
import org.example.service.InsufficientFundsException;
import org.example.service.NoItemInventoryException;
import org.example.service.VendingMachineServiceLayer;
import org.example.service.VendingMachineServiceLayerImpl;
import org.example.ui.UserIO;
import org.example.ui.UserIOConsoleImpl;
import org.example.ui.VendingMachineView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class VendingMachineController {

    //private Change change;
    private UserIO io = new UserIOConsoleImpl();
    private VendingMachineView view;
    private VendingMachineServiceLayer service;


    // constructor for using view and service layer
    @Autowired
    public VendingMachineController(VendingMachineView view, VendingMachineServiceLayer service) {
        this.view = view;
        this.service = service;
        //this.Change = change;
    }

    public VendingMachineController(VendingMachineServiceLayerImpl vendingMachineServiceLayer, Change change, VendingMachineView vendingMachineView) {

    }

    // main part to run the program
    public void run() {
        boolean keepGoing = true;
        String itemSelection = "";
        BigDecimal inputMoney;
        view.displayMenuBanner();
        try {
            getMenu();
        } catch (VendingMachinePersistenceException e) {
            view.displayErrorMessage(e.getMessage());
        }
        inputMoney = getMoney();
        while (keepGoing) {
            try {
                //Display the menu and get a selection
                itemSelection = getItemSelection();

                //If the user selects Exit, the program is exited
                if (itemSelection.equalsIgnoreCase("exit")) {
                    break;
                }
                getItem(itemSelection, inputMoney);
                keepGoing = false;

            } catch (InsufficientFundsException | NoItemInventoryException | VendingMachinePersistenceException e) {
                view.displayErrorMessage(e.getMessage());
                view.displaySelectAnotherMsg();
            }
        }
        exitMessage();

    }

    // get the menu of a list of items
    private void getMenu() throws VendingMachinePersistenceException {
        Map<String, BigDecimal> itemsInStockWithPrices = service.getItemsInStockWithPrices();
        view.displayMenu(itemsInStockWithPrices);
    }

    // obtain money from user
    private BigDecimal getMoney() {
        return view.getMoney();
    }

    // for user selection items
    private String getItemSelection() {
        return view.getItemSelection();
    }

    // display "see you" message
    private void exitMessage() {
        view.displayExitBanner();
    }

    private void getItem(String name, BigDecimal money) throws InsufficientFundsException, NoItemInventoryException, VendingMachinePersistenceException {
        Item wantedItem = service.getItem(name, money);
        Map<BigDecimal, BigDecimal> changeDuePerCoin = service.getChangePerCoin(wantedItem, money);
        view.displayChangeDuePerCoin(changeDuePerCoin);
        view.displayEnjoyBanner(name);
    }
}