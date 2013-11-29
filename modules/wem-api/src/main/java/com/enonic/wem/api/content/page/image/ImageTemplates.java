package com.enonic.wem.api.content.page.image;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.api.support.AbstractImmutableEntityList;

public final class ImageTemplates
    extends AbstractImmutableEntityList<ImageTemplate>
{
    private final ImmutableMap<ImageTemplateName, ImageTemplate> templatesByName;

    private final ImmutableMap<ResourcePath, ImageTemplate> templatesByPath;

    private ImageTemplates( final ImmutableList<ImageTemplate> list )
    {
        super( list );
        this.templatesByName = Maps.uniqueIndex( list, new ToNameFunction() );
        this.templatesByPath = Maps.uniqueIndex( list, new ToPathFunction() );
    }

    public ImageTemplate getTemplate( final ImageTemplateName name )
    {
        return this.templatesByName.get( name );
    }

    public ImageTemplate getTemplate( final ResourcePath path )
    {
        return this.templatesByPath.get( path );
    }

    public static ImageTemplates empty()
    {
        final ImmutableList<ImageTemplate> list = ImmutableList.of();
        return new ImageTemplates( list );
    }

    public static ImageTemplates from( final ImageTemplate... templates )
    {
        return new ImageTemplates( ImmutableList.copyOf( templates ) );
    }

    public static ImageTemplates from( final Iterable<? extends ImageTemplate> templates )
    {
        return new ImageTemplates( ImmutableList.copyOf( templates ) );
    }

    public static ImageTemplates from( final Collection<? extends ImageTemplate> templates )
    {
        return new ImageTemplates( ImmutableList.copyOf( templates ) );
    }

    private final static class ToNameFunction
        implements Function<ImageTemplate, ImageTemplateName>
    {
        @Override
        public ImageTemplateName apply( final ImageTemplate value )
        {
            return value.getName();
        }
    }

    private final static class ToPathFunction
        implements Function<ImageTemplate, ResourcePath>
    {
        @Override
        public ResourcePath apply( final ImageTemplate value )
        {
            return value.getPath();
        }
    }

    public static Builder newImageTemplates()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ImmutableList.Builder<ImageTemplate> list = new ImmutableList.Builder<>();

        public Builder add( ImageTemplate template )
        {
            this.list.add( template );
            return this;
        }

        public ImageTemplates build()
        {
            return new ImageTemplates( this.list.build() );
        }
    }
}
