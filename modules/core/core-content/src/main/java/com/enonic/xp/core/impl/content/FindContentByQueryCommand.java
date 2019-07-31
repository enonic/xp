package com.enonic.xp.core.impl.content;

import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.FindContentByQueryParams;
import com.enonic.xp.content.FindContentByQueryResult;
import com.enonic.xp.highlight.HighlightedFields;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeHit;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.Nodes;

final class FindContentByQueryCommand
    extends AbstractContentCommand
{
    private final FindContentByQueryParams params;

    private FindContentByQueryCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    FindContentByQueryResult execute()
    {
        final NodeQuery nodeQuery = ContentQueryNodeQueryTranslator.translate( this.params.getContentQuery() ).
            addQueryFilters( createFilters() ).
            build();

        final FindNodesByQueryResult result = nodeService.findByQuery( nodeQuery );

        final NodeIds nodeIds = result.getNodeIds();

        final Map<ContentId, HighlightedFields> highlight = result.getNodeHits().stream().
            filter( nodeHit -> nodeHit.getHighlight() != null && nodeHit.getHighlight().size() > 0 ).
            collect( Collectors.toMap( hit -> ContentId.from( hit.getNodeId().toString() ), NodeHit::getHighlight ) );

        final Nodes foundNodes = this.nodeService.getByIds( nodeIds );

        Contents contents = this.translator.fromNodes( foundNodes, true );

        return FindContentByQueryResult.create().
            contents( contents ).
            aggregations( result.getAggregations() ).
            hits( result.getHits() ).
            totalHits( result.getTotalHits() ).
            highlight( highlight ).
            build();
    }

    public static final class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private FindContentByQueryParams params;

        private Builder()
        {
        }

        public Builder params( final FindContentByQueryParams params )
        {
            this.params = params;
            return this;
        }

        public FindContentByQueryCommand build()
        {
            validate();
            return new FindContentByQueryCommand( this );
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( params );
            Preconditions.checkNotNull( params.getContentQuery() );
        }

    }
}
