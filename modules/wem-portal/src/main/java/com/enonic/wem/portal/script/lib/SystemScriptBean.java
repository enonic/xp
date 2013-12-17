package com.enonic.wem.portal.script.lib;

import javax.inject.Inject;

public final class SystemScriptBean
{
    public final static String NAME = "system";

    @Inject
    protected MustacheScriptBean mustache;

    public MustacheScriptBean getMustache()
    {
        return this.mustache;
    }
}
