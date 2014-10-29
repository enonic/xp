package com.enonic.wem.api.content;

import com.enonic.wem.api.content.site.CreateSiteParams;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;

public interface ContentService
{
    Content getById( ContentId id );

    Site getNearestSite( ContentId contentId );

    Contents getByIds( GetContentByIdsParams params );

    Content getByPath( ContentPath path );

    Contents getByPaths( ContentPaths paths );

    FindContentByParentResult findByParent( FindContentByParentParams params );

    Site create( final CreateSiteParams params );

    Content create( CreateContentParams params );

    Content update( UpdateContentParams params );

    Content push( final PushContentParams params );

    DeleteContentResult delete( DeleteContentParams params );

    DataValidationErrors validate( ValidateContentData data );

    Content duplicate( DuplicateContentParams params );

    Content rename( RenameContentParams params );

    FindContentByQueryResult find( FindContentByQueryParams params );

    CompareContentResult compare( final CompareContentParams params );

    CompareContentResults compare( final CompareContentsParams params );

    FindContentVersionsResult getVersions( final FindContentVersionsParams params );

    GetActiveContentVersionsResult getActiveVersions( final GetActiveContentVersionsParams params );

    Content setChildOrder( final SetContentChildOrderParams params );

    Content orderChild( final OrderChildContentParams params );

    String generateContentName( String displayName );

    ContentPermissions getPermissions( final ContentId contentId );
}
