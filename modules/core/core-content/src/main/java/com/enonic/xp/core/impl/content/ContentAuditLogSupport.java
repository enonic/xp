package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.ApplyContentPermissionsResult;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.CreateMediaParams;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.DeleteContentsResult;
import com.enonic.xp.content.DuplicateContentParams;
import com.enonic.xp.content.DuplicateContentsResult;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.MoveContentsResult;
import com.enonic.xp.content.PublishContentResult;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.content.ReorderChildContentsParams;
import com.enonic.xp.content.ReorderChildContentsResult;
import com.enonic.xp.content.SetContentChildOrderParams;
import com.enonic.xp.content.UndoPendingDeleteContentParams;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.content.UnpublishContentsResult;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.UpdateMediaParams;
import com.enonic.xp.site.CreateSiteParams;
import com.enonic.xp.site.Site;

interface ContentAuditLogSupport
{
    void createSite( final CreateSiteParams params, final Site site );

    void createContent( final CreateContentParams params, final Content content );

    void createMedia( final CreateMediaParams params, final Content content );

    void update( final UpdateContentParams params, final Content content );

    void update( final UpdateMediaParams params, final Content content );

    void delete( final DeleteContentParams params, final DeleteContentsResult contents );

    void undoPendingDelete( final UndoPendingDeleteContentParams params, final Contents contents );

    void publish( final PushContentParams params, final PublishContentResult result );

    void unpublishContent( final UnpublishContentParams params, final UnpublishContentsResult result );

    void duplicate( final DuplicateContentParams params, final DuplicateContentsResult result );

    void move( final MoveContentParams params, MoveContentsResult result );

    void rename( final RenameContentParams params, final Content content );

    void setActiveContentVersion( final ContentId contentId, final ContentVersionId versionId );

    void setChildOrder( final SetContentChildOrderParams params, final Content content );

    void reorderChildren( final ReorderChildContentsParams params, final ReorderChildContentsResult result );

    void applyPermissions( final ApplyContentPermissionsParams params, final ApplyContentPermissionsResult result );

    void reprocess( final Content content );
}
