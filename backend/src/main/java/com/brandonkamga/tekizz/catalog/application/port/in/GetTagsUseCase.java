package com.brandonkamga.tekizz.catalog.application.port.in;

import com.brandonkamga.tekizz.domain.Tag;
import java.util.List;

public interface GetTagsUseCase {
    List<Tag> getActiveTags();
    List<Tag> getTagsByCategoryId(Long categoryId);
}
