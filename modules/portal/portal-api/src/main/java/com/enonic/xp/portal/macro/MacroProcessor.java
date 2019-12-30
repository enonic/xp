package com.enonic.xp.portal.macro;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.portal.PortalResponse;

@PublicApi
public interface MacroProcessor
{

    PortalResponse process( final MacroContext macroContext );

}
