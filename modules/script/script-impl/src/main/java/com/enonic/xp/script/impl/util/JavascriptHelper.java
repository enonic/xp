package com.enonic.xp.script.impl.util;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

public interface JavascriptHelper
{
    ScriptObjectMirror newJsArray();

    ScriptObjectMirror newJsObject();
}
