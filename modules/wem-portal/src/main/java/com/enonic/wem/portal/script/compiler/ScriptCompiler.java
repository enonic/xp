package com.enonic.wem.portal.script.compiler;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;

import com.enonic.wem.portal.script.loader.ScriptSource;

public interface ScriptCompiler
{
    public Script compile( final Context context, final ScriptSource source );
}
