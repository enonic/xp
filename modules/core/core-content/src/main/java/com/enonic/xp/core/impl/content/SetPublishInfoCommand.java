package com.enonic.xp.core.impl.content;

import java.time.Instant;

import com.enonic.xp.content.ContentIndexPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentPublishInfo;
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

    private SetPublishInfoCommand( final Builder builder )
    {
        super( builder );
        this.nodeIds = builder.nodeIds;
        this.contentPublishInfo = builder.contentPublishInfo == null ? ContentPublishInfo.create().build() : builder.contentPublishInfo;
    }

    public void execute()
    {
        final NodeIds firstTimePublished = findNodesWithoutPublishInfo( nodeIds );

        if ( firstTimePublished.getSize() == 0 )
        {
            return;
        }

        final Instant now = Instant.now();
        final Instant publishFrom = contentPublishInfo.getFrom() == null ? now : contentPublishInfo.getFrom();
        final Instant publishTo = contentPublishInfo.getTo();

        for ( final NodeId id : firstTimePublished )
        {
            this.nodeService.update( UpdateNodeParams.create().
                editor( toBeEdited -> {

                    toBeEdited.data.setInstant( ContentPropertyNames.MODIFIED_TIME, now );
                    toBeEdited.data.setString( ContentPropertyNames.MODIFIER, getCurrentUser().getKey().toString() );

                    PropertySet publishInfo = toBeEdited.data.getSet( ContentPropertyNames.PUBLISH_INFO );
                    if ( publishInfo == null )
                    {
                        publishInfo = toBeEdited.data.addSet( ContentPropertyNames.PUBLISH_INFO );
                    }
                    publishInfo.setInstant( ContentPropertyNames.PUBLISH_FROM, publishFrom );
                    if ( publishTo == null )
                    {
                        publishInfo.removeProperty( ContentPropertyNames.PUBLISH_TO );
                    }
                    else
                    {
                        publishInfo.setInstant( ContentPropertyNames.PUBLISH_TO, publishTo );
                    }
                } ).
                id( id ).
                build() );
        }

        this.nodeService.refresh( RefreshMode.ALL );
    }

    private User getCurrentUser()
    {
        final User user = ContextAccessor.current().getAuthInfo().getUser();
        return user != null ? user : User.ANONYMOUS;
    }

    private NodeIds findNodesWithoutPublishInfo( final NodeIds nodesToPush )
    {
        if ( nodesToPush.isEmpty() )
        {
            return NodeIds.empty();
        }

        final NodeQuery query = NodeQuery.create().
            addQueryFilter( BooleanFilter.create().
                mustNot( ExistsFilter.create().
                    fieldName( ContentIndexPath.PUBLISH_FROM.getPath() ).
                    build() ).
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

        public SetPublishInfoCommand build()
        {
            return new SetPublishInfoCommand( this );
        }

    }

}
