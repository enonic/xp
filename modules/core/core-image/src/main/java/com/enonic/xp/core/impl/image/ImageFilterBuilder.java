package com.enonic.xp.core.impl.image;

import com.google.common.annotations.Beta;

@Beta
public interface ImageFilterBuilder
{
    ImageFilter build( String expr );
}
