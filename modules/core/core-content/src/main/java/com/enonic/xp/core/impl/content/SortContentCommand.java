package com.enonic.xp.core.impl.content;

import java.util.Objects;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ReorderChildContentParams;
import com.enonic.xp.content.SortContentParams;
import com.enonic.xp.content.SortContentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.ReorderChildNodeParams;
import com.enonic.xp.node.SortNodeParams;
import com.enonic.xp.node.SortNodeResult;

class SortContentCommand
    extends AbstractCreatingOrUpdatingContentCommand
{
    private final SortContentParams params;

    private SortContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create( final SortContentParams params )
    {
        return new Builder( params );
    }

    SortContentResult execute()
    {
        try
        {
            final SortNodeParams.Builder paramsBuilder = SortNodeParams.create()
                .nodeId( NodeId.from( params.getContentId() ) )
                .refresh( RefreshMode.ALL )
                .childOrder( params.getChildOrder() )
                .manualOrderSeed( params.getManualOrderSeed() );

            for ( final ReorderChildContentParams param : params.getReorderChildContents() )
            {
                paramsBuilder.addManualOrder( ReorderChildNodeParams.create()
                                                  .nodeId( NodeId.from( param.getContentToMove() ) )
                                                  .moveBefore( param.getContentToMoveBefore() == null
                                                                   ? null
                                                                   : NodeId.from( param.getContentToMoveBefore() ) )
                                                  .build() );
            }

            if ( !layersSync )
            {
                paramsBuilder.processor( InheritedContentDataProcessor.SORT );
            }

            paramsBuilder.versionAttributes( layersSync
                                                 ? ContentAttributesHelper.layersSyncAttr()
                                                 : ContentAttributesHelper.versionHistoryAttr( ContentAttributesHelper.SORT_ATTR ) );

            final SortNodeResult sortNodeResult = nodeService.sort( paramsBuilder.build() );

            final Content content = ContentNodeTranslator.fromNode( sortNodeResult.getNode() );

            return SortContentResult.create()
                .content( content )
                .movedChildren(
                    sortNodeResult.getReorderedNodes().stream().map( Node::id ).map( ContentId::from ).collect( ContentIds.collector() ) )
                .build();
        }
        catch ( NodeAccessException e )
        {
            throw ContentNodeHelper.toContentAccessException( e );
        }
    }

    public static class Builder
        extends AbstractCreatingOrUpdatingContentCommand.Builder<SortContentCommand.Builder>
    {
        private final SortContentParams params;

        private Builder( final SortContentParams params )
        {
            this.params = Objects.requireNonNull( params, "params cannot be null" );
        }

        @Override
        void validate()
        {
            super.validate();
        }

        SortContentCommand build()
        {
            validate();
            return new SortContentCommand( this );
        }
    }
}
