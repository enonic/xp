package com.enonic.wem.core.jcr.old;

import javax.jcr.Property;
import javax.jcr.PropertyIterator;

class JcrPropertyIteratorImpl
        implements JcrPropertyIterator
{
    private final PropertyIterator propertyIterator;

    JcrPropertyIteratorImpl( PropertyIterator propertyIterator )
    {
        this.propertyIterator = propertyIterator;
    }

    @Override
    public JcrProperty nextProperty()
    {
        final Property nextProp = propertyIterator.nextProperty();
        return ( nextProp == null ) ? null : new JcrPropertyImpl( nextProp );
    }

    @Override
    public void skip( long skipNum )
    {
        propertyIterator.skip( skipNum );
    }

    @Override
    public long getSize()
    {
        return propertyIterator.getSize();
    }

    @Override
    public long getPosition()
    {
        return propertyIterator.getPosition();
    }

    @Override
    public boolean hasNext()
    {
        return propertyIterator.hasNext();
    }

    @Override
    public JcrProperty next()
    {
        return nextProperty();
    }

    @Override
    public void remove()
    {
        propertyIterator.remove();
    }
}
