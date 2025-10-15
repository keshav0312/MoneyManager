package com.moneymanger.moneytracker.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tbl_profiles")
public class ProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   private  long id;
   private  String FullName;
   @Column(unique = true)
   private String email;
   private String password;
   private String profileImageUrl;
   @CreationTimestamp
   @Column(updatable = false)
   private LocalDateTime createdAt;
   @UpdateTimestamp
   private LocalDateTime updatedAt;
   private  Boolean isActive;
    private  String activationToken;

    @PrePersist
    public  void  prePersist()
    {
        if(this.isActive==null)
        {
            isActive=false;
        }
    }
}
