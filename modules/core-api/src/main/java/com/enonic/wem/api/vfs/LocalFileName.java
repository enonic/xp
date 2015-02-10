package com.enonic.wem.api.vfs;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class LocalFileName
    extends AbstractFileName
{
    private final static String SEPARATOR = FileSystems.getDefault().getSeparator();

    public LocalFileName( final String[] elements, final boolean absolute )
    {
        super( elements, absolute );
    }

    public static LocalFileName from( final String path )
    {
        return new LocalFileName( getPathElements( path ), path.startsWith( SEPARATOR ) );
    }

    public static LocalFileName from( final Path path )
    {
        return new LocalFileName( getPathElements( path.toString() ), path.isAbsolute() );
    }

    public Path getPath()
    {
        return absolute ? doBuildPath().toAbsolutePath() : doBuildPath();
    }

    private static String[] getPathElements( final String path )
    {
        final List<String> elements = Lists.newLinkedList();

        final String[] elementArray = path.split( Pattern.quote( SEPARATOR ) );

        for ( final String element : elementArray )
        {
            if ( !Strings.isNullOrEmpty( element ) )
            {
                elements.add( element );
            }
        }

        return elements.toArray( new String[elements.size()] );
    }

    @Override
    public String getLocalPath()
    {
        final Path path = doBuildPath();

        return absolute ? SEPARATOR + path.toString() : path.toString();
    }

    private Path doBuildPath()
    {
        final Path path;

        if ( size() > 1 )
        {
            final String rootElement = elements[0];

            String[] other = Arrays.copyOfRange( elements, 1, size() );

            path = Paths.get( rootElement, other );
        }
        else
        {
            path = Paths.get( elements[0] );
        }
        return path;
    }
}
