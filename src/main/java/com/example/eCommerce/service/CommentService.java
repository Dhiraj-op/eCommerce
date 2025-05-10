package com.example.eCommerce.service;

import com.example.eCommerce.dto.CommentDTO;
import com.example.eCommerce.exception.ResourceNotFoundException;
import com.example.eCommerce.mapper.CommentMapper;
import com.example.eCommerce.model.Comment;
import com.example.eCommerce.model.Product;
import com.example.eCommerce.model.User;
import com.example.eCommerce.repoitory.CommentRepository;
import com.example.eCommerce.repoitory.ProductRepository;
import com.example.eCommerce.repoitory.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    public CommentDTO addComment(Long productId, Long userID,CommentDTO commentDTO ){
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Resource not found"));

        User user = userRepository.findById(userID)
                .orElseThrow(()-> new ResourceNotFoundException("Resource not found"));

        Comment comment = commentMapper.toEntity(commentDTO);

        comment.setProduct(product);
        comment.setUser(user);
        Comment saveComment = commentRepository.save(comment);
        return commentMapper.toDTO(saveComment);
    }

    public List<CommentDTO> getCommentByProduct(Long productId){
        List<Comment> comment = commentRepository.findByProductId(productId);
        return comment.stream()
                .map(commentMapper::toDTO)
                .collect(Collectors.toList());
    }


}
