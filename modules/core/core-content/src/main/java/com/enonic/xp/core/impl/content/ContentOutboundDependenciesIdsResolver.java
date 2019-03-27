package com.enonic.xp.core.impl.content;

import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.core.impl.content.serializer.ContentDataSerializer;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypes;

public class ContentOutboundDependenciesIdsResolver
{
    private final ContentService contentService;

    private final ContentDataSerializer contentDataSerializer;

    public ContentOutboundDependenciesIdsResolver( final ContentService contentService, final ContentDataSerializer contentDataSerializer )
    {
        this.contentService = contentService;
        this.contentDataSerializer = contentDataSerializer;
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

        final Stream<Property> extraDataDependencies = content.hasExtraData() ? content.getAllExtraData().
            stream().
            flatMap( extraData -> extraData.getData().
                getProperties( ValueTypes.REFERENCE ).
                stream() ) : Stream.empty();

        Stream.of( content.getData().getProperties( ValueTypes.REFERENCE ).stream(),
                   contentPageData.getProperties( ValueTypes.REFERENCE ).stream(), extraDataDependencies ).
            flatMap( s -> s ).
            forEach( property -> {
                final String value = property.getValue().toString();

                if ( !contentId.toString().equals( value ) && StringUtils.isNotBlank( value ) )
                {
                    contentIds.add( ContentId.from( value ) );
                }
            } );

        if ( content.getProcessedReferences() != null && content.getProcessedReferences().getSize() > 0 )
        {
            contentIds.addAll( content.getProcessedReferences() );
        }

        return contentIds.build();
    }

}
