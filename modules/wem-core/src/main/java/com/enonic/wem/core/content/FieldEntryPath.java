package com.enonic.wem.core.content;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.enonic.wem.core.content.config.field.FieldPath;

public class FieldEntryPath
    implements Iterable<FieldEntryPath.Element>
{
    private final static String ELEMENT_DIVIDER = ".";

    private List<Element> elements;

    public FieldEntryPath( List<Element> pathElements )
    {
        Preconditions.checkNotNull( pathElements );
        elements = pathElements;
    }

    public FieldEntryPath( FieldEntryPath fieldPath, String name )
    {
        elements = new ArrayList<Element>();
        elements.addAll( fieldPath.elements );
        elements.add( new Element( name ) );
    }

    public FieldEntryPath( String path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );

        elements = splitPathIntoElements( path );
    }

    public FieldPath resolveFieldPath()
    {
        List<String> fieldPathElements = new ArrayList<String>();
        for ( Element element : elements )
        {
            fieldPathElements.add( element.getName() );
        }
        return new FieldPath( fieldPathElements );
    }

    public FieldEntryPath asNewUsingFirstPathElement()
    {
        List<Element> pathElements = Lists.newArrayList();
        pathElements.add( elements.get( 0 ) );
        return new FieldEntryPath( pathElements );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final FieldEntryPath fieldEntryPath = (FieldEntryPath) o;

        return elements.equals( fieldEntryPath.elements );
    }

    @Override
    public int hashCode()
    {
        return elements.hashCode();
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();
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

    private static List<Element> splitPathIntoElements( String path )
    {
        List<Element> elements = new ArrayList<Element>();

        StringTokenizer st = new StringTokenizer( path, ELEMENT_DIVIDER );
        while ( st.hasMoreTokens() )
        {
            elements.add( new Element( st.nextToken() ) );
        }
        return elements;
    }

    public Iterator<Element> iterator()
    {
        return elements.iterator();
    }

    public static class Element
    {
        private final static String POSITION_START_MARKER = "[";

        private final static String POSITION_STOP_MARKER = "]";

        private String name;

        private boolean hasPosition = false;

        private int position = 0;

        public Element( String element )
        {
            Preconditions.checkNotNull( element );

            int indexStart = element.indexOf( POSITION_START_MARKER );
            int indexStop = element.indexOf( POSITION_STOP_MARKER );

            if ( indexStart >= 0 )
            {
                if ( indexStop > indexStart + 1 )
                {
                    this.hasPosition = true;
                    this.name = element.substring( 0, indexStart );
                    this.position = Integer.parseInt( element.substring( indexStart + 1, indexStop ) );
                }
                else
                {
                    throw new IllegalArgumentException( "Invalid FieldEntryPath element: " + element );
                }
            }
            else
            {
                if ( indexStop >= 0 )
                {
                    throw new IllegalArgumentException( "Invalid FieldEntryPath element: " + element );
                }

                this.name = element;
                this.hasPosition = false;
            }
        }

        public String getName()
        {
            return name;
        }

        public int getPosition()
        {
            return position;
        }

        @Override
        public boolean equals( final Object o )
        {
            if ( this == o )
            {
                return true;
            }
            if ( o == null || getClass() != o.getClass() )
            {
                return false;
            }

            final Element element = (Element) o;

            return position == element.position && name.equals( element.name );
        }

        @Override
        public int hashCode()
        {
            int result = name.hashCode();
            result = 31 * result + position;
            return result;
        }

        @Override
        public String toString()
        {
            StringBuilder s = new StringBuilder();
            s.append( name );
            if ( hasPosition )
            {
                s.append( POSITION_START_MARKER ).append( position ).append( POSITION_STOP_MARKER );
            }
            return s.toString();
        }
    }
}