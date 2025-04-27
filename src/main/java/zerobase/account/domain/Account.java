package zerobase.account.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;
import zerobase.account.type.AccountStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class Account extends BaseEntity {
    @ManyToOne
    private Member member;
    private String accountNumber;
    private Long balance;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    private LocalDateTime registeredAt;
    private LocalDateTime unRegisteredAt;
}
