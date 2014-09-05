package com.enonic.wem.xslt.internal;

import com.enonic.wem.script.ScriptContributorBase;
import com.enonic.wem.xslt.XsltProcessorFactory;

public final class XsltScriptContributor
    extends ScriptContributorBase
{
    public XsltScriptContributor()
    {
        addLibrary( "view/xslt", "/lib/view/xslt.js" );
    }

    public void setProcessorFactory( final XsltProcessorFactory processorFactory )
    {
        addVariable( "xsltProcessorFactory", processorFactory );
    }
}
