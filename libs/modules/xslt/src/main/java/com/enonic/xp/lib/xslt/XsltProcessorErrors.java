package com.enonic.xp.lib.xslt;

import java.util.Iterator;
import java.util.List;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import com.google.common.collect.Lists;

final class XsltProcessorErrors
    implements ErrorListener, Iterable<TransformerException>
{
    private final List<TransformerException> errors;

    public XsltProcessorErrors()
    {
        this.errors = Lists.newArrayList();
    }

    public boolean hasErrors()
    {
        return !this.errors.isEmpty();
    }

    @Override
    public void error( final TransformerException e )
    {
        this.errors.add( e );
    }

    @Override
    public void fatalError( final TransformerException e )
    {
        this.errors.add( e );
    }

    @Override
    public void warning( final TransformerException e )
    {
        // Do nothing
    }

    @Override
    public Iterator<TransformerException> iterator()
    {
        return this.errors.iterator();
    }
}
