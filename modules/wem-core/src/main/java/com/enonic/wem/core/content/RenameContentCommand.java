package com.enonic.wem.core.content;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentAlreadyExistException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.RenameContentParams;
import com.enonic.wem.core.entity.EntityId;
import com.enonic.wem.core.entity.NodeAlreadyExistException;
import com.enonic.wem.core.entity.NodeName;
import com.enonic.wem.core.entity.RenameNodeParams;

import static com.enonic.wem.core.content.ContentNodeHelper.translateNodePathToContentPath;


final class RenameContentCommand
    extends AbstractContentCommand
{
    private final RenameContentParams params;

    private RenameContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    Content execute()
    {
        params.validate();

        try
        {
            return doExecute();
        }
        catch ( final NodeAlreadyExistException e )
        {
            final ContentPath path = translateNodePathToContentPath( e.getNode() );
            throw new ContentAlreadyExistException( path );
        }
    }

    private Content doExecute()
    {
        final EntityId entityId = EntityId.from( params.getContentId() );
        final NodeName nodeName = NodeName.from( params.getNewName().toString() );
        nodeService.rename( RenameNodeParams.create().
            entityId( entityId ).
            nodeName( nodeName ).
            build() );

        return getContent( params.getContentId() );
    }

    public static Builder create( final RenameContentParams params )
    {
        return new Builder( params );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private final RenameContentParams params;

        public Builder( final RenameContentParams params )
        {
            this.params = params;
        }

        void validate()
        {
            super.validate();
        }

        public RenameContentCommand build()
        {
            validate();
            return new RenameContentCommand( this );
        }

    }


}

