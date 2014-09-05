package com.enonic.wem.thymeleaf.internal;

import com.enonic.wem.script.ScriptContributorBase;
import com.enonic.wem.thymeleaf.ThymeleafProcessorFactory;

public final class ThymeleafScriptContributor
    extends ScriptContributorBase
{
    public ThymeleafScriptContributor()
    {
        addLibrary( "view/thymeleaf", "/lib/view/thymeleaf.js" );
    }

    public void setProcessorFactory( final ThymeleafProcessorFactory processorFactory )
    {
        addVariable( "thymeleafProcessorFactory", processorFactory );
    }
}
