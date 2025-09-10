package com.enonic.xp.content;

import com.google.common.io.ByteSource;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.archive.ArchiveContentsResult;
import com.enonic.xp.archive.RestoreContentParams;
import com.enonic.xp.archive.RestoreContentsResult;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.site.Site;
import com.enonic.xp.util.BinaryReference;

@PublicApi
public interface ContentService
{
    Content create( CreateContentParams params );

    Content create( CreateMediaParams params );

    Content update( UpdateContentParams params );

    Content update( UpdateMediaParams params );

    PatchContentResult patch( PatchContentParams params );

    Content rename( RenameContentParams params );

    DeleteContentsResult delete( DeleteContentParams params );

    PublishContentResult publish( PushContentParams params );

    UnpublishContentsResult unpublish( UnpublishContentParams params );

    CompareContentResults resolvePublishDependencies( ResolvePublishDependenciesParams params );

    ContentIds resolveRequiredDependencies( ResolveRequiredDependenciesParams params );

    ContentValidityResult getContentValidity( ContentValidityParams params );

    DuplicateContentsResult duplicate( DuplicateContentParams params );

    MoveContentsResult move( MoveContentParams params );

    ArchiveContentsResult archive( ArchiveContentParams params );

    RestoreContentsResult restore( RestoreContentParams params );

    SortContentResult sort( SortContentParams params );

    Boolean hasUnpublishedChildren( HasUnpublishedChildrenParams params );

    ApplyContentPermissionsResult applyPermissions( ApplyContentPermissionsParams params );

    Content getById( ContentId contentId );

    Site getNearestSite( ContentId contentId );

    Site findNearestSiteByPath( ContentPath contentPath );

    Contents getByIds( GetContentByIdsParams params );

    Content getByPath( ContentPath path );

    Contents getByPaths( ContentPaths paths );

    FindContentByParentResult findByParent( FindContentByParentParams params );

    FindContentIdsByParentResult findIdsByParent( FindContentByParentParams params );

    FindContentIdsByQueryResult find( ContentQuery query );

    FindContentPathsByQueryResult findPaths( ContentQuery query );

    CompareContentResults compare( CompareContentsParams params );

    GetPublishStatusesResult getPublishStatuses( GetPublishStatusesParams params );

    FindContentVersionsResult getVersions( FindContentVersionsParams params );

    GetActiveContentVersionsResult getActiveVersions( GetActiveContentVersionsParams params );

    ByteSource getBinary( ContentId contentId, BinaryReference binaryReference );

    ByteSource getBinary( ContentId contentId, ContentVersionId contentVersionId, BinaryReference binaryReference );

    String getBinaryKey( ContentId contentId, BinaryReference binaryReference );

    AccessControlList getRootPermissions();

    ContentDependencies getDependencies( ContentId id );

    ContentIds getOutboundDependencies( ContentId id );

    boolean contentExists( ContentId contentId );

    boolean contentExists( ContentPath contentPath );

    Content getByIdAndVersionId( ContentId contentId, ContentVersionId versionId );

    ImportContentResult importContent( ImportContentParams params );
}
