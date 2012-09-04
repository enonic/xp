package com.enonic.wem.web.jsp.bundle;

import javax.servlet.jsp.tagext.TagSupport;

public final class BundleTag
    extends TagSupport
{
    public void addJavaScript(final String pattern)
    {
        System.out.println(pattern);
    }
}
