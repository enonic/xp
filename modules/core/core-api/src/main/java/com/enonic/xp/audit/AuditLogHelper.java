package com.enonic.xp.audit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertyTree;

public class AuditLogHelper
{

    public static final String SOURCE_CORE_CONTENT = "com.enonic.xp.core-content";

    public static LogAuditLogParams createAuditLogParams( final String type, final String source, final String message,
                                                          final PropertyTree data, final Collection<ContentId> contentIds )
    {
        return LogAuditLogParams.create().
            type( type ).
            source( source ).
            data( data ).
            message( message ).
            objectUris( createAuditLogUris( contentIds ) ).
            build();
    }

    public static AuditLogUris createAuditLogUris( final Collection<ContentId> contentIds )
    {
        if ( contentIds == null || contentIds.isEmpty() )
        {
            return AuditLogUris.empty();
        }

        final List<AuditLogUri> auditLogUris = new ArrayList<>();
        contentIds.forEach( contentId -> {
            auditLogUris.add( createAuditLogUri( contentId ) );
        } );

        return AuditLogUris.from( auditLogUris );
    }

    public static AuditLogUri createAuditLogUri( final ContentId contentId )
    {
        final Context context = ContextAccessor.current();
        return AuditLogUri.from( context.getRepositoryId() + ":" + context.getBranch() + ":" + contentId );
    }

}
