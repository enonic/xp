package com.enonic.xp.script.graaljs.impl.util;

import org.graalvm.polyglot.Value;

public interface JavascriptHelper
{
    Value newJsArray();

    Value newJsObject();

    Value parseJson( String text );
}
