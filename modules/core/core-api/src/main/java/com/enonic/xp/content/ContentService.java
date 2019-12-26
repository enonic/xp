package com.enonic.xp.content;

import java.io.InputStream;

import com.google.common.io.ByteSource;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.site.CreateSiteParams;
import com.enonic.xp.site.Site;
import com.enonic.xp.util.BinaryReference;

@PublicApi
public interface ContentService
{

    Site create( CreateSiteParams params );

    Content create( CreateContentParams params );

    Content create( CreateMediaParams params );

    Content update( UpdateContentParams params );

    Content update( UpdateMediaParams params );

    Content rename( RenameContentParams params );

    DeleteContentsResult deleteWithoutFetch( DeleteContentParams params );

    int undoPendingDelete( UndoPendingDeleteContentParams params );

    PublishContentResult publish( PushContentParams params );

    UnpublishContentsResult unpublishContent( UnpublishContentParams params );

    CompareContentResults resolvePublishDependencies( ResolvePublishDependenciesParams params );

    ContentIds resolveRequiredDependencies( ResolveRequiredDependenciesParams params );

    @Deprecated
    boolean isValidContent( ContentIds contentIds );

    @Deprecated
    ContentIds getInvalidContent( ContentIds contentIds );

    ContentValidityResult getContentValidity( ContentValidityParams params );

    DuplicateContentsResult duplicate( DuplicateContentParams params );

    MoveContentsResult move( MoveContentParams params );

    Content setChildOrder( SetContentChildOrderParams params );

    ReorderChildContentsResult reorderChildren( ReorderChildContentsParams params );

    Boolean hasUnpublishedChildren( final HasUnpublishedChildrenParams params );

    ApplyContentPermissionsResult applyPermissions( ApplyContentPermissionsParams params );

    Content getById( ContentId contentId );

    Site getNearestSite( ContentId contentId );

    Contents getByIds( GetContentByIdsParams params );

    Content getByPath( ContentPath path );

    AccessControlList getPermissionsById( ContentId contentId );

    Contents getByPaths( ContentPaths paths );

    FindContentByParentResult findByParent( FindContentByParentParams params );

    FindContentIdsByParentResult findIdsByParent( FindContentByParentParams params );

    @Deprecated
    FindContentByQueryResult find( FindContentByQueryParams params );

    FindContentIdsByQueryResult find( ContentQuery query );

    Contents findByApplicationKey( ApplicationKey key);

    @Deprecated
    ContentPaths findContentPaths( ContentQuery query);

    CompareContentResult compare( CompareContentParams params );

    CompareContentResults compare( CompareContentsParams params );

    GetPublishStatusesResult getPublishStatuses( GetPublishStatusesParams params );

    FindContentVersionsResult getVersions( FindContentVersionsParams params );

    GetActiveContentVersionsResult getActiveVersions( GetActiveContentVersionsParams params );

    ContentVersion getActiveVersion(GetActiveContentVersionParams params);

    SetActiveContentVersionResult setActiveContentVersion( ContentId contentId, ContentVersionId versionId );

    ByteSource getBinary( ContentId contentId, BinaryReference binaryReference );

    ByteSource getBinary( ContentId contentId, ContentVersionId contentVersionId, BinaryReference binaryReference );

    @Deprecated
    InputStream getBinaryInputStream( ContentId contentId, BinaryReference binaryReference );

    String getBinaryKey( ContentId contentId, BinaryReference binaryReference );

    AccessControlList getRootPermissions();

    ContentDependencies getDependencies( ContentId id );

    ContentIds getOutboundDependencies( ContentId id );

    boolean contentExists( ContentId contentId );

    boolean contentExists( ContentPath contentPath );

    Content reprocess( ContentId contentId );

    Content getByIdAndVersionId( final ContentId contentId, final ContentVersionId versionId );

    Content getByPathAndVersionId( final ContentPath contentPath, final ContentVersionId versionId );

}
