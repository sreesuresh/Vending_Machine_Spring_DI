package org.example.dao;

import org.junit.jupiter.api.*;

class VendingMachineAuditDaoFileImplTest {
    String testAuditFile = "testAuditFile.txt";
    VendingMachineAuditDao testAuditDao = new VendingMachineAuditDaoFileImpl(testAuditFile);


    public VendingMachineAuditDaoFileImplTest() {
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
    public void testWriteAuditEntry() throws VendingMachinePersistenceException {
        //ARRANGE
        String entry = "One Snickers removed.";

        //ACT
        testAuditDao.writeAuditEntry(entry);

    }

}