package com.enonic.xp.project.layer;

import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class ContentLayers
    extends AbstractImmutableEntitySet<ContentLayer>
{
    private ContentLayers( final Builder builder )
    {
        super( builder.layers.build() );
    }

    public static ContentLayers from( final Collection<ContentLayer> contentLayers )
    {
        return ContentLayers.create().addAll( contentLayers ).build();
    }

    public static ContentLayers empty()
    {
        return ContentLayers.create().build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentLayerKeys getKeys()
    {
        return ContentLayerKeys.from( stream().
            map( ContentLayer::getKey ).
            collect( Collectors.toList() ) );
    }

    public ContentLayer getLayer( final ContentLayerKey contentLayerKey )
    {
        return stream().
            filter( contentLayer -> contentLayer.getKey().equals( contentLayerKey ) ).
            findFirst().
            orElse( null );
    }

    public static class Builder
    {
        private final ImmutableSet.Builder<ContentLayer> layers = ImmutableSet.builder();

        public Builder add( final ContentLayer contentLayer )
        {
            this.layers.add( contentLayer );
            return this;
        }

        public Builder addAll( final Collection<ContentLayer> contentLayers )
        {
            this.layers.addAll( contentLayers );
            return this;
        }

        public ContentLayers build()
        {
            return new ContentLayers( this );
        }
    }

}
