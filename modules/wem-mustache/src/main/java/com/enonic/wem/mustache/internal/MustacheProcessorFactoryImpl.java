package com.enonic.wem.mustache.internal;

import javax.inject.Singleton;

import com.samskivert.mustache.Mustache;

import com.enonic.wem.mustache.MustacheProcessor;
import com.enonic.wem.mustache.MustacheProcessorFactory;

@Singleton
final class MustacheProcessorFactoryImpl
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
