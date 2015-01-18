package com.enonic.wem.api.image;

public interface ImageFilterBuilder
{
    ImageFilter build( BuilderContext context, String expr );
}
