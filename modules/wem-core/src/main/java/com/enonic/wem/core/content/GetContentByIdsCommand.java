package com.enonic.wem.core.content;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.GetContentByIdsParams;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.NoEntityWithIdFoundException;
import com.enonic.wem.api.entity.Nodes;


final class GetContentByIdsCommand
    extends AbstractContentCommand<GetContentByIdsCommand>
{
    private GetContentByIdsParams params;

    Contents execute()
    {
        final Contents contents;

        try
        {
            contents = doExecute();
        }
        catch ( NoEntityWithIdFoundException ex )
        {
            final ContentId contentId = ContentId.from( ex.getId().toString() );
            throw new ContentNotFoundException( contentId );
        }

        return this.params.doGetChildrenIds()
            ? new ChildContentIdsResolver( this.nodeService, this.contentTypeService, this.blobService ).resolve( contents )
            : contents;
    }

    private Contents doExecute()
    {
        final EntityIds entityIds = getAsEntityIds( this.params.getIds() );
        final Nodes nodes = nodeService.getByIds( entityIds );

        return getTranslator().fromNodes( nodes );
    }

    private EntityIds getAsEntityIds( final ContentIds contentIds )
    {
        final Set<EntityId> entityIds = Sets.newHashSet();

        final Iterator<ContentId> iterator = contentIds.iterator();

        while ( iterator.hasNext() )
        {
            entityIds.add( EntityId.from( iterator.next().toString() ) );
        }

        return EntityIds.from( entityIds );
    }

    GetContentByIdsCommand params( final GetContentByIdsParams params )
    {
        this.params = params;
        return this;
    }
}
