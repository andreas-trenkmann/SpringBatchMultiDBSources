package net.trenkmann.dev.repository.source;

import net.trenkmann.dev.model.source.SourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SourceRepository
    extends JpaRepository<SourceEntity, Integer>, JpaSpecificationExecutor<SourceEntity> {}
