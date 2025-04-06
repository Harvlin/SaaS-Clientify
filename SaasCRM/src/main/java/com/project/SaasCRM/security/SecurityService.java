package com.project.SaasCRM.security;

import com.project.SaasCRM.domain.entity.Customer;
import com.project.SaasCRM.domain.entity.Deal;
import com.project.SaasCRM.domain.entity.User;
import com.project.SaasCRM.repository.CustomerRepository;
import com.project.SaasCRM.repository.DealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final CustomerRepository customerRepository;
    private final DealRepository dealRepository;

    public boolean isCurrentUser(Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            User user = (User) auth.getPrincipal();
            return user.getId().equals(userId);
        }
        return false;
    }

    @Transactional(readOnly = true)
    public boolean isAssignedToCustomer(Long customerId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            User user = (User) auth.getPrincipal();
            return customerRepository.findById(customerId)
                .map(customer -> customer.getAssignedUsers().contains(user))
                .orElse(false);
        }
        return false;
    }

    @Transactional(readOnly = true)
    public boolean isAssignedToDeal(Long dealId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            User user = (User) auth.getPrincipal();
            return dealRepository.findById(dealId)
                .map(deal -> deal.getAssignedUsers().contains(user))
                .orElse(false);
        }
        return false;
    }

    public boolean hasRole(String roleName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_" + roleName));
    }

    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    @Transactional(readOnly = true)
    public boolean canAccessCustomer(Long customerId) {
        return isAdmin() || isAssignedToCustomer(customerId);
    }

    @Transactional(readOnly = true)
    public boolean canAccessDeal(Long dealId) {
        return isAdmin() || isAssignedToDeal(dealId);
    }
}

