package com.enonic.xp.portal.impl.macro;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.mapper.MapperHelper;
import com.enonic.xp.portal.impl.mapper.PortalRequestMapper;
import com.enonic.xp.portal.macro.MacroContext;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class MacroContextMapper
    implements MapSerializable
{
    private final MacroContext macroContext;

    public MacroContextMapper( final MacroContext macroContext )
    {
        this.macroContext = macroContext;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "name", macroContext.getName() );
        gen.value( "body", macroContext.getBody() );
        MapperHelper.serializeMultimap( "params", gen, macroContext.getParameters() );
        final PortalRequest request = macroContext.getRequest();
        gen.map( "request" );
        if ( request != null )
        {
            new PortalRequestMapper( request ).serialize( gen );
        }
        gen.end();
        gen.value( "document", macroContext.getDocument() );
    }
}
