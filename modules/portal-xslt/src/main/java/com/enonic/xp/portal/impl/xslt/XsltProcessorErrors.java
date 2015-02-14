package com.enonic.xp.portal.impl.xslt;

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

    public void error( final TransformerException e )
    {
        this.errors.add( e );
    }

    public void fatalError( final TransformerException e )
    {
        this.errors.add( e );
    }

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
