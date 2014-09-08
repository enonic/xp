package com.enonic.wem.thymeleaf.internal;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.enonic.wem.script.ScriptContributorBase;
import com.enonic.wem.thymeleaf.ThymeleafProcessorFactory;

@Singleton
final class ThymeleafScriptContributor
    extends ScriptContributorBase
{
    public ThymeleafScriptContributor()
    {
        addLibrary( "view/thymeleaf", "/lib/view/thymeleaf.js" );
    }

    @Inject
    public void setProcessorFactory( final ThymeleafProcessorFactory processorFactory )
    {
        addVariable( "thymeleafProcessorFactory", processorFactory );
    }
}
