package com.enonic.wem.portal.script.lib;

import java.util.Iterator;
import java.util.List;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import com.google.common.collect.Lists;

public final class XsltErrorListener
    implements ErrorListener, Iterable<TransformerException>
{
    private final List<TransformerException> list;

    public XsltErrorListener()
    {
        this.list = Lists.newArrayList();
    }

    @Override
    public void warning( final TransformerException e )
        throws TransformerException
    {
        this.list.add( e );
    }

    @Override
    public void error( final TransformerException e )
        throws TransformerException
    {
        this.list.add( e );
    }

    @Override
    public void fatalError( final TransformerException e )
        throws TransformerException
    {
        this.list.add( e );
    }

    @Override
    public Iterator<TransformerException> iterator()
    {
        return this.list.iterator();
    }
}
