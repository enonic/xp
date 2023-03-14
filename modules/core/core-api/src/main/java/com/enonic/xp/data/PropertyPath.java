package com.enonic.xp.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;

import static com.google.common.base.Strings.isNullOrEmpty;


/**
 * Immutable
 */
@PublicApi
public final class PropertyPath
    implements Iterable<PropertyPath.Element>
{
    public static final PropertyPath ROOT = new PropertyPath();

    public static final String ELEMENT_DIVIDER = ".";

    private final ImmutableList<Element> elements;

    private final boolean relative;

    public static PropertyPath from( final PropertyPath parentPath, final String element )
    {
        Preconditions.checkNotNull( parentPath, "parentPath cannot be null" );
        Preconditions.checkNotNull( element, "element cannot be null" );
        return new PropertyPath( parentPath, new Element( element ) );
    }

    public static PropertyPath from( final PropertyPath parentPath, final Element element )
    {
        Preconditions.checkNotNull( parentPath, "parentPath cannot be null" );
        Preconditions.checkNotNull( element, "element cannot be null" );

        return new PropertyPath( parentPath, element );
    }

    public static PropertyPath from( final Iterable<Element> pathElements )
    {
        Preconditions.checkNotNull( pathElements, "pathElements cannot be null" );
        return new PropertyPath( ImmutableList.copyOf( pathElements ) );
    }

    public static PropertyPath from( final String path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );
        return new PropertyPath( splitPathIntoElements( path ) );
    }

    public static PropertyPath from( final String parentPath, final String... children )
    {
        Preconditions.checkNotNull( parentPath, "parentPath cannot be null" );
        Preconditions.checkNotNull( children, "children cannot be null" );

        final List<Element> elements = new ArrayList<>( splitPathIntoElements( parentPath ) );

        for ( final String child : children )
        {
            elements.add( Element.from( child ) );
        }

        return new PropertyPath( ImmutableList.copyOf( elements ) );
    }

    public static PropertyPath from( final Element... pathElements )
    {
        Preconditions.checkNotNull( pathElements, "pathElements cannot be null" );
        return new PropertyPath( ImmutableList.copyOf( pathElements ) );
    }

    private PropertyPath()
    {
        this.elements = ImmutableList.of();
        this.relative = false;
    }

    private PropertyPath( final ImmutableList<Element> pathElements )
    {
        Preconditions.checkNotNull( pathElements, "pathElements cannot be null" );

        this.elements = pathElements;
        this.relative = this.elements.isEmpty() || !this.elements.get( 0 ).getName().startsWith( ELEMENT_DIVIDER );
    }

    private PropertyPath( final PropertyPath parentPath, final Element element )
    {
        Preconditions.checkNotNull( parentPath, "parentPath cannot be null" );
        Preconditions.checkNotNull( element, "element cannot be null" );

        final ImmutableList.Builder<Element> elementBuilder = ImmutableList.builder();
        elementBuilder.addAll( parentPath.pathElements() ).add( element );
        this.elements = elementBuilder.build();
        this.relative = this.elements.isEmpty() || !this.elements.get( 0 ).getName().startsWith( ELEMENT_DIVIDER );
    }

    public PropertyPath getParent()
    {
        return this.elements.size() <= 1 ? null : PropertyPath.from( this.elements.subList( 0, this.elements.size() - 1 ) );
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

    @Override
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

    public boolean startsWith( final PropertyPath path )
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

    public PropertyPath removeFirstPathElement()
    {
        final ImmutableList.Builder<Element> builder = ImmutableList.builder();
        for ( int i = 0; i < elements.size(); i++ )
        {
            if ( i > 0 )
            {
                builder.add( elements.get( i ) );
            }
        }
        return new PropertyPath( builder.build() );
    }

    public PropertyPath removeIndexFromLastElement()
    {

        if ( this.elements.size() > 1 )
        {
            return PropertyPath.from( this.getParent(), this.getLastElement().getName() );
        }
        else
        {
            return PropertyPath.from( new Element( this.getLastElement().getName() ) );
        }
    }

    public PropertyPath resetAllIndexesTo( final int index )
    {
        final ImmutableList.Builder<Element> builder = ImmutableList.builder();
        for ( int i = 0; i < elements.size(); i++ )
        {
            final Element element = elements.get( i );
            builder.add( new Element( element.getName(), index ) );
        }
        return new PropertyPath( builder.build() );
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

        final PropertyPath other = (PropertyPath) o;
        return elements.equals( other.elements );
    }

    @Override
    public int hashCode()
    {
        return elements.hashCode();
    }

    @Override
    public String toString()
    {
        return elements.stream().map( String::valueOf ).collect( Collectors.joining( ELEMENT_DIVIDER ) );
    }

    private static ImmutableList<Element> splitPathIntoElements( final String path )
    {
        List<Element> elements = new ArrayList<>();

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
        private static final String INDEX_START_MARKER = "[";

        private static final String INDEX_STOP_MARKER = "]";

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
            Preconditions.checkArgument( !isNullOrEmpty( element ), "Element cannot be empty" );

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
                    throw new IllegalArgumentException( "Invalid DataPath element: " + element );
                }
            }
            else
            {
                if ( indexStop >= 0 )
                {
                    throw new IllegalArgumentException( "Invalid DataPath element: " + element );
                }

                this.name = element;
                this.hasIndex = false;
                this.index = 0;
            }
        }

        public Element( final String name, final int index )
        {
            Preconditions.checkNotNull( name, "Element name cannot be null" );
            Preconditions.checkArgument( !isNullOrEmpty( name ), "Element name cannot be empty" );
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
            if ( hasIndex && index > 0 )
            {
                s.append( INDEX_START_MARKER ).append( index ).append( INDEX_STOP_MARKER );
            }
            return s.toString();
        }
    }
}
