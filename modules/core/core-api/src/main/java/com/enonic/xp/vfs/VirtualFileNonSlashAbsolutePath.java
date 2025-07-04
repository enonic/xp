package com.enonic.xp.vfs;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

final class VirtualFileNonSlashAbsolutePath
    extends VirtualFilePathImpl
{
    VirtualFileNonSlashAbsolutePath( final Path path )
    {
        super( path );
    }

    private VirtualFileNonSlashAbsolutePath( final Builder builder )
    {
        super( builder.elements, builder.absolute );
    }

    private static Builder create()
    {
        return new Builder();
    }

    @Override
    public String getPath()
    {
        return join();
    }

    @Override
    public Path toLocalPath()
    {
        Path path = Path.of( "" );

        for ( final String element : this.elements )
        {
            path = Path.of( path.toString(), element );
        }

        return path;
    }

    @Override
    public VirtualFileNonSlashAbsolutePath join( final String... elements )
    {
        final Builder builder = VirtualFileNonSlashAbsolutePath.create().
            addAll( this.elements ).
            absolute( this.absolute );

        for ( final String element : elements )
        {
            builder.addAll( resolvePathElements( element, SEPARATOR ) );
        }

        return builder.build();
    }

    private static final class Builder
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

        public VirtualFileNonSlashAbsolutePath build()
        {
            return new VirtualFileNonSlashAbsolutePath( this );
        }

    }

}
