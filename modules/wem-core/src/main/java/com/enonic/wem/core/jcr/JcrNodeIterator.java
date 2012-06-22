package com.enonic.wem.core.jcr;

import java.util.Iterator;

public interface JcrNodeIterator extends Iterator<JcrNode>
{
    public JcrNode nextNode();

    public void skip( long skipNum );

    public long getSize();

    public long getPosition();

}
