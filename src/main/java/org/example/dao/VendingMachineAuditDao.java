package org.example.dao;

public interface VendingMachineAuditDao {
    void writeAuditEntry(String entry) throws VendingMachinePersistenceException;
}