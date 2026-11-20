package com.enonic.xp.content;

import org.jspecify.annotations.NullMarked;

import com.google.common.io.ByteSource;

import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.archive.ArchiveContentsResult;
import com.enonic.xp.archive.RestoreContentParams;
import com.enonic.xp.archive.RestoreContentsResult;
import com.enonic.xp.index.IndexPath;
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

    /**
     * Resolves the contents to publish together with the given contents, along with how each of them compares to the master branch.
     *
     * @deprecated Resolve the dependencies with {@link #getOutboundDependencies(ContentId)}, repeating it for every dependency found, and
     * tell which of them need publishing from the {@link Content#getPublishInfo()} of each in the draft branch, as described on
     * {@link ContentPublishInfo}. Scheduled for removal.
     */
    @Deprecated
    CompareContentResults resolvePublishDependencies( ResolvePublishDependenciesParams params );

    /**
     * Resolves the ancestors of the given contents that have to be published for the given contents to be published.
     *
     * @deprecated Fetch the ancestors with {@link #getByPaths(ContentPaths)} and tell which of them are not published from the
     * {@link Content#getPublishInfo()} of each in the draft branch, as described on {@link ContentPublishInfo}. Scheduled for removal.
     */
    @Deprecated
    ContentIds resolveRequiredDependencies( ResolveRequiredDependenciesParams params );

    ContentValidityResult getContentValidity( ContentValidityParams params );

    /**
     * Tells whether any child of a content differs from the master branch.
     *
     * @deprecated Find the children with {@link #find(ContentQuery)} and tell which of them are not published or are changed since
     * publishing from the {@link Content#getPublishInfo()} of each in the draft branch, as described on {@link ContentPublishInfo}.
     * Scheduled for removal.
     */
    @Deprecated
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
     * @deprecated Every child is resolved in full, so a caller requiring only ids, paths or a count pays for contents it does not use.
     * Use {@link #find(ContentQuery)} with {@link ContentQuery.Builder#parentPath(ContentPath)} /
     * {@link ContentQuery.Builder#parentId(ContentId)}, followed by {@link #getByIds(GetContentByIdsParams)} for the ids actually
     * required. Scheduled for removal.
     */
    @Deprecated
    FindContentByParentResult findByParent( FindContentByParentParams params );

    /**
     * Finds the ids of the children of a content.
     *
     * @deprecated Use {@link #find(ContentQuery)} with {@link ContentQuery.Builder#parentPath(ContentPath)} /
     * {@link ContentQuery.Builder#parentId(ContentId)}. The query form answers the same question and additionally accepts every other
     * constraint a search supports, including a query expression, content types, aggregations and highlighting, whereas this method
     * accepts filters only. It inherits the child order of the parent in the same way, and
     * {@link ContentQuery.Builder#recursive(boolean)} replaces {@link FindContentByParentParams.Builder#recursive(Boolean)}. Scheduled
     * for removal.
     */
    @Deprecated
    FindContentIdsByParentResult findIdsByParent( FindContentByParentParams params );

    FindContentIdsByQueryResult find( ContentQuery query );

    /**
     * Finds the paths of the contents matching a query.
     *
     * @deprecated Use {@link #find(ContentQuery)} with {@link ContentQuery.Builder#returnFields(IndexPath...)} naming {@code _path}.
     * The query form runs the same search and returns the same paths, already relative to the content root of the calling context, and
     * retains the ids that this method discards, so a caller requiring both no longer has to search twice. Scheduled for removal.
     */
    @Deprecated
    FindContentPathsByQueryResult findPaths( ContentQuery query );

    /**
     * Compares contents in the branch of the calling context with the master branch.
     *
     * @deprecated Tell whether a content is published and whether it is changed since publishing from the
     * {@link Content#getPublishInfo()} of the content in the draft branch, as described on {@link ContentPublishInfo}. Scheduled for
     * removal.
     */
    @Deprecated
    CompareContentResults compare( CompareContentsParams params );

    /**
     * Resolves the publish status of contents by comparing them with the master branch.
     *
     * @deprecated Tell the publish status from the {@link Content#getPublishInfo()} of the content in the draft branch, as described on
     * {@link ContentPublishInfo}. Scheduled for removal.
     */
    @Deprecated
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
