package com.enonic.xp.index;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.Value;

@Beta
public interface IndexValueProcessor
{
    Value process( final Value value );

    String getName();
}
