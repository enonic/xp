package com.enonic.wem.xslt.internal;

import com.enonic.wem.xslt.XsltProcessor;
import com.enonic.wem.xslt.XsltRenderParams;

public final class XsltScriptHelper
{
    private final XsltProcessor processor;

    public XsltScriptHelper( final XsltProcessor processor )
    {
        this.processor = processor;
    }

    public XsltProcessor getProcessor()
    {
        return this.processor;
    }

    public XsltRenderParams newRenderParams()
    {
        return new XsltRenderParams();
    }
}
