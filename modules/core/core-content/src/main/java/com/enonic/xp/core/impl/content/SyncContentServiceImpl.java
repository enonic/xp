package com.enonic.xp.core.impl.content;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.ResetContentInheritParams;
import com.enonic.xp.content.SyncContentService;
import com.enonic.xp.core.impl.content.serializer.ContentDataSerializer;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.schema.content.ContentTypeService;

@Component
public class SyncContentServiceImpl
    implements SyncContentService
{
    private ContentService contentService;

    private ContentTypeService contentTypeService;

    private NodeService nodeService;

    private EventPublisher eventPublisher;

    private ContentNodeTranslator translator;

    private ProjectService projectService;

    private ContentSynchronizer contentSynchronizer;

    @Activate
    public SyncContentServiceImpl( @Reference final ContentTypeService contentTypeService, @Reference final NodeService nodeService,
                                   @Reference final EventPublisher eventPublisher,
                                   @Reference final PageDescriptorService pageDescriptorService,
                                   @Reference final PartDescriptorService partDescriptorService,
                                   @Reference final LayoutDescriptorService layoutDescriptorService,
                                   @Reference final ProjectService projectService, @Reference final ContentService contentService,
                                   @Reference final ContentSynchronizer contentSynchronizer )
    {
        this.contentTypeService = contentTypeService;
        this.nodeService = nodeService;
        this.eventPublisher = eventPublisher;
        this.projectService = projectService;
        this.contentService = contentService;
        this.contentSynchronizer = contentSynchronizer;

        final ContentDataSerializer contentDataSerializer = ContentDataSerializer.create().
            layoutDescriptorService( layoutDescriptorService ).
            pageDescriptorService( pageDescriptorService ).
            partDescriptorService( partDescriptorService ).
            build();

        this.translator = new ContentNodeTranslator( nodeService, contentDataSerializer );
    }

    @Override
    public void restoreInheritance( final ResetContentInheritParams params )
    {
        ResetContentInheritanceCommand.create( params ).
            contentService( contentService ).
            projectService( projectService ).
            nodeService( nodeService ).
            contentTypeService( contentTypeService ).
            eventPublisher( eventPublisher ).
            translator( translator ).
            contentSynchronizer( contentSynchronizer ).
            build().
            execute();
    }
}
