package com.enonic.xp.portal.jslib.impl;

import org.osgi.service.component.annotations.Component;

import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.script.command.CommandHandler;
import com.enonic.wem.script.command.CommandRequest;
import com.enonic.wem.script.mapper.PageComponentMapper;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalContextAccessor;

@Component(immediate = true)
public final class GetComponentHandler
    implements CommandHandler
{
    @Override
    public String getName()
    {
        return "portal.getComponent";
    }

    @Override
    public Object execute( final CommandRequest req )
    {
        final PortalContext context = PortalContextAccessor.get();
        final PageComponent pageComponent = context.getComponent();
        return pageComponent != null ? convert( pageComponent ) : null;
    }

    private Object convert( final PageComponent component )
    {
        return component == null ? null : new PageComponentMapper( component );
    }

}
