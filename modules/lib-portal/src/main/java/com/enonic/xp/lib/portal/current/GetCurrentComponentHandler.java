package com.enonic.xp.lib.portal.current;

import com.enonic.xp.lib.content.mapper.ComponentMapper;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.bean.BeanContext;
import com.enonic.xp.portal.bean.ScriptBean;
import com.enonic.xp.region.Component;

public final class GetCurrentComponentHandler
    implements ScriptBean
{
    private PortalRequest request;

    public ComponentMapper execute()
    {
        final Component component = this.request.getComponent();
        return component != null ? convert( component ) : null;
    }

    private ComponentMapper convert( final Component component )
    {
        return component == null ? null : new ComponentMapper( component );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.request = PortalRequestAccessor.get();
    }
}
