package com.enonic.xp.lib.content;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.content.SetActiveContentVersionResult;

public class SetActiveVersionHandler
    extends BaseVersionHandler
{
    private ContentVersionId versionId;

    public void setVersionId( final String versionId )
    {
        this.versionId = ContentVersionId.from( versionId );
    }

    @Override
    protected Object doExecute()
    {
        final ContentId contentId = getContentId();
        if ( contentId == null )
        {
            return false;
        }
        final SetActiveContentVersionResult result = this.contentService.setActiveContentVersion( contentId, versionId );
        return result != null;
    }
}
