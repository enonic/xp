package com.enonic.wem.api.content;

import java.util.concurrent.CompletableFuture;

import com.google.common.io.ByteSource;

import com.enonic.wem.api.content.site.CreateSiteParams;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.util.BinaryReference;

public interface ContentService
{
    Site create( CreateSiteParams params );

    Content create( CreateContentParams params );

    Content create( CreateMediaParams params );

    Content update( UpdateContentParams params );

    Content update( UpdateMediaParams params );

    Content rename( RenameContentParams params );

    String generateContentName( String displayName );

    Content delete( DeleteContentParams params );

    PushContentsResult push( PushContentParams params );

    Content duplicate( DuplicateContentParams params );

    Content move( MoveContentParams params );

    Content setChildOrder( SetContentChildOrderParams params );

    ReorderChildContentsResult reorderChildren( ReorderChildContentsParams params );

    CompletableFuture<Integer> applyPermissions( ApplyContentPermissionsParams params );

    Content getById( ContentId id );

    Site getNearestSite( ContentId contentId );

    Contents getByIds( GetContentByIdsParams params );

    Content getByPath( ContentPath path );

    Contents getByPaths( ContentPaths paths );

    FindContentByParentResult findByParent( FindContentByParentParams params );

    FindContentByQueryResult find( FindContentByQueryParams params );

    CompareContentResult compare( CompareContentParams params );

    CompareContentResults compare( CompareContentsParams params );

    FindContentVersionsResult getVersions( FindContentVersionsParams params );

    GetActiveContentVersionsResult getActiveVersions( GetActiveContentVersionsParams params );

    ByteSource getBinary( final ContentId contentId, final BinaryReference binaryReference );

    AccessControlList getRootPermissions();
}
