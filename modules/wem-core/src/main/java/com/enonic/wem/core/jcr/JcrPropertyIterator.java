package com.enonic.wem.core.jcr;

import java.util.Iterator;

public interface JcrPropertyIterator
        extends Iterator<JcrProperty>
{
    public JcrProperty nextProperty();

    public void skip( long skipNum );

    public long getSize();

    public long getPosition();
}
