package org.example.service;

import org.example.dao.VendingMachineAuditDao;
import org.example.dao.VendingMachineDao;
import org.example.dao.VendingMachinePersistenceException;
import org.example.dto.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class VendingMachineServiceLayerImpl implements VendingMachineServiceLayer {

    //The service layer is responsible for the business logic of an application. It sits between
    //the controller and DAOs.

    private VendingMachineAuditDao auditDao;
    private VendingMachineDao dao;

    // constructors
    @Autowired
    public VendingMachineServiceLayerImpl(VendingMachineAuditDao auditDao, VendingMachineDao dao) {
        this.auditDao = auditDao;
        this.dao = dao;
    }

    // override all items from dao
    @Override
    public void checkIfEnoughMoney(Item item, BigDecimal inputMoney) throws InsufficientFundsException {
        //Checks if the user has input enough money to buy selected item
        //If the cost of the item is greater than the amount of money put in
        if (item.getCost().compareTo(inputMoney) == 1) {
            throw new InsufficientFundsException(
                    "ERROR: insufficient funds, you have only input " + inputMoney);
        }
    }

    @Override
    public Map<String, BigDecimal> getItemsInStockWithPrices() throws VendingMachinePersistenceException {
        //Map of key=name, value=cost
        Map<String, BigDecimal> itemsInStockWithCosts = dao.getMapOfItemNamesInStockWithPrices();
        return itemsInStockWithCosts;
    }

    @Override
    public Map<BigDecimal, BigDecimal> getChangePerCoin(Item item, BigDecimal money) {
        Map<BigDecimal, BigDecimal> changeDuePerCoin = dao.getChangePerCoin(item, money);
        return changeDuePerCoin;
    }

    @Override
    public Item getItem(String name, BigDecimal inputMoney) throws InsufficientFundsException, NoItemInventoryException, VendingMachinePersistenceException {
        Item wantedItem = dao.getItem(name);   //the inputs are case-sensitive.

        //If the wanted item returns null, the item does not exist in the items map
        if (wantedItem == null) {
            throw new NoItemInventoryException(
                    "ERROR: there are no " + name + "'s in the vending machine.");
        }

        //Check if the user has input enough money
        checkIfEnoughMoney(wantedItem, inputMoney);

        //If they have, check that the item is in stock and if so, remove one item from the inventory
        removeOneItemFromInventory(name);
        return wantedItem;
//        //Give user their change
//        return getChangePerCoin(wantedItem,inputMoney);
    }


    public void removeOneItemFromInventory(String name) throws NoItemInventoryException, VendingMachinePersistenceException {
        //Remove one item from the inventory only when there are items to be removed, i.e. inventory>0.
        if (dao.getItemInventory(name) > 0) {
            dao.reduceOneItemFromInventory(name);
            //if an items removed, write to the audit log
            auditDao.writeAuditEntry(" One " + name + " removed");
        } else {
            //If there are no items left to remove, throw an exception
            throw new NoItemInventoryException(
                    "ERROR: " + name + " is out of stock.");
        }
    }
}