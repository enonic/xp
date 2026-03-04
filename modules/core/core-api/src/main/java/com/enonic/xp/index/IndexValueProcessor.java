package com.enonic.xp.index;

import com.enonic.xp.data.Value;


public interface IndexValueProcessor
{
    Value process( Value value );

    String getName();
}
