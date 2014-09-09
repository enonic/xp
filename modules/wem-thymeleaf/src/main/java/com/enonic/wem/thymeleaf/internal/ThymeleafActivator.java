package com.enonic.wem.thymeleaf.internal;

import com.enonic.wem.guice.GuiceActivator;
import com.enonic.wem.thymeleaf.ThymeleafProcessorFactory;

public final class ThymeleafActivator
    extends GuiceActivator
{
    @Override
    protected void configure()
    {
        bind( ThymeleafProcessorFactory.class ).to( ThymeleafProcessorFactoryImpl.class );

        service( ThymeleafProcessorFactory.class ).export();
        service( ThymeleafScriptLibrary.class ).export();
    }
}
