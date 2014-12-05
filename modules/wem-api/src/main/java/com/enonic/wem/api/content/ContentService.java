package com.enonic.wem.api.content;

import com.enonic.wem.api.content.site.CreateSiteParams;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;

public interface ContentService
{
    Site create( final CreateSiteParams params );

    Content create( CreateContentParams params );

    Content update( UpdateContentParams params );

    Content rename( RenameContentParams params );

    String generateContentName( String displayName );

    DeleteContentResult delete( DeleteContentParams params );

    Content push( final PushContentParams params );

    Content duplicate( DuplicateContentParams params );

    Content setChildOrder( final SetContentChildOrderParams params );

    ReorderChildContentsResult reorderChildren( final ReorderChildContentsParams params );

    Content getById( ContentId id );

    Site getNearestSite( ContentId contentId );

    Contents getByIds( GetContentByIdsParams params );

    Content getByPath( ContentPath path );

    Contents getByPaths( ContentPaths paths );

    FindContentByParentResult findByParent( FindContentByParentParams params );

    DataValidationErrors validate( ValidateContentData data );

    FindContentByQueryResult find( FindContentByQueryParams params );

    CompareContentResult compare( final CompareContentParams params );

    CompareContentResults compare( final CompareContentsParams params );

    FindContentVersionsResult getVersions( final FindContentVersionsParams params );

    GetActiveContentVersionsResult getActiveVersions( final GetActiveContentVersionsParams params );

    ContentPermissions getPermissions( final ContentId contentId );
}
