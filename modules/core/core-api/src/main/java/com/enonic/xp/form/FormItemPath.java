package com.enonic.xp.form;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertyPath;


@PublicApi
public final class FormItemPath
    implements Iterable<String>
{
    public static final FormItemPath ROOT = new FormItemPath( ImmutableList.of() );

    private static final String ELEMENT_DIVIDER = ".";

    private final ImmutableList<String> elements;

    private FormItemPath( final ImmutableList<String> elementNames )
    {
        this.elements = elementNames;
    }

    public static FormItemPath from( final FormItemPath parentPath, final String name )
    {
        Objects.requireNonNull( parentPath, "parentPath cannot be null" );
        Objects.requireNonNull( name, "name cannot be null" );

        return fromInternal( ImmutableList.<String>builder().addAll( parentPath.elements ).add( name ).build() );
    }

    public static FormItemPath from( final Iterable<String> pathElements )
    {
        Objects.requireNonNull( pathElements, "pathElements cannot be null" );

        return fromInternal( ImmutableList.copyOf( pathElements ) );
    }

    public static FormItemPath from( final String path )
    {
        Objects.requireNonNull( path, "path cannot be null" );

        return fromInternal( ImmutableList.copyOf( path.split( Pattern.quote( ELEMENT_DIVIDER ), -1 ) ));
    }

    public static FormItemPath from( final PropertyPath path )
    {
        Objects.requireNonNull( path, "path cannot be null" );
        return fromInternal( path.pathElements().stream().map( PropertyPath.Element::getName ).collect( ImmutableList.toImmutableList() ) );
    }

    private static FormItemPath fromInternal( final ImmutableList<String> elementNames )
    {
        return elementNames.isEmpty() ? ROOT : new FormItemPath( elementNames );
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

    public List<String> getElements()
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
