package com.enonic.wem.core.content;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.RenameContentParams;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NodeName;
import com.enonic.wem.api.entity.RenameNodeParams;


final class RenameContentCommand
    extends AbstractContentCommand<RenameContentCommand>
{
    private RenameContentParams params;

    Content execute()
    {
        params.validate();

        return doExecute();
    }

    private Content doExecute()
    {
        final EntityId entityId = EntityId.from( params.getContentId() );
        final NodeName nodeName = NodeName.from( params.getNewName().toString() );
        nodeService.rename( new RenameNodeParams().entityId( entityId ).nodeName( nodeName ),
                            new Context( ContentConstants.DEFAULT_WORKSPACE ) );

        return getContent( params.getContentId() );
    }

    RenameContentCommand params( final RenameContentParams params )
    {
        this.params = params;
        return this;
    }
}
