package com.enonic.xp.vfs;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

import static com.google.common.base.Strings.isNullOrEmpty;

class VirtualFilePathImpl
    implements VirtualFilePath
{
    static final String SEPARATOR = "/";

    final boolean absolute;

    final List<String> elements;

    private VirtualFilePathImpl( final Builder builder )
    {
        this.absolute = builder.absolute;
        this.elements = builder.elements;
    }

    VirtualFilePathImpl( final String path, final String separator )
    {
        this.absolute = path.startsWith( separator );
        this.elements = resolvePathElements( path, separator );
    }

    VirtualFilePathImpl( final Path path )
    {
        this.absolute = path.isAbsolute();
        this.elements = resolvePathElements( path.toString(), path.getFileSystem().getSeparator() );
    }

    VirtualFilePathImpl( final List<String> elements, final boolean absolute )
    {
        this.absolute = absolute;
        this.elements = elements;
    }

    static List<String> resolvePathElements( final String path, final String separator )
    {
        final LinkedList<String> elements = new LinkedList<>();

        final String[] elementArray = path.split( Pattern.quote( separator ) );

        for ( final String element : elementArray )
        {
            if ( !isNullOrEmpty( element ) )
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

    @Override
    public VirtualFilePath subtractPath( final VirtualFilePath subtract )
    {
        Preconditions.checkArgument( this.elements.size() >= subtract.size(),
                                     "No point in trying to remove [" + subtract.getPath() + "] from [" + this.getPath() + "]" );

        for ( int i = 0; i < subtract.size(); i++ )
        {
            if ( !this.elements.get( i ).equals( subtract.getElements().get( i ) ) )
            {
                throw new IllegalArgumentException( subtract.getElements().get( i ) + " is not present in the path to substract from" );
            }
        }

        final Builder builder = VirtualFilePathImpl.create().
            absolute( false );

        for ( int i = subtract.size(); i < this.elements.size(); i++ )
        {
            builder.add( this.elements.get( i ) );
        }

        return builder.build();
    }

    @Override
    public String getPath()
    {
        return absolute ? SEPARATOR + join() : join();
    }

    String join()
    {
        return String.join( SEPARATOR, elements );
    }

    @Override
    public List<String> getElements()
    {
        return elements;
    }

    @Override
    public String getName()
    {
        return this.elements.getLast();
    }

    @Override
    public VirtualFilePath join( final String... elements )
    {
        final Builder builder = VirtualFilePathImpl.create().
            addAll( this.elements ).
            absolute( this.absolute );

        for ( final String element : elements )
        {
            builder.addAll( resolvePathElements( element, SEPARATOR ) );
        }

        return builder.build();
    }

    @Override
    public int size()
    {
        return this.elements.size();
    }

    @Override
    public Path toLocalPath()
    {
        Path path = this.absolute ? Path.of( "/" ) : Path.of( "" );

        for ( final String element : this.elements )
        {
            path = Path.of( path.toString(), element );
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
        private final LinkedList<String> elements = new LinkedList<>();

        private boolean absolute;

        public Builder add( final String element )
        {
            this.elements.add( element );
            return this;
        }

        public Builder addAll( final List<String> elements )
        {
            this.elements.addAll( elements );
            return this;
        }

        public Builder absolute( final boolean absolute )
        {
            this.absolute = absolute;
            return this;
        }

        public VirtualFilePathImpl build()
        {
            return new VirtualFilePathImpl( this );
        }
    }
}
