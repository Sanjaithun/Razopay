package com.razorpay.revenue_recovery.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FailureTransactionRepository extends JpaRepository<FailureTransaction, String> {
}