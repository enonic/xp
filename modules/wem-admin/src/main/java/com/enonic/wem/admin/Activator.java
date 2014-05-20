package com.enonic.wem.admin;

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
import com.enonic.wem.guice.GuiceActivator;

public final class Activator
    extends GuiceActivator
{
    @Override
    protected void configure()
    {
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

        // Export services
        service( ResourceServlet.class ).attribute( "alias", "/" ).export();
    }
}
