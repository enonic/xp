package com.enonic.xp.core.impl.content;

import java.time.Instant;

import com.enonic.xp.content.ContentIndexPath;
import com.enonic.xp.content.ContentPropertyNames;
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
import com.enonic.xp.query.filter.ValueFilter;

public class SetFirstTimePublishedCommand
    extends AbstractContentCommand
{
    private final NodeIds nodeIds;

    private SetFirstTimePublishedCommand( final Builder builder )
    {
        super( builder );
        this.nodeIds = builder.nodeIds;
    }

    public void execute()
    {
        final NodeIds firstTimePublished = findFirstTimePublished( nodeIds );

        if ( firstTimePublished.getSize() == 0 )
        {
            return;
        }

        final Instant now = Instant.now();

        for ( final NodeId id : firstTimePublished )
        {
            this.nodeService.update( UpdateNodeParams.create().
                editor( toBeEdited -> {

                    toBeEdited.data.setInstant( ContentPropertyNames.MODIFIED_TIME, now );
                    toBeEdited.data.setString( ContentPropertyNames.MODIFIER, ContextAccessor.current().
                        getAuthInfo().getUser().getKey().toString() );

                    PropertySet publishInfo = toBeEdited.data.getSet( ContentPropertyNames.PUBLISH_INFO );
                    if ( publishInfo == null )
                    {
                        publishInfo = toBeEdited.data.addSet( ContentPropertyNames.PUBLISH_INFO );
                    }
                    publishInfo.setInstant( ContentPropertyNames.PUBLISH_FROM, now );
                } ).
                id( id ).
                build() );
        }

        this.nodeService.refresh( RefreshMode.ALL );
    }

    private NodeIds findFirstTimePublished( final NodeIds nodesToPush )
    {
        final NodeQuery query = NodeQuery.create().
            addQueryFilter( BooleanFilter.create().
                mustNot( ExistsFilter.create().
                    fieldName( ContentIndexPath.PUBLISHED_TIME.getPath() ).
                    build() ).
                must( ValueFilter.create().
                    fieldName( ContentPropertyNames.ID ).
                    addValues( nodesToPush.getAsStrings() ).
                    build() ).
                build() ).
            size( NodeQuery.ALL_RESULTS_SIZE_FLAG ).
            build();

        final FindNodesByQueryResult result = this.nodeService.findByQuery( query );

        return result.getNodeIds();
    }

    public static SetFirstTimePublishedCommand.Builder create()
    {
        return new Builder();
    }

    public static SetFirstTimePublishedCommand.Builder create( final AbstractContentCommand source )
    {
        return new Builder( source );
    }

    public final static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private NodeIds nodeIds;

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

        public SetFirstTimePublishedCommand build()
        {
            return new SetFirstTimePublishedCommand( this );
        }

    }

}
