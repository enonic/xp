package com.enonic.wem.mustache.internal;

import org.osgi.service.component.annotations.Component;

import com.samskivert.mustache.Mustache;

import com.enonic.wem.mustache.MustacheProcessor;
import com.enonic.wem.mustache.MustacheProcessorFactory;

@Component
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
