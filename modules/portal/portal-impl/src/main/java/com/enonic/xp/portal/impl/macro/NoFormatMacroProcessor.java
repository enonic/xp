package com.enonic.xp.portal.impl.macro;


import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.macro.MacroContext;
import com.enonic.xp.portal.macro.MacroProcessor;

public class NoFormatMacroProcessor
    implements MacroProcessor
{

    @Override
    public PortalResponse process( final MacroContext context )
    {
        final String html = context.getBody();
        return PortalResponse.create().body( html ).build();
    }
}
