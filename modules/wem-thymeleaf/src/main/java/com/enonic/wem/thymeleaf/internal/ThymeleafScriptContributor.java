package com.enonic.wem.thymeleaf.internal;

import com.enonic.wem.script.ScriptContributorBase;
import com.enonic.wem.thymeleaf.ThymeleafProcessor;

public final class ThymeleafScriptContributor
    extends ScriptContributorBase
{
    public ThymeleafScriptContributor()
    {
        addLibrary( "view/thymeleaf", "/lib/view/thymeleaf.js" );
    }

    public void setProcessor( final ThymeleafProcessor processor )
    {
        addVariable( "thymeleafProcessor", processor );
    }
}
