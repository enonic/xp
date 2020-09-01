package com.enonic.xp.core.impl.project;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.AttachmentNames;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.content.Media;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.content.SetContentChildOrderParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.icon.Thumbnail;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.schema.content.ContentTypeFromMimeTypeResolver;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfigsDataSerializer;

public class ParentProjectSynchronizer
{
    private final static Logger LOG = LoggerFactory.getLogger( ParentProjectSynchronizer.class );

    private final ContentService contentService;

    private final MediaInfoService mediaInfoService;

    private final Context targetContext;

    private final Context sourceContext;

    private ParentProjectSynchronizer( final Builder builder )
    {
        this.contentService = builder.contentService;
        this.mediaInfoService = builder.mediaInfoService;
        final Project sourceProject = builder.sourceProject;
        final Project targetProject = builder.targetProject;

        this.targetContext = ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( targetProject.getName().getRepoId() ).
            branch( ContentConstants.BRANCH_DRAFT ).
            build();

        this.sourceContext = ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( sourceProject.getName().getRepoId() ).
            branch( ContentConstants.BRANCH_DRAFT ).
            build();
    }

    public static ParentProjectSynchronizer.Builder create()
    {
        return new Builder();
    }

    public void syncRoot()
    {
        final Content root = sourceContext.callWith( () -> contentService.getByPath( ContentPath.ROOT ) );
        this.syncWithChildren( root.getId() );
    }

    public void syncWithChildren( final ContentId contentId )
    {
        final Queue<ContentId> queue = new LinkedList( List.of( contentId ) );

        sourceContext.runWith( () -> {

            if ( contentService.contentExists( contentId ) )
            {
                final Content contentToSync = contentService.getById( contentId );
                if ( !( "/content".equals( contentToSync.getPath().toString() ) || "/".equals( contentToSync.getPath().toString() ) ) )
                {
                    this.doSync( contentService.getById( contentId ) );
                }
            }
            while ( queue.size() > 0 )
            {
                final FindContentByParentResult result = contentService.findByParent( FindContentByParentParams.create().
                    parentId( queue.poll() ).
                    recursive( false ).
                    childOrder( ChildOrder.path() ).
                    size( -1 ).
                    build() );

                for ( final Content content : result.getContents() )
                {
                    final Content synchedContent = this.doSync( content );

                    if ( synchedContent != null )
                    {
                        if ( content.hasChildren() )
                        {
                            queue.offer( content.getId() );
                        }
                    }
                }
            }
        } );
    }

    private Content doSync( final Content sourceContent )
    {
        return targetContext.callWith( () -> {

            if ( contentService.contentExists( sourceContent.getId() ) )
            {
                Content targetContent = contentService.getById( sourceContent.getId() );

                if ( isDataSyncable( sourceContent, targetContent ) )
                {
                    targetContent = this.doSyncMoved( sourceContent, targetContent );
                    targetContent = this.doSyncRenamed( sourceContent, targetContent );
                    targetContent = this.doSyncSorted( sourceContent, targetContent );
                    return this.doSyncUpdated( sourceContent, targetContent );
                }

                return contentService.getById( sourceContent.getId() );
            }
            else
            {
                return this.doSyncCreated( sourceContent );
            }
        } );
    }

    public Content syncRenamed( final ContentId contentId )
    {
        return sourceContext.callWith( () -> {
            final Content sourceContent = contentService.getById( contentId );

            return targetContext.callWith( () -> {
                final Content targetContent = contentService.getById( contentId );
                return doSyncRenamed( sourceContent, targetContent );
            } );
        } );

    }

    private Content doSyncRenamed( final Content sourceContent, final Content targetContent )
    {
        if ( isToSyncName( targetContent ) )
        {
            if ( needToRename( sourceContent, targetContent ) )
            {
                return contentService.rename( RenameContentParams.create().
                    contentId( targetContent.getId() ).
                    newName( sourceContent.getName() ).
                    stopInherit( false ).
                    build() );
            }
        }
        return targetContent;
    }

    public Content syncMoved( final ContentId contentId )
    {
        return sourceContext.callWith( () -> {
            final Content sourceContent = contentService.getById( contentId );

            return targetContext.callWith( () -> {
                final Content targetContent = contentService.getById( contentId );
                return doSyncMoved( sourceContent, targetContent );
            } );
        } );

    }

    private Content doSyncMoved( final Content sourceContent, final Content targetContent )
    {
        if ( isToSyncParent( targetContent ) )
        {
            final Content sourceParent = sourceContext.callWith( () -> contentService.getByPath( sourceContent.getParentPath() ) );
            final ContentPath targetParentPath = targetContext.callWith( () -> contentService.contentExists( sourceParent.getId() )
                ? contentService.getById( sourceParent.getId() ).getPath()
                : ( "/content".equals( sourceParent.getPath().toString() ) || "/".equals( sourceParent.getPath().toString() ) )
                    ? ContentPath.ROOT
                    : null );

            if ( targetParentPath != null )
            {
                if ( !targetParentPath.equals( targetContent.getParentPath() ) )
                {
                    final MoveContentParams moveContentParams = MoveContentParams.create().
                        contentId( targetContent.getId() ).
                        parentContentPath( targetParentPath ).
                        stopInherit( false ).
                        build();

                    contentService.move( moveContentParams );

                    return contentService.getById( targetContent.getId() );
                }
            }
        }
        return targetContent;
    }

    public Content syncUpdated( final ContentId contentId )
    {
        return sourceContext.callWith( () -> {
            final Content sourceContent = contentService.getById( contentId );

            return targetContext.callWith( () -> {
                if ( contentService.contentExists( contentId ) )
                {
                    final Content targetContent = contentService.getById( contentId );
                    if ( isDataSyncable( sourceContent, targetContent ) )
                    {
                        return doSyncUpdated( sourceContent, targetContent );
                    }
                }
                return null;
            } );
        } );
    }

    private Content doSyncUpdated( final Content sourceContent, final Content targetContent )
    {
        if ( isToSyncData( targetContent ) )
        {
            if ( needToUpdate( sourceContent, targetContent ) )
            {
                final UpdateContentParams params = updateParams( sourceContent );

                doSyncMedia( sourceContent, params );
                doSyncThumbnail( sourceContent, targetContent, params );

                return contentService.update( params );
            }
        }
        return targetContent;
    }

    public Content syncSorted( final ContentId contentId )
    {
        return sourceContext.callWith( () -> {
            final Content sourceContent = contentService.getById( contentId );

            return targetContext.callWith( () -> {
                if ( contentService.contentExists( contentId ) )
                {
                    final Content targetContent = contentService.getById( contentId );
                    if ( isDataSyncable( sourceContent, targetContent ) )
                    {
                        return doSyncSorted( sourceContent, targetContent );
                    }
                }
                return null;
            } );
        } );
    }

    private Content doSyncSorted( final Content sourceContent, final Content targetContent )
    {
        if ( isToSyncSort( targetContent ) )
        {
            if ( needToSort( sourceContent, targetContent ) )
            {
                final SetContentChildOrderParams sortParams = SetContentChildOrderParams.create().
                    childOrder( sourceContent.getChildOrder() ).
                    contentId( sourceContent.getId() ).
                    stopInherit( false ).
                    build();

                return contentService.setChildOrder( sortParams );
            }
        }
        return targetContent;
    }

    public Content syncCreated( final ContentId contentId )
    {
        return sourceContext.callWith( () -> {
            final Content sourceContent = contentService.getById( contentId );
            return targetContext.callWith( () -> {
                if ( contentService.contentExists( contentId ) )
                {
                    return contentService.getById( contentId );
                }
                else
                {
                    return doSyncCreated( sourceContent );
                }
            } );
        } );
    }

    private Content doSyncCreated( final Content sourceContent )
    {
        try
        {
            return sourceContext.callWith( () -> {
                if ( contentService.contentExists( sourceContent.getParentPath() ) )
                {
                    final ContentId parentId = contentService.getByPath( sourceContent.getParentPath() ).getId();
                    return targetContext.callWith( () -> {

                        if ( sourceContent.getParentPath().isRoot() )
                        {
                            return contentService.create( sourceContext.callWith( () -> createParams( sourceContent, ContentPath.ROOT ) ) );
                        }

                        if ( contentService.contentExists( parentId ) )
                        {
                            final Content targetParent = contentService.getById( parentId );
                            return contentService.create(
                                sourceContext.callWith( () -> createParams( sourceContent, targetParent.getPath() ) ) );
                        }

                        return null;
                    } );
                }
                return null;
            } );

        }
        catch ( ContentAlreadyExistsException e )
        {
            LOG.warn( "content [{}] already exists.", sourceContent.getId() );
        }
        return null;
    }

    private boolean isDataSyncable( final Content sourceContent, final Content targetContent )
    {
        if ( WorkflowState.READY.equals( targetContent.getWorkflowInfo().getState() ) )
        {
            return WorkflowState.READY.equals( sourceContent.getWorkflowInfo().getState() );
        }
        return true;
    }

    private boolean isToSyncData( final Content targetContent )
    {
        return targetContent.getInherit().contains( ContentInheritType.CONTENT );
    }

    private boolean isToSyncName( final Content targetContent )
    {
        return targetContent.getInherit().contains( ContentInheritType.NAME );
    }

    private boolean isToSyncParent( final Content targetContent )
    {
        return targetContent.getInherit().contains( ContentInheritType.PARENT );
    }

    private boolean isToSyncSort( final Content targetContent )
    {
        return targetContent.getInherit().contains( ContentInheritType.SORT );
    }

    private boolean needToUpdate( final Content sourceContent, final Content targetContent )
    {
        return !Objects.equals( sourceContent.getData(), targetContent.getData() ) ||
            !Objects.equals( sourceContent.getAllExtraData(), targetContent.getAllExtraData() ) ||
            !Objects.equals( sourceContent.getDisplayName(), targetContent.getDisplayName() ) ||
            !Objects.equals( sourceContent.getOwner(), targetContent.getOwner() ) ||
            !Objects.equals( sourceContent.getLanguage(), targetContent.getLanguage() ) ||
            !Objects.equals( sourceContent.getWorkflowInfo(), targetContent.getWorkflowInfo() ) ||
            !Objects.equals( sourceContent.getPage(), targetContent.getPage() ) ||
            !Objects.equals( sourceContent.getThumbnail(), targetContent.getThumbnail() ) ||
            !Objects.equals( sourceContent.getProcessedReferences(), targetContent.getProcessedReferences() ) ||
            sourceContent.inheritsPermissions() != targetContent.inheritsPermissions() ||
            sourceContent.isValid() != targetContent.isValid();
    }

    private boolean needToSort( final Content sourceContent, final Content targetContent )
    {
        return !Objects.equals( sourceContent.getChildOrder(), targetContent.getChildOrder() );
    }

    private boolean needToRename( final Content sourceContent, final Content targetContent )
    {
        return !targetContent.getName().equals( sourceContent.getName() );
    }

    private UpdateContentParams updateParams( final Content source )
    {
        return new UpdateContentParams().
            contentId( source.getId() ).
            modifier( PrincipalKey.ofAnonymous() ).
            requireValid( false ).
            stopInherit( false ).
            editor( edit -> {
                edit.data = source.getData();
                edit.extraDatas = source.getAllExtraData();
                edit.displayName = source.getDisplayName();
                edit.owner = source.getOwner();
                edit.language = source.getLanguage();
                edit.inheritPermissions = source.inheritsPermissions();
                edit.permissions = source.getPermissions();
                edit.workflowInfo = source.getWorkflowInfo();
                edit.page = source.getPage();
                edit.thumbnail = source.getThumbnail();
                edit.valid = source.isValid();
                edit.processedReferences = ContentIds.create().addAll( source.getProcessedReferences() );
            } );
    }

    private CreateContentParams createParams( final Content source, final ContentPath targetParentPath )
    {
        final CreateContentParams.Builder builder = CreateContentParams.create();

        builder.contentId( source.getId() ).
            contentData( source.getData().copy() ).
            extraDatas( source.getAllExtraData().copy() ).
            type( source.getType() ).
            owner( source.getOwner() ).
            displayName( source.getDisplayName() ).
            name( source.getName() ).
            parent( targetParentPath ).
            page( source.getPage() ).
            requireValid( false ).
            createSiteTemplateFolder( false ).
            inheritPermissions( true ).
            inherit( Set.of( ContentInheritType.CONTENT, ContentInheritType.PARENT, ContentInheritType.NAME, ContentInheritType.SORT ) ).
            originProject( ProjectName.from( sourceContext.getRepositoryId() ) ).
            createAttachments( CreateAttachments.from( source.getAttachments().
                stream().
                map( attachment -> {
                    final ByteSource binary = contentService.getBinary( source.getId(), attachment.getBinaryReference() );

                    return CreateAttachment.create().
                        name( attachment.getName() ).
                        label( attachment.getLabel() ).
                        mimeType( attachment.getMimeType() ).
                        text( attachment.getTextContent() ).
                        byteSource( binary ).
                        build();
                } ).collect( Collectors.toSet() ) ) ).
            childOrder( source.getChildOrder() ).
            language( source.getLanguage() ).
            useParentLanguage( false ).
            workflowInfo( source.getWorkflowInfo() );

        if ( source instanceof Site )
        {
            final Site site = (Site) source;

            final PropertyTree data = source.getData().copy();
            data.setString( "description", site.getDescription() );

            new SiteConfigsDataSerializer().toProperties( site.getSiteConfigs(), data.getRoot() );
        }

        return builder.build();
    }

    private void doSyncThumbnail( final Content sourceContent, final Content targetContent, final UpdateContentParams params )
    {
        if ( sourceContent.hasThumbnail() && !sourceContent.getThumbnail().equals( targetContent.getThumbnail() ) )
        {
            final Thumbnail sourceThumbnail = sourceContent.getThumbnail();

            final ByteSource sourceBinary =
                sourceContext.callWith( () -> contentService.getBinary( sourceContent.getId(), sourceThumbnail.getBinaryReference() ) );

            final CreateAttachment createThumbnail = CreateAttachment.create().
                name( AttachmentNames.THUMBNAIL ).
                mimeType( sourceThumbnail.getMimeType() ).
                byteSource( sourceBinary ).
                build();

            final CreateAttachments.Builder createAttachments = CreateAttachments.create().add( createThumbnail );
            if ( params.getCreateAttachments() != null )
            {
                createAttachments.add( params.getCreateAttachments() );
            }

            params.createAttachments( createAttachments.build() );
        }
    }

    private void doSyncMedia( final Content sourceContent, final UpdateContentParams params )
    {
        if ( sourceContent instanceof Media )
        {
            final Media sourceMedia = (Media) sourceContent;

            final Attachment mediaAttachment = sourceMedia.getMediaAttachment();

            final ByteSource sourceBinary =
                sourceContext.callWith( () -> contentService.getBinary( sourceMedia.getId(), mediaAttachment.getBinaryReference() ) );
            final MediaInfo mediaInfo = sourceContext.callWith( () -> mediaInfoService.parseMediaInfo( sourceBinary ) );

            final ContentTypeName type = ContentTypeFromMimeTypeResolver.resolve( mediaAttachment.getMimeType() );

            final CreateAttachment createAttachment = CreateAttachment.create().
                name( mediaAttachment.getName() ).
                mimeType( mediaAttachment.getMimeType() ).
                label( "source" ).
                byteSource( sourceBinary ).
                text( type != null && type.isTextualMedia() ? mediaInfo.getTextContent() : null ).
                build();

            params.clearAttachments( true ).
                createAttachments( CreateAttachments.from( createAttachment ) );

        }


    }

    public static final class Builder
    {
        private ContentService contentService;

        private MediaInfoService mediaInfoService;

        private Project targetProject;

        private Project sourceProject;

        private Builder()
        {
        }

        private void validate()
        {
            Preconditions.checkNotNull( contentService, "contentService must be set." );
            Preconditions.checkNotNull( mediaInfoService, "mediaInfoService must be set." );
            Preconditions.checkNotNull( sourceProject, "sourceProject must be set." );
            Preconditions.checkNotNull( targetProject, "targetProject must be set." );
            Preconditions.checkNotNull( targetProject.getParent(),
                                        "target project [" + targetProject.getName() + "] has no parent to import from." );

            if ( !sourceProject.getName().equals( targetProject.getParent() ) )
            {
                throw new IllegalArgumentException(
                    "[" + sourceProject.getName() + "] is not a parent project for [" + targetProject.getName() + "]." );
            }
        }

        public Builder contentService( final ContentService contentService )
        {
            this.contentService = contentService;
            return this;
        }

        public Builder mediaInfoService( final MediaInfoService mediaInfoService )
        {
            this.mediaInfoService = mediaInfoService;
            return this;
        }

        public Builder targetProject( final Project targetProject )
        {
            this.targetProject = targetProject;
            return this;
        }

        public Builder sourceProject( final Project sourceProject )
        {
            this.sourceProject = sourceProject;
            return this;
        }

        public ParentProjectSynchronizer build()
        {
            validate();
            return new ParentProjectSynchronizer( this );
        }
    }
}
