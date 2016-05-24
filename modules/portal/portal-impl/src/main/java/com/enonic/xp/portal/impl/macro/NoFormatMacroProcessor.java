package com.enonic.xp.portal.impl.macro;


import org.osgi.service.component.annotations.Component;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.macro.MacroContext;

@Component(immediate = true, service = BuiltInMacroProcessor.class)
public class NoFormatMacroProcessor
    implements BuiltInMacroProcessor
{

    @Override
    public String getName()
    {
        return "noformat";
    }

    @Override
    public PortalResponse process( final MacroContext context )
    {
        final String html = context.getBody();
        return PortalResponse.create().body( html ).build();
    }
}
