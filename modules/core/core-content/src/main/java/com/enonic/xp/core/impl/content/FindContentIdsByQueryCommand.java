package com.enonic.xp.core.impl.content;

import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.highlight.HighlightedProperties;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeHit;
import com.enonic.xp.node.NodeQuery;

final class FindContentIdsByQueryCommand
    extends AbstractContentCommand
{
    private final ContentQuery query;

    private FindContentIdsByQueryCommand( final Builder builder )
    {
        super( builder );
        this.query = builder.query;
    }

    public static Builder create()
    {
        return new Builder();
    }

    FindContentIdsByQueryResult execute()
    {
        final NodeQuery nodeQuery = ContentQueryNodeQueryTranslator.translate( this.query ).
            addQueryFilters( createFilters() ).
            build();

        final FindNodesByQueryResult result = nodeService.findByQuery( nodeQuery );

        final Map<ContentId, HighlightedProperties> highlight = result.getNodeHits().stream().
            filter( nodeHit -> nodeHit.getHighlight() != null && nodeHit.getHighlight().size() > 0 ).
            collect( Collectors.toMap( hit -> ContentId.from( hit.getNodeId().toString() ), NodeHit::getHighlight ) );

        return FindContentIdsByQueryResult.create().
            contents( ContentNodeHelper.toContentIds( result.getNodeIds() ) ).
            aggregations( result.getAggregations() ).
            highlight( highlight ).
            hits( result.getHits() ).
            totalHits( result.getTotalHits() ).

            build();
    }

    public static final class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentQuery query;

        private Builder()
        {
        }

        public Builder query( final ContentQuery query )
        {
            this.query = query;
            return this;
        }

        public FindContentIdsByQueryCommand build()
        {
            validate();
            return new FindContentIdsByQueryCommand( this );
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( query );
        }

    }
}
