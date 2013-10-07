package com.enonic.wem.api.module;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;

import com.enonic.wem.api.resource.Resource;

@Immutable
public final class ModuleFileEntry
    implements Iterable<ModuleFileEntry>
{
    private final ImmutableMap<String, ModuleFileEntry> entries;

    private final boolean isDirectory;

    private final String name;

    private final Resource resource;

    private ModuleFileEntry( final Builder builder )
    {
        Preconditions.checkNotNull( builder.name );
        this.isDirectory = builder.isDirectory;
        this.name = builder.name;
        this.resource = builder.resource;
        if ( builder.isDirectory )
        {
            this.entries = Maps.uniqueIndex( builder.entryList, new Function<ModuleFileEntry, String>()
            {
                public String apply( final ModuleFileEntry input )
                {
                    return input.getName();
                }
            } );
        }
        else
        {
            this.entries = ImmutableMap.of();
        }
    }

    public boolean isDirectory()
    {
        return isDirectory;
    }

    public boolean isFile()
    {
        return !isDirectory;
    }

    public String getName()
    {
        return name;
    }

    public Resource getResource()
    {
        return resource;
    }

    public Resource getResource( final String pathToResource )
    {
        final ModuleFileEntry entry = getEntry( pathToResource );
        return entry == null ? null : entry.getResource();
    }

    public ModuleFileEntry getEntry( final String entryPath )
    {
        final Iterator<String> pathElements = Splitter.on( "/" ).omitEmptyStrings().split( entryPath ).iterator();
        if ( !pathElements.hasNext() )
        {
            return null;
        }

        ModuleFileEntry entry = this;
        while ( pathElements.hasNext() )
        {
            final String pathElement = pathElements.next();
            entry = entry.entries.get( pathElement );
            if ( entry == null )
            {
                return null;
            }
        }
        return entry;
    }

    public boolean contains( final String entryPath )
    {
        return getEntry( entryPath ) != null;
    }

    public ImmutableCollection<ModuleFileEntry> entries()
    {
        return entries.values();
    }

    public int size()
    {
        return entries.size();
    }

    public boolean isEmpty()
    {
        return entries.isEmpty();
    }

    @Override
    public Iterator<ModuleFileEntry> iterator()
    {
        return entries.values().iterator();
    }

    String asTreeString()
    {
        final StringBuilder result = new StringBuilder();
        asTree( "", result );
        return result.toString();
    }

    private void asTree( final String parentPath, final StringBuilder result )
    {
        final String entryPath = parentPath + "/" + this.name;
        result.append( entryPath );
        for ( ModuleFileEntry entry : this )
        {
            result.append( "\r\n" );
            entry.asTree( entryPath, result );
        }
    }

    public static ModuleFileEntry newFileEntry( final String fileName, final ByteSource source )
    {
        final Resource resource = new Resource( fileName, source );
        return new Builder( false, fileName ).resource( resource ).build();
    }

    public static ModuleFileEntry newFileEntry( final File file )
    {
        final Resource resource = new Resource( file.getName(), Files.asByteSource( file ) );
        return new Builder( false, resource.getName() ).resource( resource ).build();
    }

    public static Builder directoryBuilder( final String name )
    {
        return new Builder( true, name );
    }

    public static Builder copyOf( final ModuleFileEntry entry )
    {
        return new Builder( entry );
    }

    public static class Builder
    {
        private final List<ModuleFileEntry> entryList = Lists.newArrayList();

        private final List<Builder> builderEntryList = Lists.newArrayList();

        private boolean isDirectory;

        private String name;

        private Resource resource;

        private Builder( final boolean isDirectory, final String name )
        {
            this.isDirectory = isDirectory;
            this.name = name;
        }

        private Builder( final ModuleFileEntry entry )
        {
            this.isDirectory = entry.isDirectory();
            // TODO copy or make Resource immutable
            this.resource = entry.resource == null ? null : new Resource( entry.resource.getName(), entry.resource.getByteSource() );
            this.name = entry.getName();
            for ( ModuleFileEntry subEntry : entry )
            {
                this.builderEntryList.add( new Builder( subEntry ) );
            }
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        private Builder resource( final Resource resource )
        {
            this.resource = resource;
            return this;
        }

        public Builder add( final File file )
        {
            entryList.add( ModuleFileEntry.newFileEntry( file ) );
            return this;
        }

        public Builder add( final String fileName, final ByteSource source )
        {
            entryList.add( ModuleFileEntry.newFileEntry( fileName, source ) );
            return this;
        }

        public Builder add( final ModuleFileEntry entry )
        {
            entryList.add( entry );
            return this;
        }

        public Builder add( final Iterable<ModuleFileEntry> entries )
        {
            Iterables.addAll( entryList, entries );
            return this;
        }

        public Builder add( final Builder entryBuilder )
        {
            builderEntryList.add( entryBuilder );
            return this;
        }

        public Builder remove( final String entryName )
        {
            boolean removed = Iterables.removeIf( this.builderEntryList, new Predicate<Builder>()
            {
                @Override
                public boolean apply( Builder entry )
                {
                    return entryName.equals( entry.name );
                }
            } );
            if ( !removed )
            {
                Iterables.removeIf( this.entryList, new Predicate<ModuleFileEntry>()
                {
                    @Override
                    public boolean apply( ModuleFileEntry entry )
                    {
                        return entryName.equals( entry.name );
                    }
                } );
            }
            return this;
        }

        private Builder getChildEntry( final String name )
        {
            for ( Builder child : this.builderEntryList )
            {
                if ( name.equals( child.name ) )
                {
                    return child;
                }
            }
            return null;
        }

        public Builder getEntry( final String entryPath )
        {
            final Iterator<String> pathElements = Splitter.on( "/" ).omitEmptyStrings().split( entryPath ).iterator();
            if ( !pathElements.hasNext() )
            {
                return null;
            }

            Builder entry = this;
            while ( pathElements.hasNext() )
            {
                final String pathElement = pathElements.next();
                entry = entry.getChildEntry( pathElement );
                if ( entry == null )
                {
                    return null;
                }
            }
            return entry;
        }

        public ModuleFileEntry build()
        {
            for ( Builder entryBuilder : builderEntryList )
            {
                add( entryBuilder.build() );
            }
            return new ModuleFileEntry( this );
        }
    }

}
