package com.enonic.wem.api.vfs;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class VirtualFilePath
{
    protected final static String SEPARATOR = "/";

    protected final boolean absolute;

    protected final LinkedList<String> elements;

    private VirtualFilePath( final Builder builder )
    {
        this.absolute = builder.absolute;
        this.elements = builder.elements;
    }

    protected VirtualFilePath( final LinkedList<String> elements, final boolean absolute )
    {
        this.absolute = absolute;
        this.elements = elements;
    }

    public static VirtualFilePath from( final String path )
    {
        return new VirtualFilePath( resolvePathElements( path, SEPARATOR ), path.startsWith( SEPARATOR ) );
    }

    public static VirtualFilePath from( final Path path )
    {
        final String separator = path.getFileSystem().getSeparator();

        if ( !path.isAbsolute() && path.startsWith( separator ) )
        {
            return new DriveLetterFilePath( resolvePathElements( path.toString(), separator ) );
        }
        else
        {
            return new VirtualFilePath( resolvePathElements( path.toString(), separator ), path.isAbsolute() );
        }
    }

    private static LinkedList<String> resolvePathElements( final String path, final String separator )
    {
        final LinkedList<String> elements = Lists.newLinkedList();

        final String[] elementArray = path.split( Pattern.quote( separator ) );

        for ( final String element : elementArray )
        {
            if ( !Strings.isNullOrEmpty( element ) )
            {
                elements.add( element );
            }
        }

        return elements;
    }

    private static Builder create()
    {
        return new Builder();
    }

    public VirtualFilePath subtractPath( final VirtualFilePath subtract )
    {
        Preconditions.checkArgument( this.elements.size() >= subtract.size(),
                                     "No point in trying to remove [" + subtract.getPath() + "] from [" + this.getPath() + "]" );

        for ( int i = 0; i < subtract.size(); i++ )
        {
            if ( !this.elements.get( i ).equals( subtract.elements.get( i ) ) )
            {
                throw new IllegalArgumentException( subtract.elements.get( i ) + " is not present in the path to substract from" );
            }
        }

        final Builder builder = VirtualFilePath.create().
            absolute( false );

        for ( int i = subtract.size(); i < this.elements.size(); i++ )
        {
            builder.add( this.elements.get( i ) );
        }

        return builder.build();
    }

    public String getPath()
    {
        return absolute ? SEPARATOR + join() : join();
    }

    protected String join()
    {
        return Joiner.on( SEPARATOR ).join( elements );
    }

    public LinkedList<String> getElements()
    {
        return elements;
    }

    public String getName()
    {
        return this.elements.getLast();
    }

    public VirtualFilePath join( final VirtualFilePath... paths )
    {
        final Builder builder = VirtualFilePath.create().
            addAll( this.elements ).
            absolute( this.absolute );

        for ( final VirtualFilePath virtualFilePath : paths )
        {
            builder.addAll( virtualFilePath.getElements() );
        }

        return builder.build();
    }

    public VirtualFilePath join( final String... elements )
    {
        final Builder builder = VirtualFilePath.create().
            addAll( this.elements ).
            absolute( this.absolute );

        for ( final String element : elements )
        {
            builder.addAll( resolvePathElements( element, SEPARATOR ) );
        }

        return builder.build();
    }

    int size()
    {
        return this.elements.size();
    }

    public Path toLocalPath()
    {
        Path path = this.absolute ? Paths.get( "/" ) : Paths.get( "" );

        for ( final String element : this.elements )
        {
            path = Paths.get( path.toString(), element );
        }

        return path;
    }

    @Override
    public String toString()
    {
        return this.getPath();
    }

    private static class Builder
    {
        private final LinkedList<String> elements = Lists.newLinkedList();

        private boolean absolute;

        public Builder add( final String element )
        {
            this.elements.add( element );
            return this;
        }

        public Builder addAll( final LinkedList<String> elements )
        {
            this.elements.addAll( elements );
            return this;
        }

        public Builder absolute( final boolean absolute )
        {
            this.absolute = absolute;
            return this;
        }

        public VirtualFilePath build()
        {
            return new VirtualFilePath( this );
        }

    }
}
