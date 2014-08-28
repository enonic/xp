package com.enonic.wem.xslt.internal;

import com.enonic.wem.script.ScriptContributorBase;
import com.enonic.wem.xslt.XsltProcessor;

public final class XsltScriptContributor
    extends ScriptContributorBase
{
    public XsltScriptContributor()
    {
        addLibrary( "view/xslt", "/lib/view/xslt.js" );
    }

    public void setProcessor( final XsltProcessor processor )
    {
        addVariable( "xsltScriptHelper", new XsltScriptHelper( processor ) );
    }
}
