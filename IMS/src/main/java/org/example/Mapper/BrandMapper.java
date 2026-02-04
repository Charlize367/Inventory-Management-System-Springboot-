package org.example.Mapper;

import org.example.DTO.Request.BrandRequest;
import org.example.DTO.Request.CategoryRequest;
import org.example.DTO.Response.BrandResponse;
import org.example.DTO.Response.CategoryResponse;
import org.example.Entities.Brand;
import org.example.Entities.Categories;
import org.springframework.stereotype.Component;

@Component
public class BrandMapper {

    public Brand toEntity(BrandRequest req) {
        Brand brand = new Brand();
        brand.setBrandName(req.getBrandName());
        return brand;
    }

    public BrandResponse toResponse(Brand brand) {
        BrandResponse brandResponse = new BrandResponse();
        brandResponse.setBrandId(brand.getBrandId());
        brandResponse.setBrandName(brand.getBrandName());
        return brandResponse;
    }
}
