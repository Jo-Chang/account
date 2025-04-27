package zerobase.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.account.domain.Account;
import zerobase.account.domain.Member;
import zerobase.account.dto.AccountInfo;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findFirstByOrderByIdDesc();
    Integer countByMember(Member member);
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findAllByMember(Member member);
}
