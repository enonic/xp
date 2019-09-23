package com.enonic.xp.core.impl.content;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.audit.AuditLogUri;
import com.enonic.xp.audit.AuditLogUris;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.CreateMediaParams;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.UpdateMediaParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.site.CreateSiteParams;
import com.enonic.xp.site.Site;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public class ContentAuditLogSupport
{

    private static final String SOURCE_CORE_CONTENT = "com.enonic.xp.core-content";

    private final AuditLogService auditLogService;

    private ContentAuditLogSupport( final Builder builder )
    {
        this.auditLogService = builder.auditLogService;
    }

    public void createSite( final CreateSiteParams params, final Site site )
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "description", params.getDescription() );

        final LogAuditLogParams logParams = createAuditLogParams( "system.content.create", "Create a new site", data, site.getId() );

        auditLogService.log( logParams );
    }

    public void createContent( final CreateContentParams params, final Content content )
    {
        final LogAuditLogParams logParams =
            createAuditLogParams( "system.content.create", "Create a new content", params.getData(), content.getId() );

        auditLogService.log( logParams );

    }

    public void createMedia( final CreateMediaParams params, final Content content )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "artist", params.getArtist() );
        data.addString( "caption", params.getCaption() );
        data.addString( "copyright", params.getCopyright() );
        data.addString( "mimeType", params.getMimeType() );
        data.addString( "name", params.getName() );
        data.addString( "tags", params.getTags() );
        data.addString( "parent", params.getParent() != null ? params.getParent().toString() : "" );
        data.addDouble( "focalX", params.getFocalX() );
        data.addDouble( "focalY", params.getFocalY() );

        final LogAuditLogParams logParams = createAuditLogParams( "system.content.create", "Create a new media", data, content.getId() );

        auditLogService.log( logParams );
    }

    public void update( final UpdateContentParams params, final Content content )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "contentId", params.getContentId() != null ? params.getContentId().toString() : "" );
        data.addString( "modifier", params.getModifier() != null ? params.getModifier().toString() : "" );

        final LogAuditLogParams logParams =
            createAuditLogParams( "system.content.update", String.format( "Update the content [%s]", content.getId().toString() ), data,
                                  content.getId() );
        auditLogService.log( logParams );
    }

    public void update( final UpdateMediaParams params, final Content content )
    {
        final PropertyTree data = new PropertyTree();

        data.addString( "artist", params.getArtist() );
        data.addString( "copyright", params.getCopyright() );
        data.addString( "caption", params.getCaption() );
        data.addString( "mimeType", params.getMimeType() );
        data.addString( "name", params.getName() );
        data.addString( "tags", params.getTags() );
        data.addDouble( "focalX", params.getFocalX() );
        data.addDouble( "focalY", params.getFocalY() );
        data.addString( "content", params.getContent().toString() );

        final LogAuditLogParams logParams =
            createAuditLogParams( "system.content.update", String.format( "Update the media [%s]", content.getId().toString() ), data,
                                  content.getId() );
        auditLogService.log( logParams );
    }

    public void delete( final DeleteContentParams params, final Contents contents )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "contentPath", params.getContentPath().toString() );

        final LogAuditLogParams logParams =
            createAuditLogParams( "system.content.delete", String.format( "Delete the content [%s]", contents.getIds().getSize() ), data,
                                  params.getContentPath() );
        auditLogService.log( logParams );
    }

    public void deleteWithoutFetch( final DeleteContentParams params )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "contentPath", params.getContentPath().toString() );
        data.addBoolean( "deleteOnline", params.isDeleteOnline() );

        final LogAuditLogParams logParams =
            createAuditLogParams( "system.content.delete", String.format( "Delete the content [%s]", params.getContentPath() ), data,
                                  params.getContentPath() );

        auditLogService.log( logParams );
    }


    private LogAuditLogParams createAuditLogParams( final String type, final String message, final PropertyTree data,
                                                    final ContentId contentId )
    {
        return createAuditLogParams( type, message, data, Collections.singletonList( contentId ) );
    }

    private LogAuditLogParams createAuditLogParams( final String type, final String message, final PropertyTree data,
                                                    final ContentPath contentPath )
    {
        return createAuditLogParamsFromPaths( type, message, data, Collections.singletonList( contentPath ) );
    }

    private LogAuditLogParams createAuditLogParamsFromPaths( final String type, final String message, final PropertyTree data,
                                                             final Collection<ContentPath> contentPaths )
    {
        return LogAuditLogParams.create().
            type( type ).
            source( SOURCE_CORE_CONTENT ).
            data( data ).
            message( message ).
            objectUris( fromContentPaths( contentPaths ) ).
            build();
    }

    private LogAuditLogParams createAuditLogParams( final String type, final String message, final PropertyTree data,
                                                    final Collection<ContentId> contentIds )
    {
        return LogAuditLogParams.create().
            type( type ).
            source( SOURCE_CORE_CONTENT ).
            data( data ).
            message( message ).
            objectUris( fromContentIds( contentIds ) ).
            build();
    }

    private AuditLogUris fromContentIds( final Collection<ContentId> contentIds )
    {
        return AuditLogUris.from( ofNullable( contentIds ).orElse( emptyList() ).
            stream().
            map( this::createAuditLogUri ).
            collect( Collectors.toList() ) );
    }

    private AuditLogUris fromContentPaths( final Collection<ContentPath> contentPaths )
    {
        return AuditLogUris.from( ofNullable( contentPaths ).orElse( emptyList() ).
            stream().
            map( this::createAuditLogUri ).
            collect( Collectors.toList() ) );
    }

    private AuditLogUri createAuditLogUri( final ContentId contentId )
    {
        final Context context = ContextAccessor.current();
        return AuditLogUri.from( context.getRepositoryId() + ":" + context.getBranch() + ":" + contentId );
    }

    private AuditLogUri createAuditLogUri( final ContentPath contentPath )
    {
        final Context context = ContextAccessor.current();
        return AuditLogUri.from( context.getRepositoryId() + ":" + context.getBranch() + ":/content" + contentPath );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private AuditLogService auditLogService;

        public Builder auditLogService( final AuditLogService auditLogService )
        {
            this.auditLogService = auditLogService;
            return this;
        }

        public ContentAuditLogSupport build()
        {
            return new ContentAuditLogSupport( this );
        }
    }

}
