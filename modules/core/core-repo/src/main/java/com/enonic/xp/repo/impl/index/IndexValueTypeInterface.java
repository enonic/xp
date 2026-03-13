package com.enonic.xp.repo.impl.index;

import org.jspecify.annotations.NonNull;

public interface IndexValueTypeInterface
{
    String INDEX_VALUE_TYPE_SEPARATOR = ".";

    @NonNull String getPostfix();
}
