package co.com.pragma.powerup.r2dbc;

import co.com.pragma.powerup.r2dbc.entity.LoanApplicationEntity;
import co.com.pragma.powerup.r2dbc.entity.LoanApplicationListItemEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface MyReactiveRepository extends ReactiveCrudRepository<LoanApplicationEntity,
        Long>, ReactiveQueryByExampleExecutor<LoanApplicationEntity>
{
    @Query("SELECT la.amount, la.term, la.email, " +
            "lt.name AS loanTypeName, lt.interest_rate AS interestRate, " +
            "s.name AS statusName" +
            "FROM loanapplications la " +
            "JOIN loantypes lt ON la.id_loan_type = lt.id_loan_type " +
            "JOIN status s ON la.id_status = s.id_status " +
            "WHERE s.name IN ('Pendiente de revision', 'Rechazadas', 'Revision manual') " +
            "AND (LOWER(la.email) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "  OR LOWER(lt.name) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "  OR LOWER(s.name) LIKE LOWER(CONCAT('%', :filter, '%'))) " +
            "LIMIT :limit OFFSET :offset")
    Flux<LoanApplicationListItemEntity> findForAdvisorWithFilter(String filter, int limit, long offset);

    @Query("SELECT COUNT(*) " +
            "FROM loanapplications la " +
            "JOIN status s ON la.id_status = s.id_status " +
            "JOIN loantypes lt ON la.id_loan_type = lt.id_loan_type " +
            "WHERE s.name IN ('Pendiente de revision', 'Rechazadas', 'Revision manual') " +
            "AND (LOWER(la.email) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "  OR LOWER(lt.name) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "  OR LOWER(s.name) LIKE LOWER(CONCAT('%', :filter, '%')))  ")
    Mono<Long> countForAdvisorWithFilter(String filter);
}
