package co.com.pragma.powerup.r2dbc;


import co.com.pragma.powerup.model.status.Status;
import co.com.pragma.powerup.model.status.gateways.StatusRepository;
import co.com.pragma.powerup.r2dbc.entity.StatusEntity;
import co.com.pragma.powerup.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;

public class StatusRepositoryAdapter extends ReactiveAdapterOperations<
        Status,
        StatusEntity,
        Long,
        StatusReactiveRepository
        > implements StatusRepository {
    private final StatusReactiveRepository statusReactiveRepository;

    public StatusRepositoryAdapter(StatusReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Status.class));
        this.statusReactiveRepository = repository;

    }
    @Override
    public Mono<Status> findByName(String name){
        return this.statusReactiveRepository.findByName(name).map(d -> mapper.map(d, Status.class));
    }


}




