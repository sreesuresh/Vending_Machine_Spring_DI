package org.example.service;

import org.example.dao.*;
import org.example.dto.Item;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class VendingMachineServiceLayerImplTest {
    // initialisation
    VendingMachineDao testDao = new VendingMachineDaoFileImpl("VendingMachineTestFile.txt");
    //String testAuditFile = "testAuditFile.txt";
    VendingMachineAuditDao testAuditDao = new VendingMachineAuditDaoFileImpl();
    VendingMachineServiceLayer testService = new VendingMachineServiceLayerImpl(testAuditDao, testDao);

    // constructors
    public VendingMachineServiceLayerImplTest() {

    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testCheckIfEnoughMoney() {
        // ARRANGE
        Item HariboClone = new Item("Haribo");
        HariboClone.setCost(new BigDecimal("1.60"));
        HariboClone.setInventory(9);

        BigDecimal enoughMoney = new BigDecimal("2.00");
        BigDecimal notEnoughMoney = new BigDecimal("1.59");

        // ACT - enough
        try {
            testService.checkIfEnoughMoney(HariboClone, enoughMoney);
        } catch (InsufficientFundsException e) {
            fail("There is sufficient funds, exception should not have benn thrown.");
        }

        // ACT not enough
        try {
            testService.checkIfEnoughMoney(HariboClone, notEnoughMoney);
            fail("There insufficient funds, exception should have been thrown");
        } catch (Exception e) {

        }

    }

    @Test
    public void testGetChangePerCoin() {
        //ARRANGE
        Item hariboClone = new Item("Haribo");
        hariboClone.setCost(new BigDecimal("1.60"));
        hariboClone.setInventory(9);

        BigDecimal money = new BigDecimal("2.50");

        // Change should be $0.90: 25c: 3, 10c: 1, 5c:1
        Map<BigDecimal, BigDecimal> expectedChangePerCoin = new HashMap<>();
        expectedChangePerCoin.put(new BigDecimal("0.25"), new BigDecimal("3"));
        expectedChangePerCoin.put(new BigDecimal("0.10"), new BigDecimal(1));
        expectedChangePerCoin.put(new BigDecimal("0.05"), new BigDecimal("1"));

        //ACT
        Map<BigDecimal, BigDecimal> changePerCoin = testService.getChangePerCoin(hariboClone, money);

        //ASSERT
        assertEquals(changePerCoin.size(), 3, "There should be three coins");
    }

    @Test
    public void testGetItem() throws InsufficientFundsException, VendingMachinePersistenceException, NoItemInventoryException {
        //ARRANGE
        Item snickersClone = new Item("Snickers");
        snickersClone.setCost(new BigDecimal("2.10"));
        snickersClone.setInventory(0);
        BigDecimal money = new BigDecimal("3.00");

        Item malteasersClone = new Item("Malteasers");
        malteasersClone.setCost(new BigDecimal("2.10"));
        malteasersClone.setInventory(testDao.getItemInventory("Malteasers"));

        Item itemWanted = null;
        //ACT
        try {
            itemWanted = testService.getItem("Snickers", money);
            fail("The item wanted is out of stock.");
        } catch (NoItemInventoryException e) {
        }
        try {
            itemWanted = testService.getItem("Malteasers", money);
        } catch (NoItemInventoryException e) {
            if (testDao.getItemInventory("Malteasers") > 0) {
                fail("The item wanted is in stock.");
            }

            //ASSERT
            assertNotNull(itemWanted, "change should not be null");
            assertEquals(itemWanted, malteasersClone, "The item retrieved should be snickers");
        }
    }

    @Test
    public void testRemoveOneItemFromInventory() throws VendingMachinePersistenceException {
        //ARRANGE
        String name = "Snickers";

        //There are no snickers left
        try {
            //ACT
            testService.removeOneItemFromInventory(name);
            //ASSERT
            fail("There are no snickers left, exception should be thrown");
        } catch (NoItemInventoryException e) {
        }

        String malteasers = "Kitkat";
        try {
            //ACT
            testService.removeOneItemFromInventory(malteasers);
        } catch (NoItemInventoryException e) {
            if (testDao.getItemInventory(malteasers) > 0) {
                fail("Kitkat are in stock, exception should not be thrown");
            }
        }
    }


}
