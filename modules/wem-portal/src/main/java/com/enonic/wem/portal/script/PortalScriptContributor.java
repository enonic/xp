package com.enonic.wem.portal.script;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.enonic.wem.portal.script.lib.SystemScriptBean;
import com.enonic.wem.script.ScriptContributorBase;

@Singleton
public final class PortalScriptContributor
    extends ScriptContributorBase
{
    @Inject
    public void setSystemScriptBean( final SystemScriptBean bean )
    {
        addGlobalVariable( SystemScriptBean.NAME, bean );
    }
}
