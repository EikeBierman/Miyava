package com.miyava.common;

import com.google.common.base.Function;

public interface EntityToDTOMapper<SourceType, TargetType>
    extends Function<SourceType, TargetType> {}
