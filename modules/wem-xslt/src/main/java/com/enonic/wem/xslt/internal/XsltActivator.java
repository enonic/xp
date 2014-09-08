package com.enonic.wem.xslt.internal;

import com.enonic.wem.guice.GuiceActivator;
import com.enonic.wem.xslt.XsltProcessorFactory;

public final class XsltActivator
    extends GuiceActivator
{
    @Override
    protected void configure()
    {
        bind( XsltProcessorFactory.class ).to( XsltProcessorFactoryImpl.class );

        service( XsltProcessorFactory.class ).export();
        service( XsltScriptContributor.class ).export();
        service( XsltScriptLibrary.class ).export();
    }
}
