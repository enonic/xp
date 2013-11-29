package com.enonic.wem.core.index.document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.enonic.wem.api.BasePath2;
import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.data.Property;

public class IndexDocumentItemPath
{

    private static final char ELEMENT_DIVIDER = '_';

    private final ImmutableList<Element> elements;

    private IndexDocumentItemPath( final Builder builder )
    {
        this.elements = ImmutableList.copyOf( builder.elements );
    }

    public static Builder newIndexDocumentItemPath()
    {
        return new Builder();
    }

    public IndexDocumentItemPath( final ImmutableList<Element> pathElements )
    {
        Preconditions.checkNotNull( pathElements, "pathElements cannot be null" );
        this.elements = pathElements;

        final List<Element> parentPathElements = Lists.newArrayList();
        for ( int i = 0; i < this.elements.size(); i++ )
        {
            if ( i < this.elements.size() - 1 )
            {
                parentPathElements.add( this.elements.get( i ) );
            }
        }
    }

    public static IndexDocumentItemPath from( final String path )
    {
        Preconditions.checkNotNull( "path can not be null" );
        return new IndexDocumentItemPath( splitPathIntoElements( path ) );
    }

    public static IndexDocumentItemPath from( final Property property )
    {
        Builder builder = newIndexDocumentItemPath();

        final Iterator<DataPath.Element> iterator = property.getPath().iterator();

        while ( iterator.hasNext() )
        {
            final DataPath.Element next = iterator.next();
            builder.add( next.getName() );
        }

        return builder.build();
    }

    private static ImmutableList<Element> splitPathIntoElements( final String path )
    {
        List<Element> elements = new ArrayList<Element>();

        StringTokenizer st = new StringTokenizer( path, "/" );
        int count = 0;
        while ( st.hasMoreTokens() )
        {
            count++;
            final String element = st.nextToken();
            if ( count == 1 && path.startsWith( "." ) )
            {
                elements.add( new Element( "." + element ) );
            }
            else
            {
                elements.add( new Element( element ) );
            }

        }
        return ImmutableList.copyOf( elements );
    }

    @Override
    public String toString()
    {
        final StringBuilder s = new StringBuilder();
        for ( int i = 0, size = elements.size(); i < size; i++ )
        {
            s.append( elements.get( i ) );

            if ( i < elements.size() - 1 )
            {
                s.append( ELEMENT_DIVIDER );
            }
        }
        return s.toString();
    }


    public static class Element
        extends BasePath2.Element
    {
        public Element( final String name )
        {
            super( name );
        }
    }

    public static class Builder
    {
        private List<Element> elements;

        public Builder()
        {
            this.elements = Lists.newArrayList();
        }

        public Builder add( final Element element )
        {
            elements.add( element );
            return this;
        }

        public Builder add( final String element )
        {
            elements.add( new Element( element ) );

            return this;
        }

        public IndexDocumentItemPath build()
        {
            return new IndexDocumentItemPath( this );
        }

    }


}
