package com.enonic.xp.portal.macro;

import com.enonic.xp.portal.PortalResponse;


public interface MacroProcessor
{
    PortalResponse process( MacroContext macroContext );
}
