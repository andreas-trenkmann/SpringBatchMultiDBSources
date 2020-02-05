package net.trenkmann.dev.infrastructure.process;

import java.time.LocalDateTime;
import net.trenkmann.dev.model.source.SourceEntity;
import net.trenkmann.dev.model.target1.Target1Entity;
import org.springframework.batch.item.ItemProcessor;

public class SourceItemTransferToTarget1Processor
    implements ItemProcessor<SourceEntity, Target1Entity> {

  @Override
  public Target1Entity process(SourceEntity item) throws Exception {
    Target1Entity target1Entity = new Target1Entity();
    target1Entity.setCreationDate(LocalDateTime.now());
    target1Entity.setText(item.getText());
    target1Entity.setVar1(item.getVar1() + item.getVar1());
    return target1Entity;
  }
}
