package com.enonic.xp.image;

public interface ImageFilterBuilder
{
    ImageFilter build( BuilderContext context, String expr );
}
