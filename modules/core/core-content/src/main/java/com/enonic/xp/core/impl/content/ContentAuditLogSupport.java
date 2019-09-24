package com.enonic.xp.core.impl.content;

import java.util.stream.Collectors;

import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.audit.AuditLogUri;
import com.enonic.xp.audit.AuditLogUris;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.CreateMediaParams;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.DuplicateContentParams;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.content.ReorderChildContentsParams;
import com.enonic.xp.content.SetContentChildOrderParams;
import com.enonic.xp.content.UndoPendingDeleteContentParams;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.UpdateMediaParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.site.CreateSiteParams;
import com.enonic.xp.site.Site;

class ContentAuditLogSupport
{

    private static final String SOURCE_CORE_CONTENT = "com.enonic.xp.core-content";

    private final AuditLogService auditLogService;

    private ContentAuditLogSupport( final Builder builder )
    {
        this.auditLogService = builder.auditLogService;
    }

    void createSite( final CreateSiteParams params, final Site site )
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "description", params.getDescription() );
        data.setString( "parentContentPath", nullToNull( params.getParentContentPath() ) );
        data.setString( "name", nullToNull( params.getName() ) );
        data.setString( "displayName", params.getDisplayName() );

        log( "system.content.create", "Create a new site", data, site.getId() );
    }

    void createContent( final CreateContentParams params, final Content content )
    {
        final PropertyTree propertyTree = new PropertyTree();
        propertyTree.addString( "displayName", params.getDisplayName() );
        propertyTree.addString( "type", nullToNull( params.getType() ) );
        propertyTree.addString( "name", nullToNull( params.getName() ) );
        propertyTree.addBoolean( "requireValid", params.isRequireValid() );
        propertyTree.addBoolean( "inheritPermissions", params.isInheritPermissions() );
        if ( params.getProcessedIds() != null )
        {
            propertyTree.addStrings( "processedIds", params.getProcessedIds().stream().
                map( ContentId::toString ).collect( Collectors.toList() ) );
        }
        if ( params.getPermissions() != null )
        {
            propertyTree.addStrings( "permissions", params.getPermissions().getEntries().stream().
                map( AccessControlEntry::toString ).collect( Collectors.toList() ) );
        }

        log( "system.content.create", "Create a new content", propertyTree, content.getId() );
    }

    void createMedia( final CreateMediaParams params, final Content content )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "artist", params.getArtist() );
        data.addString( "caption", params.getCaption() );
        data.addString( "copyright", params.getCopyright() );
        data.addString( "mimeType", params.getMimeType() );
        data.addString( "name", params.getName() );
        data.addString( "tags", params.getTags() );
        data.addDouble( "focalX", params.getFocalX() );
        data.addDouble( "focalY", params.getFocalY() );
        data.addString( "parent", nullToNull( params.getParent() ) );

        log( "system.content.create", "Create a new media", data, content.getId() );
    }

    void update( final UpdateContentParams params, final Content content )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "contentId", nullToNull( params.getContentId() ) );
        data.addString( "modifier", nullToNull( params.getModifier() ) );
        data.addBoolean( "clearAttachments", params.isClearAttachments() );
        data.addBoolean( "requireValid", params.isRequireValid() );

        log( "system.content.update", String.format( "Update the content [%s]", nullToNull( content.getId() ) ), data, content.getId() );
    }

    void update( final UpdateMediaParams params, final Content content )
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
        data.addString( "content", nullToNull( params.getContent() ) );

        final String message = String.format( "Update the media [%s]", nullToNull( content.getId() ) );

        log( "system.content.update", message, data, content.getId() );
    }

    void delete( final DeleteContentParams params, final Contents contents )
    {
        if ( contents == null || contents.getIds().getSet() == null )
        {
            return;
        }

        final PropertyTree data = new PropertyTree();
        data.addString( "contentPath", nullToNull( params.getContentPath() ) );
        data.addBoolean( "deleteOnline", params.isDeleteOnline() );

        log( "system.content.delete", String.format( "Delete the content [%s]", contents.getIds().getSize() ), data,
             params.getContentPath() );
    }

    void deleteWithoutFetch( final DeleteContentParams params )
    {
        if ( params.getContentPath() == null )
        {
            return;
        }

        final PropertyTree data = new PropertyTree();
        data.addString( "contentPath", params.getContentPath().toString() );
        data.addBoolean( "deleteOnline", params.isDeleteOnline() );

        final String message = String.format( "Delete the content [%s]", params.getContentPath() );

        log( "system.content.delete", message, data, params.getContentPath() );
    }

    void undoPendingDelete( final UndoPendingDeleteContentParams params, final int affectedContents )
    {
        if ( params.getContentIds() == null )
        {
            return;
        }

        final PropertyTree data = new PropertyTree();
        data.addString( "target", nullToNull( params.getTarget() ) );
        data.addStrings( "contentIds", params.getContentIds().stream().
            map( ContentId::toString ).collect( Collectors.toList() ) );

        final String message = affectedContents > 1
            ? String.format( "Undo the deletion of [%d] contents", affectedContents )
            : String.format( "Undo the deletion of the content [%s]", params.getContentIds().first() );

        log( "system.content.delete", message, data, params.getContentIds() );
    }

    void publish( final PushContentParams params )
    {
        if ( params.getContentIds() == null )
        {
            return;
        }

        final PropertyTree data = new PropertyTree();
        if ( params.getContentIds() != null )
        {
            data.addStrings( "contentIds", params.getContentIds().stream().
                map( ContentId::toString ).collect( Collectors.toList() ) );
        }
        if ( params.getExcludedContentIds() != null )
        {
            data.addStrings( "excludedContentIds", params.getExcludedContentIds().stream().
                map( ContentId::toString ).collect( Collectors.toList() ) );
        }
        if ( params.getExcludeChildrenIds() != null )
        {
            data.addStrings( "excludeChildrenIds", params.getExcludeChildrenIds().stream().
                map( ContentId::toString ).collect( Collectors.toList() ) );
        }
        if ( params.getContentPublishInfo() != null )
        {
            data.addInstant( "contentPublishInfo.from", params.getContentPublishInfo().getFrom() );
            data.addInstant( "contentPublishInfo.to", params.getContentPublishInfo().getTo() );
            data.addInstant( "contentPublishInfo.first", params.getContentPublishInfo().getFirst() );
        }
        data.addString( "target", params.getTarget().toString() );
        data.addString( "message", params.getMessage() );
        data.addBoolean( "includeDependencies", params.isIncludeDependencies() );

        final String message = params.getContentIds().getSize() == 1
            ? String.format( "Publish the content [%s]", params.getContentIds().first() )
            : String.format( "Publish [%d] contents", params.getContentIds().getSize() );

        log( "system.content.publish", message, data, params.getContentIds() );
    }

    void unpublishContent( final UnpublishContentParams params )
    {
        if ( params.getContentIds() == null )
        {
            return;
        }

        final PropertyTree data = new PropertyTree();
        data.addStrings( "contentIds", params.getContentIds().stream().
            map( ContentId::toString ).collect( Collectors.toList() ) );
        data.addBoolean( "includeChildren", params.isIncludeChildren() );
        if ( params.getUnpublishBranch() != null )
        {
            data.addString( "unpublishBranch", params.getUnpublishBranch().getValue() );
        }

        final String message = params.getContentIds().getSize() == 1
            ? String.format( "Unpublish the content [%s]", params.getContentIds().first() )
            : String.format( "Unpublish [%d] contents", params.getContentIds().getSize() );

        log( "system.content.unpublishContent", message, data, params.getContentIds() );
    }

    void duplicate( final DuplicateContentParams params )
    {
        if ( params.getContentId() == null )
        {
            return;
        }

        final PropertyTree data = new PropertyTree();
        data.addString( "contentId", params.getContentId().toString() );
        data.addBoolean( "includeChildren", params.getIncludeChildren() );
        if ( params.getCreator() != null )
        {
            data.addString( "creator", params.getCreator().getId() );
        }

        final String message = String.format( "Duplicate the content [%s]", params.getContentId() );

        log( "system.content.duplicate", message, data, params.getContentId() );
    }

    void move( final MoveContentParams params )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "contentId", nullToNull( params.getContentId() ) );
        data.addString( "parentContentPath", nullToNull( params.getParentContentPath() ) );
        if ( params.getCreator() != null )
        {
            data.addString( "creator", params.getCreator().getId() );
        }

        final String message =
            String.format( "Move the content [%s] under parent with path [%s]", params.getContentId(), params.getParentContentPath() );

        log( "system.content.move", message, data, params.getContentId() );
    }

    void rename( final RenameContentParams params )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "contentId", nullToNull( params.getContentId() ) );
        data.addString( "newName", nullToNull( params.getNewName() ) );

        final String message =
            String.format( "Rename the content to [%s] with contentId [%s]", params.getNewName(), params.getContentId() );

        log( "system.content.rename", message, data, params.getContentId() );
    }

    void setActiveContentVersion( final ContentId contentId, final ContentVersionId versionId )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "contentId", nullToNull( contentId ) );
        data.addString( "versionId", nullToNull( versionId ) );

        final String message = String.format( "Set active the content version [%s][%s]", contentId, versionId );

        log( "system.content.setActiveContentVersion", message, data, contentId );
    }

    void setChildOrder( final SetContentChildOrderParams params )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "contentId", nullToNull( params.getContentId() ) );
        data.addString( "childOrder", nullToNull( params.getChildOrder() ) );

        final String message = String.format( "Set the child order for the content [%s]", params.getContentId() );

        log( "system.content.setChildOrder", message, data, params.getContentId() );
    }

    void reorderChildren( final ReorderChildContentsParams params )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "contentId", nullToNull( params.getContentId() ) );
        data.addBoolean( "silent", params.isSilent() );

        final String message = String.format( "Reorder the children of the content [%s]", params.getContentId() );

        log( "system.content.reorderChildren", message, data, params.getContentId() );
    }

    void applyPermissions( final ApplyContentPermissionsParams params )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "contentId", nullToNull( params.getContentId() ) );
        data.addBoolean( "inheritPermissions", params.isInheritPermissions() );
        data.addBoolean( "overwriteChildPermissions", params.isOverwriteChildPermissions() );

        if ( params.getPermissions() != null )
        {
            data.addStrings( "permissions", params.getPermissions().getEntries().stream().
                map( AccessControlEntry::toString ).collect( Collectors.toList() ) );
        }

        final String message = String.format( "Apply permissions for the content [%s]", params.getContentId() );

        log( "system.content.applyPermissions", message, data, params.getContentId() );
    }

    void reprocess( final ContentId contentId )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "contentId", nullToNull( contentId ) );

        final String message = String.format( "Reprocess the content [%s]", contentId );

        log( "system.content.reprocess", message, data, contentId );
    }

    private void log( final String type, final String message, final PropertyTree data, final ContentPaths contentPaths )
    {
        final LogAuditLogParams logParams = LogAuditLogParams.create().
            type( type ).
            source( SOURCE_CORE_CONTENT ).
            data( data ).
            message( message ).
            objectUris( AuditLogUris.from( contentPaths.
                stream().
                map( this::createAuditLogUri ).
                collect( Collectors.toList() ) ) ).
            build();

        auditLogService.log( logParams );
    }

    private void log( final String type, final String message, final PropertyTree data, final ContentIds contentIds )
    {
        final LogAuditLogParams logParams = LogAuditLogParams.create().
            type( type ).
            source( SOURCE_CORE_CONTENT ).
            data( data ).
            message( message ).
            objectUris( AuditLogUris.from( contentIds.
                stream().
                map( this::createAuditLogUri ).
                collect( Collectors.toList() ) ) ).
            build();

        auditLogService.log( logParams );
    }

    private void log( final String type, final String message, final PropertyTree data, final ContentId contentId )
    {
        log( type, message, data, ContentIds.from( contentId ) );
    }

    private void log( final String type, final String message, final PropertyTree data, final ContentPath contentPath )
    {
        log( type, message, data, ContentPaths.from( contentPath ) );
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

    private String nullToNull( Object value )
    {
        return value != null ? value.toString() : null;
    }

    static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        private AuditLogService auditLogService;

        Builder auditLogService( final AuditLogService auditLogService )
        {
            this.auditLogService = auditLogService;
            return this;
        }

        ContentAuditLogSupport build()
        {
            return new ContentAuditLogSupport( this );
        }
    }

}
