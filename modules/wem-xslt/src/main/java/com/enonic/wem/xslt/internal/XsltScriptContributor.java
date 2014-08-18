package com.enonic.wem.xslt.internal;

import com.enonic.wem.script.ScriptContributorBase;
import com.enonic.wem.xslt.XsltProcessor;

public final class XsltScriptContributor
    extends ScriptContributorBase
{
    public void setProcessor( final XsltProcessor processor )
    {
        addVariable( "xsltProcessor", processor );
    }
}
