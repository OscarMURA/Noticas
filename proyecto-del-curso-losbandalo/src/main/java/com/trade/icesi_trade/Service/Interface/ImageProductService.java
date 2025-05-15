package com.trade.icesi_trade.Service.Interface;

import com.trade.icesi_trade.model.ImageProduct;
import org.springframework.web.multipart.MultipartFile;

public interface ImageProductService {

    ImageProduct uploadImage(MultipartFile file, Long productId);
    
    ImageProduct getImageByProductId(Long productId);
    
    boolean deleteImage(Long imageId);
}
