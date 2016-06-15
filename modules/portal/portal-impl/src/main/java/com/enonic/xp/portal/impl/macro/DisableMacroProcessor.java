package com.enonic.xp.portal.impl.macro;


import org.osgi.service.component.annotations.Component;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.macro.MacroContext;

@Component(immediate = true, service = BuiltInMacroProcessor.class)
public class DisableMacroProcessor
    implements BuiltInMacroProcessor
{

    @Override
    public String getName()
    {
        return "disable";
    }

    @Override
    public PortalResponse process( final MacroContext context )
    {
        final String html = context.getBody();
        return PortalResponse.create().body( html ).build();
    }
}
