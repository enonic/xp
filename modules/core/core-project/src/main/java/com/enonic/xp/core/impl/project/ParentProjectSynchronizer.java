package com.enonic.xp.core.impl.project;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.CompareContentParams;
import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.project.Project;
import com.enonic.xp.security.PrincipalKey;

public class ParentProjectSynchronizer
{
    private final static Logger LOG = LoggerFactory.getLogger( ParentProjectSynchronizer.class );

    private final ContentService contentService;

    private final Context targetContext;

    private final Context sourceContext;

    private ParentProjectSynchronizer( final Builder builder )
    {
        this.contentService = builder.contentService;
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

    public void syncWithChildren( final ContentPath contentPath )
    {
        final Queue<ContentPath> queue = new LinkedList( List.of( contentPath ) );

        sourceContext.runWith( () -> {
            while ( queue.size() > 0 )
            {
                final FindContentByParentResult result = contentService.findByParent( FindContentByParentParams.create().
                    parentPath( queue.poll() ).
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
                            queue.offer( content.getPath() );
                        }
                    }
                }
            }
        } );
    }

    public void sync( final ContentId contentId )
    {
        this.doSync( sourceContext.callWith( () -> this.contentService.getById( contentId ) ) );
    }

    private Content doSync( final Content sourceContent )
    {
        return sourceContext.callWith( () -> {

            if ( isToSync( sourceContent ) )
            {
                return targetContext.callWith( () -> {

                    if ( contentService.contentExists( sourceContent.getId() ) )
                    {
                        final Content targetContent = contentService.getById( sourceContent.getId() );

                        this.doSyncRenamed( sourceContent, targetContent );
                        return this.doSyncUpdated( sourceContent, targetContent );
                    }

                    return this.doSyncCreated( sourceContent );
                } );
            }
            return null;
        } );
    }

    public Content syncRenamed( final ContentId contentId )
    {
        return sourceContext.callWith( () -> {
            final Content sourceContent = contentService.getById( contentId );

            if ( isToSync( sourceContent ) )
            {
                return targetContext.callWith( () -> {
                    final Content targetContent = contentService.getById( contentId );
                    return doSyncRenamed( sourceContent, targetContent );
                } );
            }
            return null;
        } );

    }

    private Content doSyncRenamed( final Content sourceContent, final Content targetContent )
    {
        if ( targetContent.isInherited() )
        {
            if ( !targetContent.getName().equals( sourceContent.getName() ) )
            {
                return contentService.rename( RenameContentParams.create().
                    contentId( targetContent.getId() ).
                    newName( sourceContent.getName() ).
                    build() );
            }
        }
        return null;
    }

    public Content syncUpdated( final ContentId contentId )
    {
        return sourceContext.callWith( () -> {
            final Content sourceContent = contentService.getById( contentId );

            if ( isToSync( sourceContent ) )
            {
                return targetContext.callWith( () -> {
                    final Content targetContent = contentService.getById( contentId );

                    doSyncRenamed( sourceContent, targetContent );
                    return doSyncUpdated( sourceContent, targetContent );
                } );
            }
            return null;
        } );
    }

    private Content doSyncUpdated( final Content sourceContent, final Content targetContent )
    {
        if ( targetContent.isInherited() )
        {
            if ( !sourceContent.equals( targetContent ) )
            {
                final UpdateContentParams params = updateParams( sourceContent );
                return contentService.update( params );
            }
        }
        return null;
    }

    public Content syncCreated( final ContentId contentId )
    {
        return sourceContext.callWith( () -> {
            final Content sourceContent = contentService.getById( contentId );
            return targetContext.callWith( () -> doSyncCreated( sourceContent ) );
        } );
    }

    private Content doSyncCreated( final Content sourceContent )
    {
        final CreateContentParams params = createParams( sourceContent );
        try
        {
            return contentService.create( params );
        }
        catch ( ContentAlreadyExistsException e )
        {
            LOG.warn( "content [{}] already exists.", params.getContentId() );
        }
        return null;
    }

    private boolean isToSync( final Content content )
    {
        final CompareContentResult compareResult =
            contentService.compare( new CompareContentParams( content.getId(), ContentConstants.BRANCH_MASTER ) );

        return CompareStatus.NEW.equals( compareResult.getCompareStatus() ) ||
            CompareStatus.EQUAL.equals( compareResult.getCompareStatus() ) ||
            ( ( CompareStatus.NEWER.equals( compareResult.getCompareStatus() ) ||
                CompareStatus.MOVED.equals( compareResult.getCompareStatus() ) ) &&
                WorkflowState.READY.equals( content.getWorkflowInfo().getState() ) );
    }

    private UpdateContentParams updateParams( final Content source )
    {
        return new UpdateContentParams().
            requireValid( false ).
            contentId( source.getId() ).
            modifier( PrincipalKey.ofAnonymous() ).
            inherited( true ).
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

    private CreateContentParams createParams( final Content source )
    {
        final CreateContentParams.Builder builder = CreateContentParams.create();

        builder.contentId( source.getId() ).
            contentData( source.getData() ).
            extraDatas( source.getAllExtraData() ).
            type( source.getType() ).
            owner( source.getOwner() ).
            displayName( source.getDisplayName() ).
            name( source.getName() ).
            parent( source.getParentPath() ).
            requireValid( false ).
            createSiteTemplateFolder( false ).
            inheritPermissions( true ).
            inherited( true ).
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
            workflowInfo( source.getWorkflowInfo() );

        return builder.build();
    }

    public static final class Builder
    {
        private ContentService contentService;

        private Project targetProject;

        private Project sourceProject;

        private Builder()
        {
        }

        private void validate()
        {
            Preconditions.checkNotNull( contentService, "contentService must be set." );
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
