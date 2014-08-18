package com.enonic.wem.portal.thymeleaf;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

import com.enonic.wem.portal.thymeleaf.ThymeleafProcessor;
import com.enonic.wem.portal.thymeleaf.ThymeleafProcessorImpl;

public final class ThymeleafModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( ThymeleafProcessor.class ).to( ThymeleafProcessorImpl.class ).in( Singleton.class );
    }
}
