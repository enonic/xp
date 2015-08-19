package com.enonic.xp.portal.impl.script;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.runtime.ScriptRuntime;
import com.enonic.xp.script.runtime.ScriptRuntimeFactory;
import com.enonic.xp.script.runtime.ScriptSettings;

@Component(immediate = true, service = PortalScriptService.class)
public final class PortalScriptServiceImpl
    implements PortalScriptService
{
    private ScriptRuntimeFactory scriptRuntimeFactory;

    private ScriptRuntime scriptRuntime;

    @Activate
    public void initialize()
    {
        this.scriptRuntime = this.scriptRuntimeFactory.create( ScriptSettings.create().build() );
    }

    @Deactivate
    public void destroy()
    {
        this.scriptRuntimeFactory.dispose( this.scriptRuntime );
    }

    @Override
    public ScriptExports execute( final ResourceKey script )
    {
        return this.scriptRuntime.execute( script );
    }

    @Reference
    public void setScriptRuntimeFactory( final ScriptRuntimeFactory scriptRuntimeFactory )
    {
        this.scriptRuntimeFactory = scriptRuntimeFactory;
    }
}
