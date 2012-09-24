package com.enonic.wem.core.jcr.old;

import java.util.List;
import java.util.ListIterator;

public class MockJcrNodeIterator
    implements JcrNodeIterator
{
    private final List<MockJcrNode> nodes;

    private final ListIterator<MockJcrNode> iterator;

    public MockJcrNodeIterator( final List<MockJcrNode> nodes )
    {
        this.nodes = nodes;
        this.iterator = nodes.listIterator();
    }

    @Override
    public JcrNode nextNode()
    {
        return this.next();
    }

    @Override
    public void skip( final long skipNum )
    {
        for ( long i = 0; i < skipNum; i++ )
        {
            if ( this.iterator.hasNext() )
            {
                this.iterator.next();
            }
            else
            {
                break;
            }
        }
    }

    @Override
    public long getSize()
    {
        return nodes.size();
    }

    @Override
    public long getPosition()
    {
        return this.iterator.nextIndex();
    }

    @Override
    public boolean hasNext()
    {
        return this.iterator.hasNext();
    }

    @Override
    public JcrNode next()
    {
        return this.iterator.next();
    }

    @Override
    public void remove()
    {
        this.iterator.remove();
    }
}
