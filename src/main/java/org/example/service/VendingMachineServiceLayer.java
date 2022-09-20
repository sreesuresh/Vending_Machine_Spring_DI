package org.example.service;



import org.example.dao.VendingMachinePersistenceException;
import org.example.dto.Item;

import java.math.BigDecimal;
import java.util.Map;

public interface VendingMachineServiceLayer {
    void checkIfEnoughMoney(Item item, BigDecimal inputMoney) throws  InsufficientFundsException;

    Map<String, BigDecimal> getItemsInStockWithPrices() throws VendingMachinePersistenceException;

    Map<BigDecimal, BigDecimal> getChangePerCoin(Item item, BigDecimal money);

    Item getItem(String name, BigDecimal inputMoney) throws
            InsufficientFundsException,
            NoItemInventoryException,
            VendingMachinePersistenceException;


    void removeOneItemFromInventory(String name) throws
            NoItemInventoryException,
            VendingMachinePersistenceException;
}