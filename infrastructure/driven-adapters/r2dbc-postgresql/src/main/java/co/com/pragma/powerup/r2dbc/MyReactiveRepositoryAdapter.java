package co.com.pragma.powerup.r2dbc;

import co.com.pragma.powerup.model.loanapplication.LoanApplication;
import co.com.pragma.powerup.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.powerup.model.loanapplication.response.LoanApplicationListItem;
import co.com.pragma.powerup.model.loanapplication.response.PageResponse;
import co.com.pragma.powerup.r2dbc.entity.LoanApplicationEntity;
import co.com.pragma.powerup.r2dbc.entity.LoanApplicationListItemEntity;
import co.com.pragma.powerup.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class MyReactiveRepositoryAdapter extends ReactiveAdapterOperations<
    LoanApplication,
        LoanApplicationEntity,
    Long,
    MyReactiveRepository
> implements LoanApplicationRepository {
    public MyReactiveRepositoryAdapter(MyReactiveRepository repository, ObjectMapper mapper) {

        super(repository, mapper, d -> mapper.map(d, LoanApplication.class));
    }
    @Override
    public Mono<PageResponse<LoanApplicationListItem>> findPending(int page, int size, String filter) {
        long offset = (long) page * size;

        return repository.countForAdvisorWithFilter(filter)
                .flatMap(totalItems -> repository.findForAdvisorWithFilter(filter, size, offset)
                        .map(this::mapToDomain)
                        .collectList()
                        .map(items -> {
                            int totalPages = (int) Math.ceil((double) totalItems / size);
                            return new PageResponse<>(page, size, totalItems, totalPages, items);
                        })
                );
    }

    private LoanApplicationListItem mapToDomain(LoanApplicationListItemEntity entity) {
        return LoanApplicationListItem.builder()
                .loanId(entity.getLoanid())
                .amount(entity.getAmount())
                .term(entity.getTerm())
                .email(entity.getEmail())
                .userName(entity.getUsername())
                .loanTypeName(entity.getLoantypename())
                .interestRate(entity.getInterestrate())
                .statusName(entity.getStatusname())
                .baseSalary(entity.getBasesalary())
                .monthlyRequestedAmount(entity.getMonthlyrequestedamount())
                .build();
    }

    @Override
    public Mono<LoanApplication> updateStatus(Long loanId, Long idStatus) {
        return repository.updateStatus(loanId, idStatus)
                .map(la -> mapper.map(la, LoanApplication.class));
    }


}
