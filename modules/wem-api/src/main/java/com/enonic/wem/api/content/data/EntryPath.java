package com.enonic.wem.api.content.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Immutable
 */
public final class EntryPath
    implements Iterable<EntryPath.Element>
{
    public final static EntryPath ROOT = new EntryPath();

    public final static String ELEMENT_DIVIDER = ".";

    private final ImmutableList<Element> elements;

    private final boolean relative;

    private final String refString;

    private final EntryPath parentPath;

    public static EntryPath from( final EntryPath parentPath, final String element )
    {
        Preconditions.checkNotNull( parentPath, "parentPath cannot be null" );
        Preconditions.checkNotNull( parentPath, "element cannot be null" );
        return new EntryPath( parentPath, new Element( element ) );
    }

    public static EntryPath from( final EntryPath parentPath, final Element element )
    {
        Preconditions.checkNotNull( parentPath, "parentPath cannot be null" );
        Preconditions.checkNotNull( element, "element cannot be null" );

        return new EntryPath( parentPath, element );
    }

    public static EntryPath from( final Iterable<Element> pathElements )
    {
        Preconditions.checkNotNull( pathElements, "pathElements cannot be null" );
        return new EntryPath( ImmutableList.copyOf( pathElements ) );
    }

    public static EntryPath from( final String path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        return new EntryPath( splitPathIntoElements( path ) );
    }

    public static EntryPath from( final Element... pathElements )
    {
        Preconditions.checkNotNull( pathElements, "pathElements cannot be null" );
        return new EntryPath( ImmutableList.copyOf( pathElements ) );
    }

    private EntryPath()
    {
        this.elements = ImmutableList.of();
        this.refString = "";
        this.relative = false;
        this.parentPath = null;
    }

    public EntryPath( final ImmutableList<Element> pathElements )
    {
        Preconditions.checkNotNull( pathElements, "pathElements cannot be null" );
        this.elements = pathElements;
        this.relative = this.elements.size() <= 0 || !this.elements.get( 0 ).getName().startsWith( ELEMENT_DIVIDER );
        this.refString = toString( this.elements );

        final List<Element> parentPathElements = Lists.newArrayList();
        for ( int i = 0; i < this.elements.size(); i++ )
        {
            if ( i < this.elements.size() - 1 )
            {
                parentPathElements.add( this.elements.get( i ) );
            }
        }
        this.parentPath = parentPathElements.size() > 0 ? EntryPath.from( parentPathElements ) : null;
    }

    private EntryPath( final EntryPath parentPath, final Element element )
    {
        Preconditions.checkNotNull( parentPath, "parentPath cannot be null" );
        Preconditions.checkNotNull( element, "element cannot be null" );
        this.parentPath = parentPath;

        final ImmutableList.Builder<Element> elementBuilder = ImmutableList.builder();
        elementBuilder.addAll( parentPath.pathElements() ).add( element );
        this.elements = elementBuilder.build();
        this.relative = this.elements.size() <= 0 || !this.elements.get( 0 ).getName().startsWith( ELEMENT_DIVIDER );
        this.refString = toString( elements );
    }

    public EntryPath getParent()
    {
        return parentPath;
    }

    public boolean isRelative()
    {
        return relative;
    }

    public Iterable<String> resolvePathElementNames()
    {
        final List<String> pathElements = new ArrayList<>();
        for ( Element element : elements )
        {
            pathElements.add( element.getName() );
        }
        return pathElements;
    }

    public int elementCount()
    {
        return elements.size();
    }

    public Iterator<Element> iterator()
    {
        return elements.iterator();
    }

    public ImmutableList<Element> pathElements()
    {
        return elements;
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

    public EntryPath asNewWithoutFirstPathElement()
    {
        final ImmutableList.Builder<Element> builder = ImmutableList.builder();
        for ( int i = 0; i < elements.size(); i++ )
        {
            if ( i > 0 )
            {
                builder.add( elements.get( i ) );
            }
        }
        return new EntryPath( builder.build() );
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

        final EntryPath other = (EntryPath) o;
        return refString.equals( other.refString );
    }

    @Override
    public int hashCode()
    {
        return refString.hashCode();
    }

    @Override
    public String toString()
    {
        return refString;
    }

    private String toString( final ImmutableList<Element> listOfElements )
    {
        StringBuilder s = new StringBuilder();
        for ( int i = 0, size = listOfElements.size(); i < size; i++ )
        {
            s.append( listOfElements.get( i ) );

            if ( i < listOfElements.size() - 1 )
            {
                s.append( ELEMENT_DIVIDER );
            }
        }
        return s.toString();
    }

    private static ImmutableList<Element> splitPathIntoElements( final String path )
    {
        List<Element> elements = new ArrayList<Element>();

        StringTokenizer st = new StringTokenizer( path, ELEMENT_DIVIDER );
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

    /**
     * Immutable.
     */
    public static class Element
    {
        private final static String INDEX_START_MARKER = "[";

        private final static String INDEX_STOP_MARKER = "]";

        private final String name;

        private final boolean hasIndex;

        private final int index;

        public static Element from( final String element )
        {
            return new Element( element );
        }

        public static Element from( final String name, final int index )
        {
            return new Element( name, index );
        }

        public Element( final String element )
        {
            Preconditions.checkNotNull( element, "Element cannot be null" );
            Preconditions.checkArgument( !StringUtils.isEmpty( element ), "Element cannot be empty" );

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
                this.index = -1;
            }
        }

        public Element( final String name, final int index )
        {
            Preconditions.checkNotNull( name, "Element name cannot be null" );
            Preconditions.checkArgument( !StringUtils.isEmpty( name ), "Element name cannot be empty" );
            Preconditions.checkArgument( index >= 0, "an index cannot be less than zero" );

            this.name = name;
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

            final Element other = (Element) o;

            return index == other.index && name.equals( other.name ) && hasIndex() == other.hasIndex();
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

        public static void checkName( final String name )
        {
            Preconditions.checkArgument( !StringUtils.isBlank( name ), "A name cannot be blank: %s", name );
            Preconditions.checkArgument( !name.contains( EntryPath.ELEMENT_DIVIDER ), "A name cannot contain %s: %s",
                                         EntryPath.ELEMENT_DIVIDER, name );
            Preconditions.checkArgument( !name.contains( INDEX_START_MARKER ), "A name cannot contain array index value. Found %s: %s",
                                         INDEX_START_MARKER, name );
            Preconditions.checkArgument( !name.contains( INDEX_STOP_MARKER ), "A name cannot contain array index value. Found %s: %s",
                                         INDEX_STOP_MARKER, name );
        }
    }
}