package net.trenkmann.dev.infrastructure.process;

import java.time.LocalDateTime;
import net.trenkmann.dev.model.source.SourceEntity;
import net.trenkmann.dev.model.target2.Target2Entity;
import org.springframework.batch.item.ItemProcessor;

public class SourceItemTransferToTarget2Processor
    implements ItemProcessor<SourceEntity, Target2Entity> {

  @Override
  public Target2Entity process(SourceEntity item) throws Exception {

    Target2Entity target2Entity = new Target2Entity();
    target2Entity.setCreationDate(LocalDateTime.now());
    target2Entity.setText(item.getText());
    target2Entity.setVar2(item.getVar1() * item.getVar1());

    return target2Entity;
  }
}
