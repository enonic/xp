package com.enonic.wem.api.content;

import com.enonic.wem.api.content.versioning.ContentVersions;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;

public interface ContentService
{
    Content getById( ContentId id, final Context context );

    Contents getByIds( GetContentByIdsParams params, final Context context );

    Content getByPath( ContentPath path, final Context context );

    Contents getByPaths( ContentPaths paths, final Context context );

    FindContentByParentResult findByParent( FindContentByParentParams params, final Context context );

    Content create( CreateContentParams params, final Context context );

    Content update( UpdateContentParams params, final Context context );

    Content push( final PushContentParams params, final Context context );

    DeleteContentResult delete( DeleteContentParams params, final Context context );

    DataValidationErrors validate( ValidateContentData data, final Context context );

    Content rename( RenameContentParams params, final Context context );

    FindContentByQueryResult find( FindContentByQueryParams params, final Context context );

    CompareContentResult compare( final CompareContentParams params, final Context context );

    CompareContentResults compare( final CompareContentsParams params, final Context context );

    ContentVersions getVersions( final GetContentVersionsParams params, final Context context );

    String generateContentName( String displayName );
}
