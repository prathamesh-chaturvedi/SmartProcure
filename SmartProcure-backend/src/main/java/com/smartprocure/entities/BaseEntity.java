package com.smartprocure.entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@MappedSuperclass
@Getter
@Setter
@ToString
public class BaseEntity {
	
	@CreationTimestamp
	@Column(name= "created_at")
	private LocalDateTime createdAt;
	@UpdateTimestamp
	@Column(name= "last_updated")
	private LocalDateTime lastUpdated;
	
}	
