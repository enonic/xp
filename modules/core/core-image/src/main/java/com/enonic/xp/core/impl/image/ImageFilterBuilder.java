package com.enonic.xp.core.impl.image;

import com.enonic.xp.core.impl.image.parser.FilterSetExpr;

public interface ImageFilterBuilder
{
    ImageFunction build( FilterSetExpr expr );
}
