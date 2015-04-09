package com.enonic.xp.image;

import com.google.common.annotations.Beta;

@Beta
public interface ImageFilterBuilder
{
    ImageFilter build( String expr );
}
