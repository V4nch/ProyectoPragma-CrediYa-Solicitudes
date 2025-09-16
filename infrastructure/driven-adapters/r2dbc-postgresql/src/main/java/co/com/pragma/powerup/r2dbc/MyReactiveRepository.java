package co.com.pragma.powerup.r2dbc;

import co.com.pragma.powerup.r2dbc.entity.LoanApplicationEntity;
import co.com.pragma.powerup.r2dbc.entity.LoanApplicationListItemEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface MyReactiveRepository extends ReactiveCrudRepository<LoanApplicationEntity,
        Long>, ReactiveQueryByExampleExecutor<LoanApplicationEntity>
{
    @Query("""
        SELECT
            la.loan_id as loanId,
            la.amount,
            la.term,
            la.email,
            u.name AS userName,
            lt.name AS loanTypeName,
            lt.interest_rate AS interestRate,
            s.name AS statusName,
            u.base_salary AS baseSalary,
            (la.amount * (1 + lt.interest_rate) / la.term) AS monthlyRequestedAmount
        FROM loanapplications la
        JOIN loantypes lt ON la.id_loan_type = lt.id_loan_type
        JOIN status s ON la.id_status = s.id_status
        JOIN users u ON la.email = u.email_address
        WHERE s.name IN ('Pendiente de revision', 'Rechazadas', 'Revision manual')
          AND (LOWER(la.email) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR LOWER(lt.name) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR LOWER(s.name) LIKE LOWER(CONCAT('%', :filter, '%')))
        LIMIT :limit OFFSET :offset;
    """)
    Flux<LoanApplicationListItemEntity> findForAdvisorWithFilter(@Param("filter") String filter,
                                                                 @Param("limit") int limit,
                                                                 @Param("offset") long offset);


    @Query("""
        SELECT COUNT(*)
        FROM loanapplications la
        JOIN loantypes lt ON la.id_loan_type = lt.id_loan_type
        JOIN status s ON la.id_status = s.id_status
        JOIN users u ON la.email = u.email_address
        WHERE s.name IN ('Pendiente de revision', 'Rechazadas', 'Revision manual')
          AND (LOWER(la.email) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR LOWER(lt.name) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR LOWER(s.name) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR LOWER(u.base_salary::text) LIKE LOWER(CONCAT('%', :filter, '%')))
    """)
    Mono<Long> countForAdvisorWithFilter(@Param("filter") String filter);

    @Query("UPDATE loanapplications SET id_status = :idStatus WHERE loan_id = :loanId RETURNING *")
    Mono<LoanApplicationEntity> updateStatus(@Param("loanId") Long loanId,
                                             @Param("idStatus") Long idStatus);
}

