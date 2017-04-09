package com.enonic.xp.lib.router;

import jdk.nashorn.api.scripting.JSObject;

@SuppressWarnings("WeakerAccess")
public interface RouteMatch
{
    JSObject getHandler();

    void appendPathParams( JSObject object );
}
