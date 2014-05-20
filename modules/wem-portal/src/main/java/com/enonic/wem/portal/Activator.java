package com.enonic.wem.portal;

import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.attachment.AttachmentService;
import com.enonic.wem.api.content.page.PageComponentService;
import com.enonic.wem.api.content.page.PageDescriptorService;
import com.enonic.wem.api.content.page.PageTemplateService;
import com.enonic.wem.api.content.page.image.ImageDescriptorService;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorService;
import com.enonic.wem.api.content.page.part.PartDescriptorService;
import com.enonic.wem.api.content.site.SiteService;
import com.enonic.wem.api.content.site.SiteTemplateService;
import com.enonic.wem.core.image.filter.ImageFilterBuilder;
import com.enonic.wem.core.module.ModuleKeyResolverService;
import com.enonic.wem.core.module.ModuleResourcePathResolver;
import com.enonic.wem.guice.GuiceActivator;

public final class Activator
    extends GuiceActivator
{
    @Override
    protected void configure()
    {
        install( new PortalModule() );

        service( PageComponentService.class ).importSingle();
        service( ModuleResourcePathResolver.class ).importSingle();
        service( ImageDescriptorService.class ).importSingle();
        service( LayoutDescriptorService.class ).importSingle();
        service( PartDescriptorService.class ).importSingle();
        service( ContentService.class ).importSingle();
        service( PageDescriptorService.class ).importSingle();
        service( PageTemplateService.class ).importSingle();
        service( SiteService.class ).importSingle();
        service( SiteTemplateService.class ).importSingle();
        service( AttachmentService.class ).importSingle();
        service( BlobService.class ).importSingle();
        service( ImageFilterBuilder.class ).importSingle();
        service( ModuleKeyResolverService.class ).importSingle();
    }
}
