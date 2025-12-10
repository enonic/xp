package com.enonic.xp.core.impl.content;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.ProjectSyncParams;
import com.enonic.xp.content.ResetContentInheritParams;
import com.enonic.xp.content.SyncContentService;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.schema.content.ContentTypeService;

@Component
public class SyncContentServiceImpl
    implements SyncContentService
{
    private final ContentService contentService;

    private final ContentTypeService contentTypeService;

    private final NodeService nodeService;

    private final EventPublisher eventPublisher;

    private final ProjectService projectService;

    private final ContentSynchronizer contentSynchronizer;

    private final ContentAuditLogSupport contentAuditLogSupport;

    @Activate
    public SyncContentServiceImpl( @Reference final ContentTypeService contentTypeService, @Reference final NodeService nodeService,
                                   @Reference final EventPublisher eventPublisher,
                                   @Reference final ProjectService projectService, @Reference final ContentService contentService,
                                   @Reference final ContentSynchronizer contentSynchronizer,
                                   @Reference final ContentAuditLogSupport contentAuditLogSupport )
    {
        this.contentTypeService = contentTypeService;
        this.nodeService = nodeService;
        this.eventPublisher = eventPublisher;
        this.projectService = projectService;
        this.contentService = contentService;
        this.contentSynchronizer = contentSynchronizer;
        this.contentAuditLogSupport = contentAuditLogSupport;
    }

    @Override
    public void resetInheritance( final ResetContentInheritParams params )
    {
        ResetContentInheritanceCommand.create( params ).
            contentService( contentService ).
            projectService( projectService ).
            nodeService( nodeService ).
            contentTypeService( contentTypeService ).
            eventPublisher( eventPublisher ).
            contentSynchronizer( contentSynchronizer ).
            build().
            execute();

        contentAuditLogSupport.resetInheritance( params );
    }

    @Override
    public void syncProject( final ProjectSyncParams params )
    {
        ProjectSyncCommand.create( params ).
            contentSynchronizer( contentSynchronizer ).
            projectService( projectService ).
            build().
            execute();

        contentAuditLogSupport.syncProject( params );
    }
}
