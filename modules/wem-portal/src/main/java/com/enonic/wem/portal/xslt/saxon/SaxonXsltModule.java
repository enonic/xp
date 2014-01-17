package com.enonic.wem.portal.xslt.saxon;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

import com.enonic.wem.portal.xslt.XsltProcessor;

public final class SaxonXsltModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( XsltProcessor.class ).to( SaxonXsltProcessor.class ).in( Singleton.class );
    }
}
