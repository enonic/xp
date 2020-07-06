package com.enonic.xp.core.impl.content;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.highlight.HighlightedProperties;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.sortvalues.SortValuesProperty;

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

        final Map<ContentId, HighlightedProperties> highlight = new LinkedHashMap<>();

        final Map<ContentId, SortValuesProperty> sortValues = new LinkedHashMap<>();

        final FindNodesByQueryResult result = nodeService.findByQuery( nodeQuery );

        result.getNodeHits().forEach( nodeHit -> {
            if ( nodeHit.getHighlight() != null && !nodeHit.getHighlight().isEmpty() )
            {
                highlight.put( ContentId.from( nodeHit.getNodeId().toString() ), nodeHit.getHighlight() );
            }

            if ( nodeHit.getSort() != null && nodeHit.getSort().getValues() != null && !nodeHit.getSort().getValues().isEmpty() )
            {
                sortValues.put( ContentId.from( nodeHit.getNodeId().toString() ), nodeHit.getSort() );
            }
        } );

        return FindContentIdsByQueryResult.create().
            contents( ContentNodeHelper.toContentIds( result.getNodeIds() ) ).
            aggregations( result.getAggregations() ).
            highlight( highlight ).
            sort( sortValues ).
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
