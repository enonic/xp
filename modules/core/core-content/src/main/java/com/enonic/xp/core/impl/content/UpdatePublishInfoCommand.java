package com.enonic.xp.core.impl.content;

import java.time.Instant;

import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;

public class UpdatePublishInfoCommand
    extends AbstractContentCommand
{
    private final NodeIds nodeIds;

    private final ContentPublishInfo contentPublishInfo;

    private UpdatePublishInfoCommand( final Builder builder )
    {
        super( builder );
        this.nodeIds = builder.nodeIds;
        this.contentPublishInfo = builder.contentPublishInfo;
    }

    public void execute()
    {
        final Instant now = Instant.now();
        final Instant from = contentPublishInfo.getFrom() == null ? now : contentPublishInfo.getFrom();
        final Instant to = contentPublishInfo.getTo();

        for ( final NodeId id : nodeIds )
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
                    publishInfo.setInstant( ContentPropertyNames.PUBLISH_FROM, from );
                    if ( to != null )
                    {
                        publishInfo.setInstant( ContentPropertyNames.PUBLISH_TO, to );
                    }
                } ).
                id( id ).
                build() );
        }

        this.nodeService.refresh( RefreshMode.ALL );
    }

    public static UpdatePublishInfoCommand.Builder create()
    {
        return new Builder();
    }

    public static UpdatePublishInfoCommand.Builder create( final AbstractContentCommand source )
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

        public UpdatePublishInfoCommand build()
        {
            return new UpdatePublishInfoCommand( this );
        }

    }

}
