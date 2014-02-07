package com.enonic.wem.portal.postprocess;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

public final class PostProcessModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( PostProcessor.class ).to( PostProcessorImpl.class ).in( Singleton.class );
    }
}
