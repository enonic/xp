package com.enonic.xp.core.impl.content;

import java.time.Instant;

import com.enonic.xp.content.ContentIndexPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.PushContentListener;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.ExistsFilter;
import com.enonic.xp.query.filter.IdFilter;
import com.enonic.xp.security.User;

public class SetPublishInfoCommand
    extends AbstractContentCommand
{
    private final NodeIds nodeIds;

    private final ContentPublishInfo contentPublishInfo;

    private final PushContentListener pushContentListener;

    private SetPublishInfoCommand( final Builder builder )
    {
        super( builder );
        this.nodeIds = builder.nodeIds;
        this.contentPublishInfo = builder.contentPublishInfo == null ? ContentPublishInfo.create().build() : builder.contentPublishInfo;
        this.pushContentListener = builder.pushContentListener;
    }

    public void execute()
    {
        final NodeIds nodeIdsToUpdate = findNodesWithoutPublishFirstAndFrom( nodeIds );

        if ( nodeIdsToUpdate.getSize() == 0 )
        {
            return;
        }

        final Instant now = Instant.now();
        final Instant publishFrom = contentPublishInfo.getFrom() == null ? now : contentPublishInfo.getFrom();
        final Instant publishTo = contentPublishInfo.getTo();

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
            if ( pushContentListener != null )
            {
                pushContentListener.contentPushed( 1 );
            }
        }

        this.nodeService.refresh( RefreshMode.ALL );
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

    public static SetPublishInfoCommand.Builder create()
    {
        return new Builder();
    }

    public static SetPublishInfoCommand.Builder create( final AbstractContentCommand source )
    {
        return new Builder( source );
    }

    public final static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private NodeIds nodeIds;

        private ContentPublishInfo contentPublishInfo;

        private PushContentListener pushContentListener;

        public Builder()
        {
        }

        public Builder( final AbstractContentCommand source )
        {
            super( source );
        }

        public Builder nodeIds( final NodeIds nodeIds )
        {
            this.nodeIds = nodeIds;
            return this;
        }

        public Builder contentPublishInfo( final ContentPublishInfo contentPublishInfo )
        {
            this.contentPublishInfo = contentPublishInfo;
            return this;
        }

        public SetPublishInfoCommand.Builder pushListener( final PushContentListener pushContentListener )
        {
            this.pushContentListener = pushContentListener;
            return this;
        }

        public SetPublishInfoCommand build()
        {
            return new SetPublishInfoCommand( this );
        }

    }

}
