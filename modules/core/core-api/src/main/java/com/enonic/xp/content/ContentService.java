package com.enonic.xp.content;

import org.jspecify.annotations.NullMarked;

import com.google.common.io.ByteSource;

import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.archive.ArchiveContentsResult;
import com.enonic.xp.archive.RestoreContentParams;
import com.enonic.xp.archive.RestoreContentsResult;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.site.Site;
import com.enonic.xp.util.BinaryReference;


public interface ContentService
{
    Content create( CreateContentParams params );

    Content create( CreateMediaParams params );

    Content update( UpdateContentParams params );

    Content update( UpdateMediaParams params );

    DeleteContentsResult delete( DeleteContentParams params );

    MoveContentsResult move( MoveContentParams params );

    PublishContentResult publish( PushContentParams params );

    UnpublishContentsResult unpublish( UnpublishContentParams params );

    PatchContentResult patch( PatchContentParams params );

    @NullMarked
    UpdateContentMetadataResult updateMetadata( UpdateContentMetadataParams params );

    @NullMarked
    UpdateWorkflowResult updateWorkflow( UpdateWorkflowParams params );

    DuplicateContentsResult duplicate( DuplicateContentParams params );

    ArchiveContentsResult archive( ArchiveContentParams params );

    RestoreContentsResult restore( RestoreContentParams params );

    SortContentResult sort( SortContentParams params );

    ApplyContentPermissionsResult applyPermissions( ApplyContentPermissionsParams params );

    CompareContentResults resolvePublishDependencies( ResolvePublishDependenciesParams params );

    ContentIds resolveRequiredDependencies( ResolveRequiredDependenciesParams params );

    ContentValidityResult getContentValidity( ContentValidityParams params );

    boolean hasUnpublishedChildren( HasUnpublishedChildrenParams params );

    Content getById( ContentId contentId );

    Site getNearestSite( ContentId contentId );

    Site findNearestSiteByPath( ContentPath contentPath );

    Contents getByIds( GetContentByIdsParams params );

    Content getByPath( ContentPath path );

    Contents getByPaths( ContentPaths paths );

    /**
     * Finds children of a content and fetches every one of them.
     *
     * @deprecated Hides a {@link #getByIds(GetContentByIdsParams)} call per page of children behind what looks like a search, so a caller
     * that only needs ids, paths or a count pays for full content resolution anyway. Search with {@link #find(ContentQuery)} and
     * {@link ContentQuery.Builder#parentPath(ContentPath)} / {@link ContentQuery.Builder#parentId(ContentId)}, then resolve only the ids
     * actually needed. Scheduled for removal.
     */
    @Deprecated
    FindContentByParentResult findByParent( FindContentByParentParams params );

    /**
     * Finds the ids of the children of a content.
     *
     * @deprecated Searching by parent is what {@link #find(ContentQuery)} with
     * {@link ContentQuery.Builder#parentPath(ContentPath)} / {@link ContentQuery.Builder#parentId(ContentId)} does, and it accepts every
     * other constraint a search can carry - a query expression, content types, aggregations, highlighting - while this takes filters
     * alone. Child order is inherited from the parent there too, and {@link ContentQuery.Builder#recursive(boolean)} covers
     * {@link FindContentByParentParams.Builder#recursive(Boolean)}. Scheduled for removal.
     */
    @Deprecated
    FindContentIdsByParentResult findIdsByParent( FindContentByParentParams params );

    /**
     * Enumerates the children of a content - or with {@link ListContentsByParentParams.Builder#recursive(boolean)} its whole subtree -
     * from the branch storage alone. Unlike a {@link #find(ContentQuery)} search this needs no search-index refresh, so it always sees
     * the latest writes, and it reads no contents: entries carry only id and path, permission-checked per entry. Everything readable is
     * returned, ordered by path - an enumeration takes no paging, no filters and no ordering choice; use a search when any of those
     * matter.
     * <p>
     * Storage-only also means storage-only semantics: publish times are not evaluated, so on the master branch entries include contents
     * whose publish window a search would filter out.
     *
     * @since 8.1.0
     */
    ListContentsByParentResult list( ListContentsByParentParams params );

    FindContentIdsByQueryResult find( ContentQuery query );

    FindContentPathsByQueryResult findPaths( ContentQuery query );

    CompareContentResults compare( CompareContentsParams params );

    GetPublishStatusesResult getPublishStatuses( GetPublishStatusesParams params );

    GetContentVersionsResult getVersions( GetContentVersionsParams params );

    GetActiveContentVersionsResult getActiveVersions( GetActiveContentVersionsParams params );

    ByteSource getBinary( ContentId contentId, BinaryReference binaryReference );

    ByteSource getBinary( ContentId contentId, ContentVersionId contentVersionId, BinaryReference binaryReference );

    /**
     * @deprecated Use {@link com.enonic.xp.project.ProjectService#getRootPermissions(com.enonic.xp.project.ProjectName)} instead.
     * Root content is project state and should be accessed via the project API.
     */
    @Deprecated
    AccessControlList getRootPermissions();

    ContentDependencies getDependencies( ContentId id );

    ContentIds getOutboundDependencies( ContentId id );

    boolean contentExists( ContentId contentId );

    boolean contentExists( ContentPath contentPath );

    Content getByIdAndVersionId( ContentId contentId, ContentVersionId versionId );
}
