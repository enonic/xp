package com.enonic.wem.mustache.internal;

import com.samskivert.mustache.Mustache;

import com.enonic.wem.mustache.MustacheProcessor;
import com.enonic.wem.mustache.MustacheProcessorFactory;

public final class MustacheProcessorFactoryImpl
    implements MustacheProcessorFactory
{
    private final Mustache.Compiler compiler;

    public MustacheProcessorFactoryImpl()
    {
        this.compiler = Mustache.compiler();
    }

    @Override
    public MustacheProcessor newProcessor()
    {
        return new MustacheProcessorImpl( this.compiler );
    }
}
