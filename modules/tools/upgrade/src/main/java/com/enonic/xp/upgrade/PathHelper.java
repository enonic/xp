package com.enonic.xp.upgrade;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public final class PathHelper
{
    static String getLastElement( final Path path )
    {
        return resolvePathElements( path ).getLast();
    }

    static LinkedList<String> resolvePathElements( final Path path )
    {
        final LinkedList<String> elements = Lists.newLinkedList();

        final String[] elementArray = path.toString().split( Pattern.quote( path.getFileSystem().getSeparator() ) );

        for ( final String element : elementArray )
        {
            if ( !Strings.isNullOrEmpty( element ) )
            {
                elements.add( element );
            }
        }

        return elements;
    }

    public static Path subtractPath( final Path path, final Path subtract )
    {
        final LinkedList<String> pathElements = resolvePathElements( path );
        final LinkedList<String> subtractElements = resolvePathElements( subtract );

        Preconditions.checkArgument( pathElements.size() >= subtractElements.size(),
                                     "No point in trying to remove [" + subtract + "] from [" + path + "]" );

        for ( int i = 0; i < subtractElements.size(); i++ )
        {
            if ( !pathElements.get( i ).equals( subtractElements.get( i ) ) )
            {
                throw new IllegalArgumentException( subtractElements.get( i ) + " is not present in the path to substract from" );
            }
        }

        final PathBuilder pathBuilder = new PathBuilder();

        for ( int i = subtractElements.size(); i < pathElements.size(); i++ )
        {
            pathBuilder.add( pathElements.get( i ) );
        }

        return pathBuilder.build();
    }


    private final static class PathBuilder
    {
        private final List<String> elements = Lists.newArrayList();

        private String first;

        void add( final String element )
        {
            if ( this.first == null )
            {
                this.first = element;
            }
            else
            {
                this.elements.add( element );
            }
        }

        Path build()
        {
            return Paths.get( this.first, this.elements.toArray( new String[this.elements.size()] ) );
        }

    }
}
