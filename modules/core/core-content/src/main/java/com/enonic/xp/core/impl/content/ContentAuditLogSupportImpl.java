package com.enonic.xp.core.impl.content;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.audit.AuditLogUri;
import com.enonic.xp.audit.AuditLogUris;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.ApplyContentPermissionsResult;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.CreateMediaParams;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.DeleteContentsResult;
import com.enonic.xp.content.DuplicateContentParams;
import com.enonic.xp.content.DuplicateContentsResult;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.MoveContentsResult;
import com.enonic.xp.content.PublishContentResult;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.content.ReorderChildContentsParams;
import com.enonic.xp.content.ReorderChildContentsResult;
import com.enonic.xp.content.SetContentChildOrderParams;
import com.enonic.xp.content.UndoPendingDeleteContentParams;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.content.UnpublishContentsResult;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.UpdateMediaParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.name.NamePrettyfier;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.CreateSiteParams;
import com.enonic.xp.site.Site;

import static com.google.common.base.Strings.isNullOrEmpty;

@Component(configurationPid = "com.enonic.xp.content")
public class ContentAuditLogSupportImpl
    implements ContentAuditLogSupport
{
    private static final String SOURCE_CORE_CONTENT = "com.enonic.xp.core-content";

    private final Executor executor;

    private final AuditLogService auditLogService;

    @Activate
    public ContentAuditLogSupportImpl( final ContentConfig config,
                                       @Reference(service = ContentAuditLogExecutor.class) final Executor executor,
                                       @Reference AuditLogService auditLogService )
    {
        this.executor = config.auditlog_enabled() ? executor : null;
        this.auditLogService = auditLogService;
    }


    public void createSite( final CreateSiteParams params, final Site site )
    {
        if ( executor != null )
        {
            executor.execute( () -> doCreateSite( params, site ) );
        }
    }

    private void doCreateSite( final CreateSiteParams params, final Site site )
    {
        final PropertyTree data = new PropertyTree();
        final PropertySet paramsSet = data.addSet( "params" );
        final PropertySet resultSet = data.addSet( "result" );

        paramsSet.setString( "description", params.getDescription() );
        paramsSet.setString( "parentContentPath", nullToNull( params.getParentContentPath() ) );
        paramsSet.setString( "name", nullToNull( params.getName() ) );
        paramsSet.setString( "displayName", params.getDisplayName() );

        addContent( resultSet, site );

        log( "system.content.create", data, site.getPath() );
    }

    public void createContent( final CreateContentParams params, final Content content )
    {
        if ( executor != null )
        {
            executor.execute( () -> doCreateContent( params, content ) );
        }
    }

    private void doCreateContent( final CreateContentParams params, final Content content )
    {
        final PropertyTree data = new PropertyTree();
        final PropertySet paramsSet = data.addSet( "params" );
        final PropertySet resultSet = data.addSet( "result" );

        paramsSet.addString( "displayName", params.getDisplayName() );
        paramsSet.addString( "type", nullToNull( params.getType() ) );
        paramsSet.addString( "name", nullToNull( params.getName() ) );
        paramsSet.addBoolean( "requireValid", params.isRequireValid() );
        paramsSet.addBoolean( "inheritPermissions", params.isInheritPermissions() );

        if ( params.getProcessedIds() != null )
        {
            paramsSet.addStrings( "processedIds", params.getProcessedIds().stream().
                map( ContentId::toString ).collect( Collectors.toList() ) );
        }
        if ( params.getPermissions() != null )
        {
            paramsSet.addStrings( "permissions", params.getPermissions().getEntries().stream().
                map( AccessControlEntry::toString ).collect( Collectors.toList() ) );
        }

        addContent( resultSet, content );

        log( "system.content.create", data, content.getPath() );
    }

    public void createMedia( final CreateMediaParams params, final Content content )
    {
        if ( executor != null )
        {
            executor.execute( () -> doCreateMedia( params, content ) );
        }
    }

    private void doCreateMedia( final CreateMediaParams params, final Content content )
    {
        final PropertyTree data = new PropertyTree();
        final PropertySet paramsSet = data.addSet( "params" );
        final PropertySet resultSet = data.addSet( "result" );

        paramsSet.addString( "artist", params.getArtist() );
        paramsSet.addString( "caption", params.getCaption() );
        paramsSet.addString( "copyright", params.getCopyright() );
        paramsSet.addString( "mimeType", params.getMimeType() );
        paramsSet.addString( "name", params.getName() );
        paramsSet.addString( "tags", params.getTags() );
        paramsSet.addDouble( "focalX", params.getFocalX() );
        paramsSet.addDouble( "focalY", params.getFocalY() );
        paramsSet.addString( "parent", nullToNull( params.getParent() ) );

        addContent( resultSet, content );

        log( "system.content.create", data, content.getPath() );
    }

    public void update( final UpdateContentParams params, final Content content )
    {
        if ( executor != null )
        {
            executor.execute( () -> doUpdate( params, content ) );
        }
    }

    private void doUpdate( final UpdateContentParams params, final Content content )
    {
        final PropertyTree data = new PropertyTree();
        final PropertySet paramsSet = data.addSet( "params" );
        final PropertySet resultSet = data.addSet( "result" );

        paramsSet.addString( "contentId", nullToNull( params.getContentId() ) );
        paramsSet.addString( "modifier", nullToNull( params.getModifier() ) );
        paramsSet.addBoolean( "clearAttachments", params.isClearAttachments() );
        paramsSet.addBoolean( "requireValid", params.isRequireValid() );

        addContent( resultSet, content );

        log( "system.content.update", data, content.getId() );
    }

    public void update( final UpdateMediaParams params, final Content content )
    {
        if ( executor != null )
        {
            executor.execute( () -> doUpdate( params, content ) );
        }
    }

    private void doUpdate( final UpdateMediaParams params, final Content content )
    {
        final PropertyTree data = new PropertyTree();
        final PropertySet paramsSet = data.addSet( "params" );
        final PropertySet resultSet = data.addSet( "result" );

        paramsSet.addString( "artist", params.getArtist() );
        paramsSet.addString( "copyright", params.getCopyright() );
        paramsSet.addString( "caption", params.getCaption() );
        paramsSet.addString( "mimeType", params.getMimeType() );
        paramsSet.addString( "name", params.getName() );
        paramsSet.addString( "tags", params.getTags() );
        paramsSet.addDouble( "focalX", params.getFocalX() );
        paramsSet.addDouble( "focalY", params.getFocalY() );
        paramsSet.addString( "content", nullToNull( params.getContent() ) );

        addContent( resultSet, content );

        log( "system.content.update", data, content.getId() );
    }

    public void delete( final DeleteContentParams params, final DeleteContentsResult contents )
    {
        if ( executor != null )
        {
            executor.execute( () -> doDelete( params, contents ) );
        }
    }

    private void doDelete( final DeleteContentParams params, final DeleteContentsResult contents )
    {
        final PropertyTree data = new PropertyTree();
        final PropertySet paramsSet = data.addSet( "params" );
        final PropertySet resultSet = data.addSet( "result" );

        paramsSet.addString( "contentPath", params.getContentPath().toString() );
        paramsSet.addBoolean( "deleteOnline", params.isDeleteOnline() );

        addContents( resultSet, contents.getDeletedContents(), "deletedContents" );
        addContents( resultSet, contents.getPendingContents(), "pendingContents" );

        log( "system.content.delete", data, ContentIds.create().
            addAll( contents.getDeletedContents() ).
            addAll( contents.getPendingContents() ).
            build() );
    }

    public void undoPendingDelete( final UndoPendingDeleteContentParams params, final Contents contents )
    {
        if ( executor != null )
        {
            executor.execute( () -> doUndoPendingDelete( params, contents ) );
        }
    }

    private void doUndoPendingDelete( final UndoPendingDeleteContentParams params, final Contents contents )
    {
        if ( params.getContentIds() == null )
        {
            return;
        }

        final PropertyTree data = new PropertyTree();
        final PropertySet paramsSet = data.addSet( "params" );

        paramsSet.addString( "target", nullToNull( params.getTarget() ) );
        paramsSet.addStrings( "contentIds", params.getContentIds().stream().
            map( ContentId::toString ).collect( Collectors.toList() ) );

        addContents( data.getRoot(), contents, "result" );

        log( "system.content.delete", data, contents.getIds() );
    }

    public void publish( final PushContentParams params, final PublishContentResult result )
    {
        if ( executor != null )
        {
            executor.execute( () -> doPublish( params, result ) );
        }
    }

    private void doPublish( final PushContentParams params, final PublishContentResult result )
    {
        if ( params.getContentIds() == null )
        {
            return;
        }

        final PropertyTree data = new PropertyTree();
        final PropertySet paramsSet = data.addSet( "params" );
        final PropertySet resultSet = data.addSet( "result" );

        if ( params.getContentIds() != null )
        {
            paramsSet.addStrings( "contentIds", params.getContentIds().stream().
                map( ContentId::toString ).collect( Collectors.toList() ) );
        }
        if ( params.getExcludedContentIds() != null )
        {
            paramsSet.addStrings( "excludedContentIds", params.getExcludedContentIds().stream().
                map( ContentId::toString ).collect( Collectors.toList() ) );
        }
        if ( params.getExcludeChildrenIds() != null )
        {
            paramsSet.addStrings( "excludeChildrenIds", params.getExcludeChildrenIds().stream().
                map( ContentId::toString ).collect( Collectors.toList() ) );
        }
        if ( params.getContentPublishInfo() != null )
        {
            final ContentPublishInfo contentPublishInfo = params.getContentPublishInfo();
            final PropertySet contentPublishInfoSet = paramsSet.addSet( "contentPublishInfo" );
            contentPublishInfoSet.addInstant( "from", contentPublishInfo.getFrom() );
            contentPublishInfoSet.addInstant( "to", contentPublishInfo.getTo() );
            contentPublishInfoSet.addInstant( "first", contentPublishInfo.getFirst() );
        }
        paramsSet.addString( "target", params.getTarget().toString() );
        paramsSet.addString( "message", params.getMessage() );
        paramsSet.addBoolean( "includeDependencies", params.isIncludeDependencies() );

        addContents( resultSet, result.getPushedContents(), "pushedContents" );
        addContents( resultSet, result.getDeletedContents(), "deletedContents" );
        addContents( resultSet, result.getFailedContents(), "failedContents" );

        log( "system.content.publish", data, ContentIds.create().
            addAll( result.getPushedContents() ).
            addAll( result.getDeletedContents() ).
            build() );
    }

    public void unpublishContent( final UnpublishContentParams params, final UnpublishContentsResult result )
    {
        if ( executor != null )
        {
            executor.execute( () -> doUnpublishContent( params, result ) );
        }
    }

    private void doUnpublishContent( final UnpublishContentParams params, final UnpublishContentsResult result )
    {
        if ( params.getContentIds() == null )
        {
            return;
        }

        final PropertyTree data = new PropertyTree();
        final PropertySet paramsSet = data.addSet( "params" );
        final PropertySet resultSet = data.addSet( "result" );

        paramsSet.addStrings( "contentIds", params.getContentIds().stream().
            map( ContentId::toString ).collect( Collectors.toList() ) );
        paramsSet.addBoolean( "includeChildren", params.isIncludeChildren() );
        if ( params.getUnpublishBranch() != null )
        {
            paramsSet.addString( "unpublishBranch", params.getUnpublishBranch().getValue() );
        }

        addContents( resultSet, result.getUnpublishedContents(), "unpublishedContents" );

        log( "system.content.unpublishContent", data, result.getUnpublishedContents() );
    }

    public void duplicate( final DuplicateContentParams params, final DuplicateContentsResult result )
    {
        if ( executor != null )
        {
            executor.execute( () -> doDuplicate( params, result ) );
        }
    }

    private void doDuplicate( final DuplicateContentParams params, final DuplicateContentsResult result )
    {
        if ( params.getContentId() == null )
        {
            return;
        }

        final PropertyTree data = new PropertyTree();
        final PropertySet paramsSet = data.addSet( "params" );
        final PropertySet resultSet = data.addSet( "result" );

        paramsSet.addString( "contentId", params.getContentId().toString() );
        paramsSet.addBoolean( "includeChildren", params.getIncludeChildren() );
        if ( params.getCreator() != null )
        {
            paramsSet.addString( "creator", params.getCreator().getId() );
        }

        resultSet.addStrings( "duplicatedContents",
                              result.getDuplicatedContents().stream().map( ContentId::toString ).collect( Collectors.toSet() ) );

        log( "system.content.duplicate", data, result.getDuplicatedContents() );
    }

    public void move( final MoveContentParams params, MoveContentsResult result )
    {
        if ( executor != null )
        {
            executor.execute( () -> doMove( params, result ) );
        }
    }

    private void doMove( final MoveContentParams params, MoveContentsResult result )
    {
        final PropertyTree data = new PropertyTree();
        final PropertySet paramsSet = data.addSet( "params" );
        final PropertySet resultSet = data.addSet( "result" );

        paramsSet.addString( "contentId", nullToNull( params.getContentId() ) );
        paramsSet.addString( "parentContentPath", nullToNull( params.getParentContentPath() ) );
        if ( params.getCreator() != null )
        {
            paramsSet.addString( "creator", params.getCreator().getId() );
        }

        addContents( resultSet, result.getMovedContents(), "movedContents" );

        log( "system.content.move", data, params.getContentId() );
    }

    public void rename( final RenameContentParams params, final Content content )
    {
        if ( executor != null )
        {
            executor.execute( () -> doRename( params, content ) );
        }

    }

    private void doRename( final RenameContentParams params, final Content content )
    {
        final PropertyTree data = new PropertyTree();
        final PropertySet paramsSet = data.addSet( "params" );
        final PropertySet resultSet = data.addSet( "result" );

        paramsSet.addString( "contentId", nullToNull( params.getContentId() ) );
        paramsSet.addString( "newName", nullToNull( params.getNewName() ) );

        addContent( resultSet, content );

        log( "system.content.rename", data, content.getId() );
    }

    public void setActiveContentVersion( final ContentId contentId, final ContentVersionId versionId )
    {
        if ( executor != null )
        {
            executor.execute( () -> doSetActiveContentVersion( contentId, versionId ) );
        }
    }

    private void doSetActiveContentVersion( final ContentId contentId, final ContentVersionId versionId )
    {
        final PropertyTree data = new PropertyTree();
        final PropertySet paramsSet = data.addSet( "params" );
        final PropertySet resultSet = data.addSet( "result" );

        paramsSet.addString( "contentId", nullToNull( contentId ) );
        paramsSet.addString( "versionId", nullToNull( versionId ) );

        resultSet.addString( "contentId", nullToNull( contentId ) );
        resultSet.addString( "versionId", nullToNull( versionId ) );

        log( "system.content.setActiveContentVersion", data, contentId );
    }

    public void setChildOrder( final SetContentChildOrderParams params, final Content content )
    {
        if ( executor != null )
        {
            executor.execute( () -> doSetChildOrder( params, content ) );
        }
    }

    private void doSetChildOrder( final SetContentChildOrderParams params, final Content content )
    {
        final PropertyTree data = new PropertyTree();
        final PropertySet paramsSet = data.addSet( "params" );
        final PropertySet resultSet = data.addSet( "result" );

        paramsSet.addString( "contentId", nullToNull( params.getContentId() ) );
        paramsSet.addString( "childOrder", nullToNull( params.getChildOrder() ) );

        addContent( resultSet, content );

        log( "system.content.setChildOrder", data, content.getId() );
    }

    public void reorderChildren( final ReorderChildContentsParams params, final ReorderChildContentsResult result )
    {
        if ( executor != null )
        {
            executor.execute( () -> doReorderChildren( params, result ) );
        }

    }

    private void doReorderChildren( final ReorderChildContentsParams params, final ReorderChildContentsResult result )
    {
        final PropertyTree data = new PropertyTree();
        final PropertySet paramsSet = data.addSet( "params" );
        final PropertySet resultSet = data.addSet( "result" );

        paramsSet.addString( "contentId", nullToNull( params.getContentId() ) );
        paramsSet.addBoolean( "silent", params.isSilent() );

        resultSet.addLong( "size", (long) result.getMovedChildren() );

        log( "system.content.reorderChildren", data, params.getContentId() );
    }

    public void applyPermissions( final ApplyContentPermissionsParams params, final ApplyContentPermissionsResult result )
    {
        if ( executor != null )
        {
            executor.execute( () -> doApplyPermissions( params, result ) );
        }
    }

    private void doApplyPermissions( final ApplyContentPermissionsParams params, final ApplyContentPermissionsResult result )
    {
        final PropertyTree data = new PropertyTree();
        final PropertySet paramsSet = data.addSet( "params" );
        final PropertySet resultSet = data.addSet( "result" );

        paramsSet.addString( "contentId", nullToNull( params.getContentId() ) );
        paramsSet.addBoolean( "inheritPermissions", params.isInheritPermissions() );
        paramsSet.addBoolean( "overwriteChildPermissions", params.isOverwriteChildPermissions() );

        if ( params.getPermissions() != null )
        {
            paramsSet.addStrings( "permissions", params.getPermissions().getEntries().stream().
                map( AccessControlEntry::toString ).collect( Collectors.toList() ) );
        }

        addContents( resultSet, result.getSkippedContents(), "skippedContents" );
        addContents( resultSet, result.getSucceedContents(), "succeedContents" );

        log( "system.content.applyPermissions", data, result.getSucceedContents() );
    }

    public void reprocess( final Content content )
    {
        if ( executor != null )
        {
            executor.execute( () -> doReprocess( content ) );
        }
    }

    private void doReprocess( final Content content )
    {
        final ContentId contentId = content.getId();

        final PropertyTree data = new PropertyTree();
        final PropertySet paramsSet = data.addSet( "params" );
        final PropertySet resultSet = data.addSet( "result" );

        paramsSet.addString( "contentId", nullToNull( contentId ) );

        addContent( resultSet, content );

        log( "system.content.reprocess", data, contentId );
    }

    private void addContent( final PropertySet targetSet, final Content content )
    {
        targetSet.setString( "id", content.getId().toString() );
        targetSet.setString( "path", content.getPath().toString() );
    }

    private void addContents( final PropertySet targetSet, final Contents contents, final String name )
    {
        contents.stream().map( content -> {
            final PropertySet contentSet = new PropertySet();
            this.addContent( contentSet, content );

            return contentSet;
        } ).
            forEach( contentSet -> targetSet.addSet( name, contentSet ) );
    }

    private void addContents( final PropertySet targetSet, final ContentIds contents, final String name )
    {
        targetSet.addStrings( name, contents.stream().
            map( ContentId::toString ).
            collect( Collectors.toSet() ) );
    }

    private void addContents( final PropertySet targetSet, final ContentPaths contents, final String name )
    {
        targetSet.addStrings( name, contents.stream().
            map( ContentPath::toString ).
            collect( Collectors.toSet() ) );
    }

    private void log( final String type, final PropertyTree data, final ContentPaths contentPaths )
    {
        log( type, data, AuditLogUris.from( contentPaths.
            stream().
            map( this::createAuditLogUri ).
            collect( Collectors.toList() ) ) );
    }

    private void log( final String type, final PropertyTree data, final ContentIds contentIds )
    {
        log( type, data, AuditLogUris.from( contentIds.
            stream().
            map( this::createAuditLogUri ).
            collect( Collectors.toList() ) ) );
    }

    private void log( final String type, final PropertyTree data, final AuditLogUris uris )
    {
        final LogAuditLogParams logParams = LogAuditLogParams.create().
            type( type ).
            source( SOURCE_CORE_CONTENT ).
            data( data ).
            objectUris( uris ).
            build();

        runAsAuditLog( () -> auditLogService.log( logParams ) );
    }

    private void log( final String type, final PropertyTree data, final ContentId contentId )
    {
        log( type, data, ContentIds.from( contentId ) );
    }

    private void log( final String type, final PropertyTree data, final ContentPath contentPath )
    {
        log( type, data, ContentPaths.from( contentPath ) );
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

    private <T> T runAsAuditLog( final Callable<T> callable )
    {
        final Context context = ContextAccessor.current();
        return ContextBuilder.from( ContextAccessor.current() ).
            authInfo( AuthenticationInfo.copyOf( context.getAuthInfo() ).
                principals( RoleKeys.AUDIT_LOG ).build() ).
            build().
            callWith( callable );
    }

    private String generateNameFromParams( final ContentName contentName, final String displayName )
    {
        if ( contentName == null || isNullOrEmpty( contentName.toString() ) )
        {
            if ( isNullOrEmpty( displayName ) )
            {
                return ContentName.unnamed().toString();
            }
            else
            {
                return NamePrettyfier.create( displayName );
            }
        }
        return contentName.toString();
    }
}
