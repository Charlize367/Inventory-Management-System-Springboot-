package org.example.Repository;


import org.example.Entities.Customers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomersRepository extends JpaRepository<Customers, Long> {
    Customers findByCustomerEmail(String email);
}
