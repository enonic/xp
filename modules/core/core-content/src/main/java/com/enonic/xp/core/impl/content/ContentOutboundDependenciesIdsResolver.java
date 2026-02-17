package com.enonic.xp.core.impl.content;

import java.util.stream.Stream;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.core.impl.content.serializer.ContentDataSerializer;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypes;

import static com.google.common.base.Strings.nullToEmpty;

class ContentOutboundDependenciesIdsResolver
{
    private final ContentService contentService;

    private final ContentDataSerializer contentDataSerializer = new ContentDataSerializer();

    ContentOutboundDependenciesIdsResolver( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    public ContentIds resolve( final ContentId contentId )
    {
        return resolveOutboundDependenciesIds( contentId );
    }

    private ContentIds resolveOutboundDependenciesIds( final ContentId contentId )
    {
        final Content content = this.contentService.getById( contentId );

        final ContentIds.Builder contentIds = ContentIds.create();

        final PropertySet contentPageData = new PropertyTree().getRoot();
        if ( content.getPage() != null )
        {
            contentDataSerializer.toPageData( content.getPage(), contentPageData );
        }

        final Stream<Property> mixinDependencies = !content.getMixins().isEmpty() ? content.getMixins()
            .stream()
            .flatMap( mixin -> mixin.getData().getProperties( ValueTypes.REFERENCE ).stream() ) : Stream.empty();

        Stream.of( content.getData().getProperties( ValueTypes.REFERENCE ).stream(),
                   contentPageData.getProperties( ValueTypes.REFERENCE ).stream(), mixinDependencies ).
            flatMap( s -> s ).
            forEach( property -> {
                final String value = property.getValue().toString();

                if ( !contentId.toString().equals( value ) && !nullToEmpty( value ).isBlank() )
                {
                    contentIds.add( ContentId.from( value ) );
                }
            } );

        if ( content.getProcessedReferences() != null && !content.getProcessedReferences().isEmpty() )
        {
            contentIds.addAll( content.getProcessedReferences() );
        }
        if ( content.getVariantOf() != null )
        {
            contentIds.add( content.getVariantOf() );
        }

        return contentIds.build();
    }

}
