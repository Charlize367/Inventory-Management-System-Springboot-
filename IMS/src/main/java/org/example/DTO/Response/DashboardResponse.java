package org.example.DTO.Response;

import java.util.List;
import java.util.Map;

public class DashboardResponse {



    private Long totalSales;
    private Long totalPurchases;
    private Long totalCustomers;
    private Long totalSuppliers;
    private Double totalProfit;
    private Long activeProducts;
    private List<Map<String, Object>> last7DaysSale;
    private List<Map<String, Object>> last7DaysPurchase;
    private List<Map<String, Object>> topSellingProducts;
    private List<Map<String, Object>> recentTransactions;
    private List<Map<String, Object>> lowStockProducts;

    public DashboardResponse(Long totalSales, Long totalPurchases, Long totalCustomers, Long totalSuppliers, Double totalProfit, Long activeProducts, List<Map<String, Object>> last7DaysSale, List<Map<String, Object>> last7DaysPurchase, List<Map<String, Object>> topSellingProducts, List<Map<String, Object>> recentTransactions, List<Map<String, Object>> lowStockProducts) {
        this.totalSales = totalSales;
        this.totalPurchases = totalPurchases;
        this.totalCustomers = totalCustomers;
        this.totalSuppliers = totalSuppliers;
        this.totalProfit = totalProfit;
        this.activeProducts = activeProducts;
        this.last7DaysSale = last7DaysSale;
        this.last7DaysPurchase = last7DaysPurchase;
        this.topSellingProducts = topSellingProducts;
        this.recentTransactions = recentTransactions;
        this.lowStockProducts = lowStockProducts;
    }

    public DashboardResponse() {

    }

    public Long getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(Long totalSales) {
        this.totalSales = totalSales;
    }

    public Long getTotalPurchases() {
        return totalPurchases;
    }

    public void setTotalPurchases(Long totalPurchases) {
        this.totalPurchases = totalPurchases;
    }

    public Long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(Long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public Long getTotalSuppliers() {
        return totalSuppliers;
    }

    public void setTotalSuppliers(Long totalSuppliers) {
        this.totalSuppliers = totalSuppliers;
    }

    public Double getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(Double totalProfit) {
        this.totalProfit = totalProfit;
    }

    public Long getActiveProducts() {
        return activeProducts;
    }

    public void setActiveProducts(Long activeProducts) {
        this.activeProducts = activeProducts;
    }

    public List<Map<String, Object>> getLast7DaysSale() {
        return last7DaysSale;
    }

    public void setLast7DaysSale(List<Map<String, Object>> last7DaysSale) {
        this.last7DaysSale = last7DaysSale;
    }

    public List<Map<String, Object>> getLast7DaysPurchase() {
        return last7DaysPurchase;
    }

    public void setLast7DaysPurchase(List<Map<String, Object>> last7DaysPurchase) {
        this.last7DaysPurchase = last7DaysPurchase;
    }

    public List<Map<String, Object>> getTopSellingProducts() {
        return topSellingProducts;
    }

    public void setTopSellingProducts(List<Map<String, Object>> topSellingProducts) {
        this.topSellingProducts = topSellingProducts;
    }

    public List<Map<String, Object>> getRecentTransactions() {
        return recentTransactions;
    }

    public void setRecentTransactions(List<Map<String, Object>> recentTransactions) {
        this.recentTransactions = recentTransactions;
    }

    public List<Map<String, Object>> getLowStockProducts() {
        return lowStockProducts;
    }

    public void setLowStockProducts(List<Map<String, Object>> lowStockProducts) {
        this.lowStockProducts = lowStockProducts;
    }

}
