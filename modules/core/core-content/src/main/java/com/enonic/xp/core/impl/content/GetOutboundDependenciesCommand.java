package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentIndexPath;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.index.FieldValues;
import com.enonic.xp.query.filter.IdFilter;

import static java.util.Objects.requireNonNull;

/**
 * The contents a content points at. Every reference a content holds - in its data, its page, its x-data, the links pulled out of its
 * HTML areas, the content it is a variant of - is stored as a reference property, and the index collects all of them into
 * {@link ContentIndexPath#REFERENCES}. Reading that field back is the same answer inbound dependencies and publish dependency
 * resolution are built on, rather than a second derivation of it.
 */
final class GetOutboundDependenciesCommand
    extends AbstractContentCommand
{
    private final ContentId contentId;

    private GetOutboundDependenciesCommand( final Builder builder )
    {
        super( builder );
        this.contentId = builder.contentId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    ContentIds execute()
    {
        final FindContentIdsByQueryResult result = FindContentIdsByQueryCommand.create()
            .query( ContentQuery.create()
                        .queryFilter( IdFilter.create().fieldName( ContentIndexPath.ID.getPath() ).value( contentId.toString() ).build() )
                        .size( 1 )
                        .build() )
            .extraReturnFields( ContentIndexPath.REFERENCES )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build()
            .execute();

        if ( !result.getContentIds().contains( contentId ) )
        {
            // no hit is either a content that is not there or one this context cannot see; getContent answers for which
            getContent( contentId );
            return ContentIds.empty();
        }

        final FieldValues fields = result.getFields().get( contentId );
        if ( fields == null )
        {
            return ContentIds.empty();
        }

        return fields.getValues( ContentIndexPath.REFERENCES )
            .stream()
            .map( Object::toString )
            // a content referring to itself is not a dependency of itself
            .filter( reference -> !reference.isBlank() && !contentId.toString().equals( reference ) )
            .map( ContentId::from )
            .collect( ContentIds.collector() );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentId contentId;

        public Builder contentId( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            requireNonNull( contentId, "contentId is required" );
        }

        public GetOutboundDependenciesCommand build()
        {
            validate();
            return new GetOutboundDependenciesCommand( this );
        }
    }
}
