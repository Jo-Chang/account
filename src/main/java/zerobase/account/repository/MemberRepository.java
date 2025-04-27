package zerobase.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.account.domain.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
}
