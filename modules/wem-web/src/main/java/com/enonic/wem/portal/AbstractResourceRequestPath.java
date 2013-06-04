package com.enonic.wem.portal;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public abstract class AbstractResourceRequestPath
{
    protected static final String ELEMENT_DIVIDER = "/";

    protected static final String ROOT = "/";

    protected List<String> elements = Lists.newLinkedList();

    public List<String> getElements()
    {
        return elements;
    }

    public void appendPath( final String element )
    {
        elements.add( element );
    }


    public boolean isRoot()
    {
        return elements.isEmpty();
    }


    public String getRelativePathAsString()
    {
        if ( isRoot() )
        {
            return ROOT;
        }

        return ROOT + Joiner.on( ELEMENT_DIVIDER ).join( elements );
    }

}
