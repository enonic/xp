package com.enonic.xp.core.impl.content;

import java.time.Instant;

import com.enonic.xp.content.ContentIndexPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.PushContentListener;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.ExistsFilter;
import com.enonic.xp.query.filter.IdFilter;

public class SetPublishInfoCommand
    extends AbstractContentCommand
{
    private final NodeIds nodeIds;

    private final Instant publishFrom;

    private final Instant publishTo;

    private final PushContentListener publishContentListener;

    private SetPublishInfoCommand( final Builder builder )
    {
        super( builder );
        this.nodeIds = builder.nodeIds;
        this.publishFrom = builder.publishFrom;
        this.publishTo = builder.publishTo;
        this.publishContentListener = builder.publishContentListener;
    }

    public void execute()
    {
        final NodeIds nodeIdsToUpdate = findNodesWithoutPublishFirstAndFrom( nodeIds );

        if ( nodeIdsToUpdate.isEmpty() )
        {
            return;
        }

        for ( final NodeId id : nodeIdsToUpdate )
        {
            this.nodeService.update( UpdateNodeParams.create().
                editor( toBeEdited -> {

                    PropertySet publishInfo = toBeEdited.data.getSet( ContentPropertyNames.PUBLISH_INFO );
                    if ( publishInfo == null )
                    {
                        publishInfo = toBeEdited.data.addSet( ContentPropertyNames.PUBLISH_INFO );
                    }

                    if ( publishInfo.getInstant( ContentPropertyNames.PUBLISH_FIRST ) == null )
                    {
                        final Instant publishFromPropertyValue = publishInfo.getInstant( ContentPropertyNames.PUBLISH_FROM );
                        if ( publishFromPropertyValue == null )
                        {
                            publishInfo.setInstant( ContentPropertyNames.PUBLISH_FIRST, publishFrom );
                        }
                        else
                        {
                            //TODO Special case for Enonic XP 6.7 and 6.8 contents. Remove after 7.0
                            publishInfo.setInstant( ContentPropertyNames.PUBLISH_FIRST, publishFromPropertyValue );
                        }
                    }

                    if ( publishInfo.getInstant( ContentPropertyNames.PUBLISH_FROM ) == null )
                    {
                        publishInfo.setInstant( ContentPropertyNames.PUBLISH_FROM, publishFrom );
                        if ( publishTo == null )
                        {
                            if ( publishInfo.hasProperty( ContentPropertyNames.PUBLISH_TO ) )
                            {
                                publishInfo.removeProperty( ContentPropertyNames.PUBLISH_TO );
                            }
                        }
                        else
                        {
                            publishInfo.setInstant( ContentPropertyNames.PUBLISH_TO, publishTo );
                        }
                    }

                } ).
                id( id ).
                build() );
            if ( publishContentListener != null )
            {
                publishContentListener.contentPushed( 1 );
            }
        }
    }

    private NodeIds findNodesWithoutPublishFirstAndFrom( final NodeIds nodesToPush )
    {
        if ( nodesToPush.isEmpty() )
        {
            return NodeIds.empty();
        }

        final BooleanFilter containPublishFirstAndFromFilter = BooleanFilter.create().
            must( ExistsFilter.create().fieldName( ContentIndexPath.PUBLISH_FIRST.getPath() ).build() ).
            must( ExistsFilter.create().fieldName( ContentIndexPath.PUBLISH_FROM.getPath() ).build() ).
            build();

        final NodeQuery query = NodeQuery.create().
            addQueryFilter( BooleanFilter.create().
                mustNot( containPublishFirstAndFromFilter ).
                must( IdFilter.create().
                    fieldName( ContentIndexPath.ID.getPath() ).
                    values( nodesToPush ).
                    build() ).
                build() ).
            size( NodeQuery.ALL_RESULTS_SIZE_FLAG ).
            build();

        final FindNodesByQueryResult result = this.nodeService.findByQuery( query );

        return result.getNodeIds();
    }

    public static SetPublishInfoCommand.Builder create( final AbstractContentCommand source )
    {
        return new Builder( source );
    }

    public static final class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private NodeIds nodeIds;

        private Instant publishFrom;

        private Instant publishTo;

        private PushContentListener publishContentListener;

        public Builder( final AbstractContentCommand source )
        {
            super( source );
        }

        public Builder nodeIds( final NodeIds nodeIds )
        {
            this.nodeIds = nodeIds;
            return this;
        }

        public Builder publishFrom( final Instant publishFrom )
        {
            this.publishFrom = publishFrom;
            return this;
        }

        public Builder publishTo( final Instant publishTo )
        {
            this.publishTo = publishTo;
            return this;
        }

        public SetPublishInfoCommand.Builder pushListener( final PushContentListener publishContentListener )
        {
            this.publishContentListener = publishContentListener;
            return this;
        }

        public SetPublishInfoCommand build()
        {
            return new SetPublishInfoCommand( this );
        }

    }

}
