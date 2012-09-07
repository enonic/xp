package com.enonic.wem.core.content.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.enonic.wem.core.content.type.formitem.FormItemPath;

public class EntryPath
    implements Iterable<EntryPath.Element>
{
    private final static String ELEMENT_DIVIDER = ".";

    private List<Element> elements;

    public EntryPath()
    {
        elements = new ArrayList<Element>();
    }

    public EntryPath( List<Element> pathElements )
    {
        Preconditions.checkNotNull( pathElements, "pathElements cannot be null" );
        elements = pathElements;
    }

    public EntryPath( EntryPath parentPath, String element )
    {
        this( parentPath, new Element( element ) );
    }

    public EntryPath( EntryPath parentPath, Element element )
    {
        Preconditions.checkNotNull( parentPath, "parentPath cannot be null" );
        Preconditions.checkNotNull( element, "element cannot be null" );

        elements = new ArrayList<Element>();
        elements.addAll( parentPath.elements );
        elements.add( element );
    }

    public EntryPath( String path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );

        elements = splitPathIntoElements( path );
    }

    public Element getFirstElement()
    {
        return elements.get( 0 );
    }

    public Element getLastElement()
    {
        return elements.get( elements.size() - 1 );
    }


    public boolean startsWith( final EntryPath path )
    {
        if ( path.elements.size() > this.elements.size() )
        {
            return false;
        }

        for ( int i = 0; i < path.elements.size(); i++ )
        {
            final Element thisElement = elements.get( i );
            final Element otherElement = path.elements.get( i );
            if ( !otherElement.equals( thisElement ) )
            {
                return false;
            }
        }

        return true;
    }

    public FormItemPath resolveFormItemPath()
    {
        List<String> formItemPathElements = new ArrayList<String>();
        for ( Element element : elements )
        {
            formItemPathElements.add( element.getName() );
        }
        return new FormItemPath( formItemPathElements );
    }

    public EntryPath asNewWithoutFirstPathElement()
    {
        List<Element> pathElements = Lists.newArrayList();
        for ( int i = 0; i < elements.size(); i++ )
        {
            if ( i > 0 )
            {
                pathElements.add( elements.get( i ) );
            }
        }
        return new EntryPath( pathElements );
    }

    public int elementCount()
    {
        return elements.size();
    }

    public Iterator<Element> iterator()
    {
        return elements.iterator();
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

        final EntryPath entryPath = (EntryPath) o;

        return elements.equals( entryPath.elements );
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


    public static class Element
    {
        private final static String POSITION_START_MARKER = "[";

        private final static String POSITION_STOP_MARKER = "]";

        private String name;

        private boolean hasPosition = false;

        private int position = 0;

        public Element( String element )
        {
            Preconditions.checkNotNull( element, "element cannot be null" );

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
                    throw new IllegalArgumentException( "Invalid EntryPath element: " + element );
                }
            }
            else
            {
                if ( indexStop >= 0 )
                {
                    throw new IllegalArgumentException( "Invalid EntryPath element: " + element );
                }

                this.name = element;
                this.hasPosition = false;
            }
        }

        public String getName()
        {
            return name;
        }

        public boolean hasPosition()
        {
            return hasPosition;
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