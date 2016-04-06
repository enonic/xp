package com.enonic.xp.portal.impl.macro;

import com.enonic.xp.macro.MacroContext;
import com.enonic.xp.macro.MacroProcessor;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;

public final class MacroProcessorScript
    implements MacroProcessor
{
    private static final String SCRIPT_METHOD_NAME = "macro";

    private final ScriptExports scriptExports;

    public MacroProcessorScript( final ScriptExports scriptExports )
    {
        this.scriptExports = scriptExports;
    }

    @Override
    public String process( final MacroContext macroContext )
    {
        final boolean exists = this.scriptExports.hasMethod( SCRIPT_METHOD_NAME );
        if ( !exists )
        {
            return null;
        }

        final MacroContextMapper macroContextMapper = new MacroContextMapper( macroContext );
        final ScriptValue scriptValue = this.scriptExports.executeMethod( SCRIPT_METHOD_NAME, macroContextMapper );
        return (String) scriptValue.getValue();
    }
}
