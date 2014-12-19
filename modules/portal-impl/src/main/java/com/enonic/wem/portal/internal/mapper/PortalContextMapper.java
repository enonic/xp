package com.enonic.wem.portal.internal.mapper;

import com.enonic.wem.script.serializer.MapGenerator;
import com.enonic.wem.script.serializer.MapSerializable;
import com.enonic.xp.portal.PortalContext;

public final class PortalContextMapper
    implements MapSerializable
{
    private final PortalContext context;

    public PortalContextMapper( final PortalContext context )
    {
        this.context = context;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "method", this.context.getMethod() );
    }
}
