package com.enonic.xp.lib.portal.current;

import com.enonic.xp.lib.content.mapper.ComponentMapper;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.region.Component;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

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
        this.request = context.getBinding( PortalRequest.class ).get();
    }
}
