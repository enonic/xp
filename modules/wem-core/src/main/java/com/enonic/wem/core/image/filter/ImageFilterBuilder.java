package com.enonic.wem.core.image.filter;

public interface ImageFilterBuilder
{
    ImageFilter build( BuilderContext context, String expr );
}
