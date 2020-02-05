package net.trenkmann.dev.repository.target1;

import net.trenkmann.dev.model.target1.Target1Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface Target1Repository
    extends JpaRepository<Target1Entity, Integer>, JpaSpecificationExecutor<Target1Entity> {

  Target1Entity findFirstByOrderByIdDesc();
}
