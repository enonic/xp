package com.enonic.wem.core.script.compiler;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;

import com.enonic.wem.api.resource.Resource;

public interface ScriptCompiler
{
    public Script compile( final Context context, final Resource resource );
}
