package com.enonic.wem.thymeleaf.internal;

import com.enonic.wem.script.ScriptContributorBase;
import com.enonic.wem.thymeleaf.ThymeleafProcessor;

public final class ThymeleafScriptContributor
    extends ScriptContributorBase
{
    public void setProcessor( final ThymeleafProcessor processor )
    {
        addVariable( "thymeleafProcessor", processor );
    }
}
