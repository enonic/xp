package com.enonic.xp.core.impl.content;

import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.archive.ArchiveContentsResult;
import com.enonic.xp.archive.RestoreContentParams;
import com.enonic.xp.archive.RestoreContentsResult;
import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.ApplyContentPermissionsResult;
import com.enonic.xp.content.Content;
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
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.content.UnpublishContentsResult;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.UpdateMediaParams;
import com.enonic.xp.site.CreateSiteParams;
import com.enonic.xp.site.Site;

interface ContentAuditLogSupport
{
    void createSite( CreateSiteParams params, Site site );

    void createContent( CreateContentParams params, Content content );

    void createMedia( CreateMediaParams params, Content content );

    void update( UpdateContentParams params, Content content );

    void update( UpdateMediaParams params, Content content );

    void delete( DeleteContentParams params, DeleteContentsResult contents );

    void publish( PushContentParams params, PublishContentResult result );

    void unpublishContent( UnpublishContentParams params, UnpublishContentsResult result );

    void duplicate( DuplicateContentParams params, DuplicateContentsResult result );

    void move( MoveContentParams params, MoveContentsResult result );

    void archive( ArchiveContentParams params, ArchiveContentsResult result );

    void restore( RestoreContentParams params, RestoreContentsResult result );

    void rename( RenameContentParams params, Content content );

    void setChildOrder( SetContentChildOrderParams params, Content content );

    void reorderChildren( ReorderChildContentsParams params, ReorderChildContentsResult result );

    void applyPermissions( ApplyContentPermissionsParams params, ApplyContentPermissionsResult result );

    void reprocess( Content content );
}
