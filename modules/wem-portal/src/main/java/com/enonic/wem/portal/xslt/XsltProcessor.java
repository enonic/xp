package com.enonic.wem.portal.xslt;

public interface XsltProcessor
{
    public String process( XsltProcessorSpec spec )
        throws XsltProcessorException;
}
