package com.enonic.wem.export.internal.writer;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class ExportItemPath
{
    private final ImmutableList<String> elements;

    private static final String ELEMENT_DIVIDER = "/";

    private ExportItemPath( final Builder builder )
    {
        this.elements = ImmutableList.copyOf( builder.elements );
    }

    public static ExportItemPath from( final ExportItemPath exportItemPath, final String element )
    {
        return create( exportItemPath ).add( element ).build();
    }

    public static ExportItemPath from( final ExportItemPath exportItemPath, final String... elements )
    {
        return create( exportItemPath ).addAll( elements ).build();
    }

    public static ExportItemPath from( final String path )
    {
        if ( Strings.isNullOrEmpty( path ) )
        {
            return ExportItemPath.create().build();
        }

        final String[] elements = path.split( "/" );

        final Builder builder = ExportItemPath.create();

        for ( final String element : elements )
        {
            builder.add( element );
        }

        return builder.build();
    }

    public ImmutableList<String> getElements()
    {
        return elements;
    }

    public Path getPath()
    {
        return Paths.get( this.getPathAsString() );
    }

    public String getPathAsString()
    {
        return Joiner.on( "/" ).join( elements );
    }

    public ExportItemPath removeLastElement()
    {
        final Builder builder = ExportItemPath.create();

        for ( int i = 0; i < this.elements.size() - 1; i++ )
        {
            builder.add( this.elements.get( i ) );
        }

        return builder.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    private static Builder create( final ExportItemPath source )
    {
        return new Builder( source );
    }

    public static class Builder
    {
        private final List<String> elements = Lists.newLinkedList();

        public Builder()
        {
        }

        public Builder( final ExportItemPath source )
        {
            this.addAll( source.elements );
        }

        public Builder add( final String pathElement )
        {
            this.elements.add( pathElement );
            return this;
        }

        public Builder addAll( final String... pathElements )
        {
            this.elements.addAll( Arrays.asList( pathElements ) );
            return this;
        }

        public Builder addAll( final Collection<String> pathElements )
        {
            this.elements.addAll( pathElements );
            return this;
        }

        public ExportItemPath build()
        {
            return new ExportItemPath( this );
        }
    }
}
