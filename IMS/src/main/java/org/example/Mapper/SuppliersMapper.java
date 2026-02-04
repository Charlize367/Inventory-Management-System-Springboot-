package org.example.Mapper;

import org.example.DTO.Request.CustomerRequest;
import org.example.DTO.Request.SupplierRequest;
import org.example.DTO.Response.CustomerResponse;
import org.example.DTO.Response.SupplierResponse;
import org.example.Entities.Customers;
import org.example.Entities.Suppliers;
import org.springframework.stereotype.Component;

@Component
public class SuppliersMapper {
    public Suppliers toEntity(SupplierRequest req) {
        Suppliers suppliers = new Suppliers();
        suppliers.setSupplierName(req.getSupplierName());
        suppliers.setSupplierContactName(req.getSupplierContactName());
        suppliers.setSupplierAddress(req.getSupplierAddress());
        suppliers.setSupplierEmail(req.getSupplierEmail());
        suppliers.setSupplierPhone(req.getSupplierPhone());

        return suppliers;
    }

    public SupplierResponse toResponse(Suppliers suppliers) {
        SupplierResponse supplierResponse = new SupplierResponse();
        supplierResponse.setSupplierId(suppliers.getSupplierId());
        supplierResponse.setSupplierName(suppliers.getSupplierName());
        supplierResponse.setSupplierContactName(suppliers.getSupplierContactName());
        supplierResponse.setSupplierAddress(suppliers.getSupplierAddress());
        supplierResponse.setSupplierEmail(suppliers.getSupplierEmail());
        supplierResponse.setSupplierPhone(suppliers.getSupplierPhone());
        return supplierResponse;
    }
}
