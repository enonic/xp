package com.enonic.xp.index;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.Value;

@PublicApi
public interface IndexValueProcessor
{
    Value process( Value value );

    String getName();
}
