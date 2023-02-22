package com.enonic.xp.form;

import java.util.Iterator;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;


@PublicApi
public final class FormItemPath
    implements Iterable<String>
{
    public static final FormItemPath ROOT = new FormItemPath( ImmutableList.of(), false );

    private static final String ELEMENT_DIVIDER = ".";

    private final ImmutableList<String> elements;

    @Deprecated
    public FormItemPath( final FormItemPath parentPath, final String name )
    {
        Preconditions.checkNotNull( parentPath, "parentPath cannot be null" );
        Preconditions.checkNotNull( name, "name cannot be null" );

        this.elements = ImmutableList.<String>builder().addAll( parentPath.elements ).add( name ).build();
    }

    @Deprecated
    public FormItemPath( final ImmutableList<String> elementNames )
    {
        this( Preconditions.checkNotNull( elementNames, "elementNames cannot be null" ), false );
    }

    private FormItemPath( final ImmutableList<String> elementNames, boolean ignore )
    {
        this.elements = elementNames;
    }

    public static FormItemPath from( final FormItemPath parentPath, final String name )
    {
        Preconditions.checkNotNull( parentPath, "parentPath cannot be null" );
        Preconditions.checkNotNull( name, "name cannot be null" );

        return fromInternal( ImmutableList.<String>builder().addAll( parentPath.elements ).add( name ).build() );
    }

    public static FormItemPath from( final Iterable<String> pathElements )
    {
        Preconditions.checkNotNull( pathElements, "pathElements cannot be null" );

        return fromInternal( ImmutableList.copyOf( pathElements ) );
    }

    public static FormItemPath from( final String path )
    {
        Preconditions.checkNotNull( path, "path cannot be null" );

        return fromInternal( ImmutableList.copyOf( path.split( Pattern.quote( ELEMENT_DIVIDER ), -1 ) ));
    }

    private static FormItemPath fromInternal( final ImmutableList<String> elementNames )
    {
        return elementNames.isEmpty() ? ROOT : new FormItemPath( elementNames, false );
    }

    public FormItemPath getParent()
    {
        return elements.isEmpty() ? null : FormItemPath.fromInternal( elements.subList( 0, elements.size() - 1 ) );
    }

    public String getFirstElement()
    {
        return elements.get( 0 );
    }

    public String getLastElement()
    {
        return elements.get( elements.size() - 1 );
    }

    public int elementCount()
    {
        return elements.size();
    }

    public ImmutableList<String> getElements()
    {
        return elements;
    }

    public String[] getElementsAsArray()
    {
        return this.elements.toArray( String[]::new );
    }

    public FormItemPath asNewWithoutFirstPathElement()
    {
        return elements.isEmpty() ? ROOT : FormItemPath.fromInternal( elements.subList( 1, elements.size() ) );
    }

    @Override
    public Iterator<String> iterator()
    {
        return elements.iterator();
    }

    @Override
    public boolean equals( final Object o )
    {
        return this == o || o instanceof FormItemPath && elements.equals( ( (FormItemPath) o ).elements );
    }

    @Override
    public int hashCode()
    {
        return elements.hashCode();
    }

    @Override
    public String toString()
    {
        return String.join( ELEMENT_DIVIDER, elements );
    }

    public static boolean hasNotPathElementDivider( final String s )
    {
        return !s.contains( ELEMENT_DIVIDER );
    }
}
