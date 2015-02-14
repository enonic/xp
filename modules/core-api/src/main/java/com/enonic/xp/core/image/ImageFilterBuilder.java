package com.enonic.xp.core.image;

public interface ImageFilterBuilder
{
    ImageFilter build( BuilderContext context, String expr );
}
