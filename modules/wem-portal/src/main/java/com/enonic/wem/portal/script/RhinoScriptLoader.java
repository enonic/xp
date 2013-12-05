package com.enonic.wem.portal.script;

import org.mozilla.javascript.NativeObject;

public interface RhinoScriptLoader
{
    public NativeObject load( String name, ScriptLocations locations )
        throws Exception;
}
