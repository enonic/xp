package com.enonic.xp.portal.impl.macro;

import com.enonic.xp.macro.MacroContext;
import com.enonic.xp.portal.impl.mapper.MapperHelper;
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
        MapperHelper.serializeMap( "params", gen, macroContext.getParams() );
    }
}
