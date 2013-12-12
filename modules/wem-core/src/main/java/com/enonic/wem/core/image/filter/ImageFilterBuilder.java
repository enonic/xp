package com.enonic.wem.core.image.filter;

import com.google.inject.ImplementedBy;

@ImplementedBy(ImageFilterBuilderImpl.class)
public interface ImageFilterBuilder
{
    ImageFilter build( BuilderContext context, String expr );
}
