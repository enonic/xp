package com.enonic.xp.portal.macro;

import com.google.common.annotations.Beta;

import com.enonic.xp.portal.PortalResponse;

@Beta
public interface MacroProcessor
{

    PortalResponse process( final MacroContext macroContext );

}
