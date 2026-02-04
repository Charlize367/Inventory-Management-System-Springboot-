package org.example.Services;

import org.example.DTO.Request.SupplierRequest;
import org.example.DTO.Response.SupplierResponse;
import org.example.Entities.Customers;
import org.example.Entities.Purchases;
import org.example.Entities.Suppliers;
import org.example.Exception.ResourceAlreadyExistsException;
import org.example.Exception.ResourceNotFoundException;
import org.example.Mapper.SuppliersMapper;
import org.example.Repository.SuppliersRepository;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SuppliersService {

    private static final Logger logger = LoggerFactory.getLogger(SuppliersService.class);

    private final SuppliersRepository suppliersRepository;
    private final SuppliersMapper suppliersMapper;

    public SuppliersService(SuppliersRepository suppliersRepository, SuppliersMapper suppliersMapper) {
        this.suppliersRepository = suppliersRepository;
        this.suppliersMapper = suppliersMapper;

    }
    public Page<SupplierResponse> getSuppliers(Pageable pageable) {
        logger.info("Displaying all suppliers");
        return suppliersRepository.findAll(pageable)
                .map(suppliersMapper::toResponse);
    }

    public List<SupplierResponse> getAllSuppliers() {
        logger.info("Displaying all suppliers with pagination");
        return suppliersRepository.findAll().stream()
                .map(suppliersMapper::toResponse)
                .toList();
    }

    public SupplierResponse getSupplierById(Long id) {
        logger.info("Fetching supplier with id: {}", id);
        Suppliers supplier = suppliersRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Supplier with id: {} does not exist", id);
                    return new ResourceNotFoundException("Supplier not found.");
                });
        logger.info("Successfully fetched supplier:{} (id: {})", supplier.getSupplierName(), supplier.getSupplierId() );
        return suppliersMapper.toResponse(supplier);
    }

    public SupplierResponse addSupplier(SupplierRequest request) {

        logger.info("Attempting to add new supplier with email: {}", request.getSupplierEmail());

        Suppliers supplier = suppliersRepository.findBySupplierEmail(request.getSupplierEmail());

        if (supplier != null) {
            logger.warn("Supplier already exists with email: {}", request.getSupplierEmail());
            throw new ResourceAlreadyExistsException("Supplier already exists.");
        }

        String safeName = Jsoup.clean(request.getSupplierName(), Safelist.none()).trim();
        String safeContactName = Jsoup.clean(request.getSupplierContactName(), Safelist.none()).trim();
        String safeEmail = Jsoup.clean(request.getSupplierEmail(), Safelist.none()).trim();
        String safeAddress = Jsoup.clean(request.getSupplierAddress(), Safelist.none()).trim();
        String safePhoneNumber = Jsoup.clean(request.getSupplierPhone(), Safelist.none()).trim();

        Suppliers newSupplier = new Suppliers();
        newSupplier.setSupplierName(safeName);
        newSupplier.setSupplierEmail(safeEmail);
        newSupplier.setSupplierAddress(safeAddress);
        newSupplier.setSupplierContactName(safeContactName);
        newSupplier.setSupplierPhone(safePhoneNumber);

        Suppliers savedSupplier = suppliersRepository.save(newSupplier);


        logger.info("Successfully added new supplier: {} ({})", safeName, safeEmail);
        return suppliersMapper.toResponse(savedSupplier);
    }


    public SupplierResponse updateSupplierDetails(Long id, SupplierRequest request) {
        logger.info("Updating supplier id: {} ", id);

        Suppliers supplier = suppliersRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Supplier not found with id: {}", id);
                    return new ResourceNotFoundException("Supplier not found");
                });

        String safeName = Jsoup.clean(request.getSupplierName(), Safelist.none()).trim();
        String safeContactName = Jsoup.clean(request.getSupplierContactName(), Safelist.none()).trim();
        String safeEmail = Jsoup.clean(request.getSupplierEmail(), Safelist.none()).trim();
        String safeAddress = Jsoup.clean(request.getSupplierAddress(), Safelist.none()).trim();
        String safePhoneNumber = Jsoup.clean(request.getSupplierPhone(), Safelist.none()).trim();

        supplier.setSupplierName(safeName);
        supplier.setSupplierEmail(safeEmail);
        supplier.setSupplierAddress(safeAddress);
        supplier.setSupplierContactName(safeContactName);
        supplier.setSupplierPhone(safePhoneNumber);
        Suppliers updatedSupplier = suppliersRepository.save(supplier);

        logger.info("Successfully updated supplier id: {} ", id);
        return suppliersMapper.toResponse(updatedSupplier);
    }

    public void deleteSupplier(Long id) {
        logger.info("Attempting to delete supplier by ID");
        Suppliers suppliers = suppliersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
        suppliersRepository.delete(suppliers);

        logger.info("Successfully deleted supplier: {} (id: {})", suppliers.getSupplierName(), id);
    }
}
