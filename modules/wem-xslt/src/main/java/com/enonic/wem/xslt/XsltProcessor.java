package com.enonic.wem.xslt;

public interface XsltProcessor
{
    public String process( XsltProcessorSpec spec )
        throws XsltProcessorException;
}
