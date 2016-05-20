package com.enonic.xp.content;

import java.io.InputStream;
import java.util.concurrent.Future;

import com.google.common.annotations.Beta;
import com.google.common.io.ByteSource;

import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.site.CreateSiteParams;
import com.enonic.xp.site.Site;
import com.enonic.xp.util.BinaryReference;

@Beta
public interface ContentService
{

    Site create( CreateSiteParams params );

    Content create( CreateContentParams params );

    Content create( CreateMediaParams params );

    Content update( UpdateContentParams params );

    Content update( UpdateMediaParams params );

    Content rename( RenameContentParams params );

    Contents delete( DeleteContentParams params );

    PushContentsResult push( PushContentParams params );

    ResolvePublishDependenciesResult resolvePublishDependencies( ResolvePublishDependenciesParams params );

    Content duplicate( DuplicateContentParams params );

    Content move( MoveContentParams params );

    Content setChildOrder( SetContentChildOrderParams params );

    ReorderChildContentsResult reorderChildren( ReorderChildContentsParams params );

    Future<Integer> applyPermissions( ApplyContentPermissionsParams params );

    Content getById( ContentId contentId );

    Site getNearestSite( ContentId contentId );

    Contents getByIds( GetContentByIdsParams params );

    Content getByPath( ContentPath path );

    AccessControlList getPermissionsById( ContentId contentId );

    Contents getByPaths( ContentPaths paths );

    FindContentByParentResult findByParent( FindContentByParentParams params );

    FindContentByQueryResult find( FindContentByQueryParams params );

    CompareContentResult compare( CompareContentParams params );

    CompareContentResults compare( CompareContentsParams params );

    FindContentVersionsResult getVersions( FindContentVersionsParams params );

    GetActiveContentVersionsResult getActiveVersions( GetActiveContentVersionsParams params );

    SetActiveContentVersionResult setActiveContentVersion( final ContentId contentId, final ContentVersionId versionId );

    ByteSource getBinary( ContentId contentId, BinaryReference binaryReference );

    @Deprecated
    InputStream getBinaryInputStream( ContentId contentId, BinaryReference binaryReference );

    String getBinaryKey( ContentId contentId, BinaryReference binaryReference );

    AccessControlList getRootPermissions();

    boolean contentExists( ContentId contentId );

    boolean contentExists( ContentPath contentPath );

    Content reprocess( ContentId contentId );
}
