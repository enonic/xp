package com.enonic.xp.script.impl.util;

import javax.script.ScriptEngine;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

public final class NashornHelper
{
    private final static NashornScriptEngineFactory FACTORY = new NashornScriptEngineFactory();

    public static ScriptEngine getScriptEngine( final ClassLoader loader, final String... args )
    {
        return FACTORY.getScriptEngine( args, loader );
    }
}
