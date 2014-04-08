package com.enonic.wem.api.content;

import com.enonic.wem.api.content.query.ContentQuery;
import com.enonic.wem.api.content.query.ContentQueryResult;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;

public interface ContentService
{
    Content getById( ContentId id );

    Contents getByIds( GetContentByIdsParams params );

    Content getByPath( ContentPath path );

    Contents getByPaths( ContentPaths paths );

    Contents getRoots();

    Contents getChildren( ContentPath parentPath );

    Content create( CreateContentParams params );

    Content update( UpdateContentParams params );

    DeleteContentResult delete( DeleteContentParams params );

    DataValidationErrors validate( ValidateContentData data );

    Content rename( RenameContentParams params );

    ContentQueryResult find( ContentQuery contentQuery );

    String generateContentName( String displayName );
}
