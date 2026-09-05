package com.razorpay.revenue_recovery.repository;

import com.razorpay.revenue_recovery.model.PaymentFailure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentFailureRepository extends JpaRepository<PaymentFailure, Long> {
    List<PaymentFailure> findAllByOrderByIdDesc();
}