package com.enonic.xp.macro;

import com.google.common.annotations.Beta;

@Beta
public interface MacroProcessor
{

    String process( final MacroContext macroContext );
}
