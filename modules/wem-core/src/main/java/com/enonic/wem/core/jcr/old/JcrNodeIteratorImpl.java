package com.enonic.wem.core.jcr.old;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

class JcrNodeIteratorImpl
        implements JcrNodeIterator
{
    private final NodeIterator nodeIterator;

    JcrNodeIteratorImpl( NodeIterator nodeIterator )
    {

        this.nodeIterator = nodeIterator;
    }

    @Override
    public JcrNode nextNode()
    {
        final Node nextNode = nodeIterator.nextNode();
        if ( nextNode == null )
        {
            return null;
        }
        return new JcrNodeImpl( nextNode );
    }

    @Override
    public void skip( long skipNum )
    {
        nodeIterator.skip( skipNum );
    }

    @Override
    public long getSize()
    {
        return nodeIterator.getSize();
    }

    @Override
    public long getPosition()
    {
        return nodeIterator.getPosition();
    }

    @Override
    public boolean hasNext()
    {
        return nodeIterator.hasNext();
    }

    @Override
    public JcrNode next()
    {
        return nextNode();
    }

    @Override
    public void remove()
    {
        nodeIterator.remove();
    }
}
