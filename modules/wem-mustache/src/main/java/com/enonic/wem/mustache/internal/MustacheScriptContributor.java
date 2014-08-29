package com.enonic.wem.mustache.internal;

import com.enonic.wem.mustache.MustacheProcessorFactory;
import com.enonic.wem.script.ScriptContributorBase;

public final class MustacheScriptContributor
    extends ScriptContributorBase
{
    public MustacheScriptContributor()
    {
        addLibrary( "view/mustache", "/lib/view/mustache.js" );
    }

    public void setProcessorFactory( final MustacheProcessorFactory processorFactory )
    {
        addVariable( "mustacheProcessorFactory", processorFactory );
    }
}
