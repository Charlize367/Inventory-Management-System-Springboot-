package org.example.Services;


import org.example.DTO.Response.DashboardResponse;
import org.example.DTO.Response.ProductResponse;
import org.example.Entities.PurchaseItems;
import org.example.Entities.Purchases;
import org.example.Entities.SaleItems;
import org.example.Entities.Sales;
import org.example.Mapper.SalesMapper;
import org.example.Repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DashboardService {
    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);

    public DashboardService(SalesRepository salesRepository, CustomersRepository customersRepository, PurchaseRepository purchaseRepository, ProductRepository productRepository, SaleItemsRepository saleItemsRepository, SuppliersRepository suppliersRepository, PurchaseItemsRepository purchaseItemsRepository) {
        this.salesRepository = salesRepository;
        this.customersRepository = customersRepository;
        this.purchaseRepository = purchaseRepository;
        this.productRepository = productRepository;
        this.saleItemsRepository = saleItemsRepository;
        this.suppliersRepository = suppliersRepository;
        this.purchaseItemsRepository = purchaseItemsRepository;

    }

    private final SalesRepository salesRepository;
    private final CustomersRepository customersRepository;
    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final SaleItemsRepository saleItemsRepository;
    private final SuppliersRepository suppliersRepository;
    private final PurchaseItemsRepository purchaseItemsRepository;




    public long getTotalActiveProducts() {
        return productRepository.countByActiveTrue();
    }

    public DashboardResponse getDashboardInfo() {

        logger.info("Getting dashboard info");

        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(6);

        //Total counts
        long totalSalesCount = salesRepository.count();

        long totalPurchasesCount = purchaseRepository.count();

        long activeProductsCount = productRepository.countByActiveTrue();

        long totalCustomersCount = customersRepository.count();

        long totalSuppliersCount = suppliersRepository.count();

        List<SaleItems> saleItems = saleItemsRepository.findAll();
        List<PurchaseItems> purchaseItems = purchaseItemsRepository.findAll();

        DashboardResponse response = new DashboardResponse();
        //total profit
        double totalProfit = 0;

        Map<Long, Double> productCostMap = new HashMap<>();
        for (PurchaseItems p: purchaseItems) {
            productCostMap.put(p.getProduct().getProductId(), p.getPurchaseCostPrice());
        }

        for (SaleItems s: saleItems) {
            if (!productCostMap.containsKey(s.getProduct().getProductId())) {
                System.out.println("⚠️ No purchase record found for product ID: " + s.getProduct().getProductId());
                continue;
            }
            double costPrice = productCostMap.getOrDefault(s.getProduct().getProductId(), 0.0);
            double revenue = s.getSalesPrice() * s.getSalesItemQuantity();
            double cost = costPrice * s.getSalesItemQuantity();
            totalProfit += (revenue - cost);

        }
        response.setTotalProfit(totalProfit);



        //top-selling products in the last 7 days
        Map<String, Integer> productSalesMap = new HashMap<>();

        List<SaleItems> last7DaysSaleItems = saleItems.stream()
                .filter(item -> {
                    LocalDate date = item.getSales().getSaleDate();
                    return !date.isBefore(sevenDaysAgo) && !date.isAfter(today);
                })
                .toList();

        for (SaleItems item : last7DaysSaleItems) {
            String productName = item.getProduct().getProductName();
            productSalesMap.merge(productName, item.getSalesItemQuantity(), Integer::sum);
        }

        List<Map<String, Object>> topSellingProducts = productSalesMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("productName", entry.getKey());
                    map.put("quantitySold", entry.getValue());
                    return map;
                })
                .toList();

        // purchase and sales for each day for the last 7 days
        List<Sales> last7DaysSales = salesRepository.findAll().stream()
                .filter(sale -> {
                    LocalDate date = sale.getSaleDate();
                    return !date.isBefore(sevenDaysAgo) && !date.isAfter(today);
                })
                .toList();

        List<Purchases> last7DaysPurchases = purchaseRepository.findAll().stream()
                .filter(purchase -> {
                    LocalDate date = purchase.getPurchaseDate();
                    return !date.isBefore(sevenDaysAgo) && !date.isAfter(today);
                })
                .toList();

        Map<LocalDate, Double> salesByDate = last7DaysSales.stream()
                .collect(Collectors.groupingBy(
                        Sales::getSaleDate,
                        Collectors.summingDouble(Sales::getSalesAmount)
                ));

        Map<LocalDate, Double> purchasesByDate = last7DaysPurchases.stream()
                .collect(Collectors.groupingBy(
                        Purchases::getPurchaseDate,
                        Collectors.summingDouble(Purchases::getPurchaseAmount)
                ));

        List<Map<String, Object>> salesLast7Days = new ArrayList<>();
        List<Map<String, Object>> purchasesLast7Days = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);

            Map<String, Object> saleEntry = new HashMap<>();
            saleEntry.put("date", date.toString());
            saleEntry.put("totalSales", salesByDate.getOrDefault(date, 0.0));
            salesLast7Days.add(saleEntry);

            Map<String, Object> purchaseEntry = new HashMap<>();
            purchaseEntry.put("date", date.toString());
            purchaseEntry.put("totalPurchases", purchasesByDate.getOrDefault(date, 0.0));
            purchasesLast7Days.add(purchaseEntry);
        }

        //recent transactions
        List<Map<String, Object>> recentSales = salesRepository.findAll().stream()
                .filter(sale -> sale.getCustomer() != null)
                .map(sale -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("type", "Sale");
                            map.put("id", sale.getSalesId());
                            map.put("date", sale.getSaleDate());
                            map.put("amount", sale.getSalesAmount());
                            map.put("party", sale.getCustomer().getCustomerName());
                            return map;
                        })
                                .toList();

        List<Map<String, Object>> recentPurchases = purchaseRepository.findAll().stream()
                .map(purchase -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("type", "Purchase");
                    map.put("id", purchase.getPurchaseId());
                    map.put("date", purchase.getPurchaseDate());
                    map.put("amount", purchase.getPurchaseAmount());
                    map.put("party", purchase.getSupplier().getSupplierName());
                    return map;
                })
                .toList();

        List<Map<String, Object>> recentTransactions = Stream.concat(recentSales.stream(), recentPurchases.stream())
                .sorted((a, b) -> ((LocalDate) b.get("date")).compareTo((LocalDate) a.get("date")))
                .limit(5)
                .toList();


        //low stock alerts

        List<Map<String, Object>> lowStockProducts = productRepository.findAll().stream()
                        .filter(product -> product.isActive() && product.getProductStock() < 5)
                                .map(product -> {
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("productId", product.getProductId());
                                    map.put("productName", product.getProductName());
                                    map.put("stock", product.getProductStock());
                                    return map;
                                })
                                        .toList();

        response.setTotalSales(totalSalesCount);
        response.setTotalPurchases(totalPurchasesCount);
        response.setActiveProducts(activeProductsCount);
        response.setTotalCustomers(totalCustomersCount);
        response.setTotalSuppliers(totalSuppliersCount);
        response.setLast7DaysSale(salesLast7Days);
        response.setLast7DaysPurchase(purchasesLast7Days);
        response.setTopSellingProducts(topSellingProducts);
        response.setRecentTransactions(recentTransactions);
        response.setLowStockProducts(lowStockProducts);

        return response;



    }
}
