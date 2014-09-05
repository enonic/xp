package com.enonic.wem.mustache.internal;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.enonic.wem.mustache.MustacheProcessorFactory;
import com.enonic.wem.script.ScriptContributorBase;

@Singleton
final class MustacheScriptContributor
    extends ScriptContributorBase
{
    public MustacheScriptContributor()
    {
        addLibrary( "view/mustache", "/lib/view/mustache.js" );
    }

    @Inject
    public void setProcessorFactory( final MustacheProcessorFactory processorFactory )
    {
        addVariable( "mustacheProcessorFactory", processorFactory );
    }
}
