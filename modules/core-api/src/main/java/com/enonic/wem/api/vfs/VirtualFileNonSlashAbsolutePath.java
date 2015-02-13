package com.enonic.wem.api.vfs;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

import com.google.common.collect.Lists;

public class VirtualFileNonSlashAbsolutePath
    extends VirtualFilePathImpl
{
    public VirtualFileNonSlashAbsolutePath( final Path path )
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

    public String getPath()
    {
        return join();
    }

    @Override
    public Path toLocalPath()
    {
        Path path = Paths.get( "" );

        for ( final String element : this.elements )
        {
            path = Paths.get( path.toString(), element );
        }

        return path;
    }

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

        public VirtualFileNonSlashAbsolutePath build()
        {
            return new VirtualFileNonSlashAbsolutePath( this );
        }

    }

}
