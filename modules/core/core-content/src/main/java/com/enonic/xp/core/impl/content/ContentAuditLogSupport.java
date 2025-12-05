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
import com.enonic.xp.content.ImportContentParams;
import com.enonic.xp.content.ImportContentResult;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.MoveContentsResult;
import com.enonic.xp.content.PatchContentParams;
import com.enonic.xp.content.PatchContentResult;
import com.enonic.xp.content.PublishContentResult;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.SortContentParams;
import com.enonic.xp.content.SortContentResult;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.content.UnpublishContentsResult;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.UpdateMediaParams;

interface ContentAuditLogSupport
{
    void createContent( CreateContentParams params, Content content );

    void createMedia( CreateMediaParams params, Content content );

    void update( UpdateContentParams params, Content content );

    void update( UpdateMediaParams params, Content content );

    void importContent( ImportContentParams params, ImportContentResult result );

    void patch( PatchContentParams params, PatchContentResult result );

    void delete( DeleteContentParams params, DeleteContentsResult contents );

    void publish( PushContentParams params, PublishContentResult result );

    void unpublishContent( UnpublishContentParams params, UnpublishContentsResult result );

    void duplicate( DuplicateContentParams params, DuplicateContentsResult result );

    void move( MoveContentParams params, MoveContentsResult result );

    void archive( ArchiveContentParams params, ArchiveContentsResult result );

    void restore( RestoreContentParams params, RestoreContentsResult result );

    void sort( SortContentParams params, SortContentResult result );

    void applyPermissions( ApplyContentPermissionsParams params, ApplyContentPermissionsResult result );
}
