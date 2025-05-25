package com.retailonboardpro.service;

import com.retailonboardpro.constant.AppConstants;
import com.retailonboardpro.dto.ApprovalHistoryDto;
import com.retailonboardpro.dto.RequestDetailsDto;
import com.retailonboardpro.dto.RequestDto;
import com.retailonboardpro.dto.UserDto;
import com.retailonboardpro.entity.ApprovalHistory;
import com.retailonboardpro.entity.Request;
import com.retailonboardpro.entity.User;
import com.retailonboardpro.repository.ApprovalHistoryRepository;
import com.retailonboardpro.repository.RequestRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestService {
    @Autowired
    private RequestRepository requestRepository;
    
    @Autowired
    private ApprovalHistoryRepository approvalHistoryRepository;
    
    @Autowired
    private UserService userService;
    
    public List<RequestDto> getAllRequests() {
        return requestRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<RequestDto> getRequestsByCreator(User creator) {
        return requestRepository.findByCreatorOrderByCreatedAtDesc(creator).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<RequestDto> getRequestsByStatus(String status) {
        return requestRepository.findByStatusOrderByCreatedAtDesc(status).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public RequestDto getRequestById(Long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy yêu cầu với ID: " + id));
        return convertToDto(request);
    }
    
    public RequestDetailsDto getRequestDetailsById(Long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy yêu cầu với ID: " + id));
        
        List<ApprovalHistory> historyList = approvalHistoryRepository.findByRequestOrderByActionAtAsc(request);
        
        RequestDetailsDto detailsDto = new RequestDetailsDto();
        detailsDto.setId(request.getId());
        detailsDto.setCategory(request.getCategory());
        detailsDto.setReason(request.getReason());
        detailsDto.setStatus(request.getStatus());
        detailsDto.setCreatedAt(request.getCreatedAt());
        detailsDto.setUpdatedAt(request.getUpdatedAt());
        
        // Convert creator
        UserDto creatorDto = new UserDto();
        creatorDto.setId(request.getCreator().getId());
        creatorDto.setName(request.getCreator().getName());
        creatorDto.setUsername(request.getCreator().getUsername());
        creatorDto.setEmail(request.getCreator().getEmail());
        creatorDto.setRole(request.getCreator().getRole());
        detailsDto.setCreator(creatorDto);
        
        // Convert history
        List<ApprovalHistoryDto> historyDtos = historyList.stream()
                .map(history -> {
                    ApprovalHistoryDto historyDto = new ApprovalHistoryDto();
                    historyDto.setId(history.getId());
                    historyDto.setRequestId(history.getRequest().getId());
                    historyDto.setAction(history.getAction());
                    historyDto.setComments(history.getComments());
                    historyDto.setActionAt(history.getActionAt());
                    
                    // Convert approver
                    UserDto approverDto = new UserDto();
                    approverDto.setId(history.getApprover().getId());
                    approverDto.setName(history.getApprover().getName());
                    approverDto.setUsername(history.getApprover().getUsername());
                    approverDto.setEmail(history.getApprover().getEmail());
                    approverDto.setRole(history.getApprover().getRole());
                    historyDto.setApprover(approverDto);
                    
                    return historyDto;
                })
                .collect(Collectors.toList());
        
        detailsDto.setHistory(historyDtos);
        
        return detailsDto;
    }
    
    @Transactional
    public RequestDto createRequest(String category, String reason, User creator) {
        Request request = new Request();
        request.setCategory(category);
        request.setReason(reason);
        request.setCreator(creator);
        request.setStatus(AppConstants.STATUS_PENDING_MANAGER);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        
        Request savedRequest = requestRepository.save(request);
        
        // Add to approval history
        ApprovalHistory history = new ApprovalHistory();
        history.setRequest(savedRequest);
        history.setApprover(creator);
        history.setAction(AppConstants.ACTION_CREATE);
        history.setActionAt(LocalDateTime.now());
        approvalHistoryRepository.save(history);
        
        return convertToDto(savedRequest);
    }
    
    @Transactional
    public RequestDto updateRequestStatus(Long id, String status, User approver, String comments) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy yêu cầu với ID: " + id));
        
        request.setStatus(status);
        request.setUpdatedAt(LocalDateTime.now());
        Request updatedRequest = requestRepository.save(request);
        
        // Add to approval history
        ApprovalHistory history = new ApprovalHistory();
        history.setRequest(updatedRequest);
        history.setApprover(approver);
        
        if (status.startsWith("Rejected")) {
            history.setAction(AppConstants.ACTION_REJECT);
        } else {
            history.setAction(AppConstants.ACTION_APPROVE);
        }
        
        history.setComments(comments);
        history.setActionAt(LocalDateTime.now());
        approvalHistoryRepository.save(history);
        
        return convertToDto(updatedRequest);
    }
    
    private RequestDto convertToDto(Request request) {
        RequestDto requestDto = new RequestDto();
        requestDto.setId(request.getId());
        requestDto.setCategory(request.getCategory());
        requestDto.setReason(request.getReason());
        requestDto.setStatus(request.getStatus());
        requestDto.setCreatedAt(request.getCreatedAt());
        requestDto.setUpdatedAt(request.getUpdatedAt());
        
        // Convert creator
        UserDto creatorDto = new UserDto();
        creatorDto.setId(request.getCreator().getId());
        creatorDto.setName(request.getCreator().getName());
        creatorDto.setUsername(request.getCreator().getUsername());
        creatorDto.setEmail(request.getCreator().getEmail());
        creatorDto.setRole(request.getCreator().getRole());
        requestDto.setCreator(creatorDto);
        
        return requestDto;
    }
} 