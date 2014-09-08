package com.enonic.wem.xslt.internal;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.enonic.wem.script.ScriptContributorBase;
import com.enonic.wem.xslt.XsltProcessorFactory;

@Singleton
final class XsltScriptContributor
    extends ScriptContributorBase
{
    public XsltScriptContributor()
    {
        addLibrary( "view/xslt", "/lib/view/xslt.js" );
    }

    @Inject
    public void setProcessorFactory( final XsltProcessorFactory processorFactory )
    {
        addVariable( "xsltProcessorFactory", processorFactory );
    }
}
