package com.enonic.wem.portal.script.lib;

import javax.inject.Inject;

public final class SystemScriptBean
{
    public final static String NAME = "system";

    @Inject
    protected MustacheScriptBean mustache;

    @Inject
    protected XsltScriptBean xslt;

    public MustacheScriptBean getMustache()
    {
        return this.mustache;
    }

    public XsltScriptBean getXslt()
    {
        return this.xslt;
    }
}
