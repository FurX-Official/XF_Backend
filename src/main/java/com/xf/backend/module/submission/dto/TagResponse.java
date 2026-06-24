package com.xf.backend.module.submission.dto;

import com.xf.backend.common.enums.TagType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagResponse {

    private Long id;
    private TagType tagType;
    private String tagName;
}
