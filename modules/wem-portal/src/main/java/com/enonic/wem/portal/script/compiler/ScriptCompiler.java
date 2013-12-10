package com.enonic.wem.portal.script.compiler;

import java.nio.file.Path;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;

public interface ScriptCompiler
{
    public Script compile( final Context context, final Path path );
}
