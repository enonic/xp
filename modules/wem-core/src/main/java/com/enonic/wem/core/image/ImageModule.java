package com.enonic.wem.core.image;

import com.google.inject.AbstractModule;

import com.enonic.wem.core.image.filter.ImageFilterBuilder;
import com.enonic.wem.core.image.filter.ImageFilterBuilderImpl;

public final class ImageModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( ImageFilterBuilder.class ).to( ImageFilterBuilderImpl.class );
    }
}
