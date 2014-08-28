package com.enonic.wem.xslt;

public interface XsltProcessor
{
    public String process( XsltProcessorParams spec )
        throws XsltProcessorException;

    public String render( XsltRenderParams params );
}
