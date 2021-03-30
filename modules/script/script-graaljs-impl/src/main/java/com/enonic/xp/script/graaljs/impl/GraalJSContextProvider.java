package com.enonic.xp.script.graaljs.impl;

import org.graalvm.polyglot.Context;

public interface GraalJSContextProvider
{
    Context getContext();
}
