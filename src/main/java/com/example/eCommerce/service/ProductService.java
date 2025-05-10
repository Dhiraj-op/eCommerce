package com.example.eCommerce.service;


import com.example.eCommerce.dto.ProductDTO;
import com.example.eCommerce.dto.ProductListDTO;
import com.example.eCommerce.exception.ResourceNotFoundException;
import com.example.eCommerce.mapper.ProductMapper;
import com.example.eCommerce.model.Product;
import com.example.eCommerce.repoitory.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    private static final String UPLOAD_DIR = "src/main/resources/static/images/";

    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO, MultipartFile image) throws IOException{
        Product product = productMapper.toEntity(productDTO);
        if(image != null && !image.isEmpty()){
            String fileName = saveImage(image);
            product.setImage("/images/"+fileName);
        }
        Product savedProduct = productRepository.save(product);
        return productMapper.toDTO(savedProduct);
    }

    @Transactional
    public ProductDTO updateProduct(Long id,ProductDTO productDTO,MultipartFile image) throws IOException{

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Resource not found"));
        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setQuantity(productDTO.getQuantity());
        if(image !=null && !image.isEmpty()){
            String fileName = saveImage(image);
            existingProduct.setImage("/image/" + fileName);
        }
        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toDTO(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id){
       if(!productRepository.existsById(id)){
           throw new ResourceNotFoundException("Product not Found");
       }
       productRepository.deleteById(id);
    }

    public ProductDTO getProduct(Long id){
      Product product = productRepository.findById(id)
              .orElseThrow(()-> new ResourceNotFoundException("Product not found"));

      return productMapper.toDTO(product);
    }

    public Page<ProductListDTO> getAllProduct(Pageable pageable){
        return productRepository.findAllWithoutComment(pageable);
    }

    private String saveImage(MultipartFile image) throws  IOException{
        String fileName = UUID.randomUUID().toString()+"_"+image.getOriginalFilename();
        Path path = Paths.get(UPLOAD_DIR + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, image.getBytes());
        return fileName;
    }




}
