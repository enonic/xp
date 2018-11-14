package com.enonic.xp.portal.impl.filter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.filter.FilterScript;
import com.enonic.xp.portal.filter.FilterScriptFactory;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;

@Component
public final class FilterScriptFactoryImpl
    implements FilterScriptFactory
{
    private PortalScriptService scriptService;

    @Override
    public FilterScript fromScript( final ResourceKey script )
    {
        return new FilterScriptImpl( scriptService, script );
    }

    @Reference
    public void setScriptService( final PortalScriptService scriptService )
    {
        this.scriptService = scriptService;
    }

}
