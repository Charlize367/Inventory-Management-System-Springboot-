package org.example.Repository;

import org.example.Entities.Suppliers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuppliersRepository extends JpaRepository<Suppliers, Long> {
    Suppliers findBySupplierEmail(String email);
}
