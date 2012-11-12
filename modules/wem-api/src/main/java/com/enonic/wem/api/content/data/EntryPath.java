package com.enonic.wem.api.content.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.enonic.wem.api.content.type.component.ComponentPath;

public final class EntryPath
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

    public EntryPath( EntryPath path, int index )
    {
        elements = new ArrayList<Element>();
        for ( int i = 0; i < path.elements.size(); i++ )
        {
            final Element el = path.elements.get( i );
            boolean last = i == path.elements.size() - 1;
            if ( last )
            {
                elements.add( new Element( el.getName(), index ) );
            }
            else
            {
                elements.add( el );
            }
        }
    }

    public EntryPath( String path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );

        elements = splitPathIntoElements( path );
    }

    public EntryPath( final EntryPath parentPath, final Element element, final int index )
    {
        Preconditions.checkNotNull( parentPath, "parentPath cannot be null" );
        Preconditions.checkNotNull( element, "element cannot be null" );

        elements = new ArrayList<Element>();
        elements.addAll( parentPath.elements );
        elements.add( new Element( element.getName() + "[" + index + "]" ) );
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
            boolean last = i == path.elements.size() - 1;
            if ( last && !otherElement.hasIndex() && !otherElement.getName().equals( thisElement.getName() ) )
            {
                return false;
            }
            else if ( ( !last || otherElement.hasIndex() ) && !otherElement.equals( thisElement ) )
            {
                return false;
            }
        }

        return true;
    }

    public ComponentPath resolveComponentPath()
    {
        List<String> componentPathElements = new ArrayList<String>();
        for ( Element element : elements )
        {
            componentPathElements.add( element.getName() );
        }
        return new ComponentPath( componentPathElements );
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

    public EntryPath asNewWithIndexAtPath( final int index, final EntryPath path )
    {
        List<Element> pathElements = Lists.newArrayList();
        for ( int i = 0; i < elements.size(); i++ )
        {
            pathElements.add( elements.get( i ) );
            EntryPath possiblyMatchingPath = new EntryPath( pathElements );
            if ( path.equals( possiblyMatchingPath ) )
            {
                pathElements.remove( i );
                pathElements.add( new Element( elements.get( i ).getName() + "[" + index + "]" ) );
            }

        }
        return new EntryPath( pathElements );
    }


    public EntryPath asNewWithoutIndexAtPath( final EntryPath path )
    {
        List<Element> pathElements = Lists.newArrayList();
        for ( int i = 0; i < elements.size(); i++ )
        {
            pathElements.add( new Element( elements.get( i ).getName() ) );
            EntryPath possiblyMatchingPath = new EntryPath( pathElements );
            if ( path.equals( possiblyMatchingPath ) )
            {
                pathElements.remove( i );
                pathElements.add( new EntryPath.Element( elements.get( i ).getName() ) );
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
        private final static String INDEX_START_MARKER = "[";

        private final static String INDEX_STOP_MARKER = "]";

        private String name;

        private boolean hasIndex = false;

        private int index = 0;

        public Element( String element )
        {
            Preconditions.checkNotNull( element, "element cannot be null" );

            int indexStart = element.indexOf( INDEX_START_MARKER );
            int indexStop = element.indexOf( INDEX_STOP_MARKER );

            if ( indexStart >= 0 )
            {
                if ( indexStop > indexStart + 1 )
                {
                    this.hasIndex = true;
                    this.name = element.substring( 0, indexStart );
                    this.index = Integer.parseInt( element.substring( indexStart + 1, indexStop ) );
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
                this.hasIndex = false;
            }
        }

        private Element( String element, int index )
        {
            Preconditions.checkNotNull( element, "element cannot be null" );
            Preconditions.checkArgument( index >= 0, "an index cannot be less than zero" );

            this.name = element;
            this.index = index;
            this.hasIndex = true;
        }

        public String getName()
        {
            return name;
        }

        public boolean hasIndex()
        {
            return hasIndex;
        }

        public int getIndex()
        {
            return index;
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

            return index == element.index && name.equals( element.name );
        }

        @Override
        public int hashCode()
        {
            int result = name.hashCode();
            result = 31 * result + index;
            return result;
        }

        @Override
        public String toString()
        {
            StringBuilder s = new StringBuilder();
            s.append( name );
            if ( hasIndex )
            {
                s.append( INDEX_START_MARKER ).append( index ).append( INDEX_STOP_MARKER );
            }
            return s.toString();
        }
    }
}