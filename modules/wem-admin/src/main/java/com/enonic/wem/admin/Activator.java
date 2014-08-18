package com.enonic.wem.admin;

import javax.inject.Inject;
import javax.servlet.Servlet;

import com.enonic.wem.admin.app.MainServlet;
import com.enonic.wem.admin.app.ResourceLocator;
import com.enonic.wem.admin.event.EventListenerImpl;
import com.enonic.wem.admin.event.EventServlet;
import com.enonic.wem.admin.rest.RestServlet;
import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.attachment.AttachmentService;
import com.enonic.wem.api.content.page.PageDescriptorService;
import com.enonic.wem.api.content.page.PageService;
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.api.content.page.image.ImageDescriptorService;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorService;
import com.enonic.wem.api.content.page.part.PartDescriptorService;
import com.enonic.wem.api.content.site.SiteService;
import com.enonic.wem.api.content.site.SiteTemplateService;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.relationship.RelationshipService;
import com.enonic.wem.api.schema.SchemaService;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.api.schema.relationship.RelationshipTypeService;
import com.enonic.wem.core.config.ConfigProperties;
import com.enonic.wem.core.initializer.StartupInitializer;
import com.enonic.wem.guice.GuiceActivator;

public final class Activator
    extends GuiceActivator
{
    @Inject
    protected ResourceLocator resourceLocator;

    @Override
    protected void configure()
    {
        configPid( "com.enonic.wem.admin" );
        install( new AdminModule() );

        // Import services
        service( ConfigProperties.class ).importSingle();
        service( BlobService.class ).importSingle();
        service( AttachmentService.class ).importSingle();
        service( ContentService.class ).importSingle();
        service( ContentTypeService.class ).importSingle();
        service( RelationshipTypeService.class ).importSingle();
        service( PageService.class ).importSingle();
        service( SiteService.class ).importSingle();
        service( RelationshipService.class ).importSingle();
        service( MixinService.class ).importSingle();
        service( SchemaService.class ).importSingle();
        service( ModuleService.class ).importSingle();
        service( SiteTemplateService.class ).importSingle();
        service( PageDescriptorService.class ).importSingle();
        service( PageTemplateService.class ).importSingle();
        service( ImageDescriptorService.class ).importSingle();
        service( LayoutDescriptorService.class ).importSingle();
        service( PartDescriptorService.class ).importSingle();
        service( StartupInitializer.class ).importSingle();

        // Export services
        service( MainServlet.class ).attribute( "alias", "/*" ).exportAs( Servlet.class );
        service( RestServlet.class ).attribute( "alias", "/admin/rest/*" ).
            attribute( "init.resteasy.servlet.mapping.prefix", "/admin/rest" ).exportAs( Servlet.class );

        service( EventServlet.class ).attribute( "alias", "/admin/event/*" ).exportAs( Servlet.class );

        service( EventListenerImpl.class ).export( );
    }

    @Override
    protected void doStart()
        throws Exception
    {
        this.resourceLocator.start();
    }

    @Override
    protected void doStop()
        throws Exception
    {
        this.resourceLocator.stop();
    }
}
