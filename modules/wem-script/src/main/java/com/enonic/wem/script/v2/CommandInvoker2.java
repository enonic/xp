package com.enonic.wem.script.v2;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

public interface CommandInvoker2
{
    public Object invoke( String name, ScriptObjectMirror input );
}
