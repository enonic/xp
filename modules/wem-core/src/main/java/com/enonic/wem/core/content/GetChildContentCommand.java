package com.enonic.wem.core.content;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;


final class GetChildContentCommand
    extends AbstractContentCommand<GetChildContentCommand>
{
    private ContentPath parentPath;

    Contents execute()
    {
        return new GetChildContentService(
            this.parentPath, this.nodeService, this.contentTypeService, this.blobService ).populateChildIds( true ).execute();
    }

    GetChildContentCommand parentPath( final ContentPath parentPath )
    {
        this.parentPath = parentPath;
        return this;
    }
}
