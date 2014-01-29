package com.enonic.wem.portal.content.page;


import com.enonic.wem.api.content.page.Descriptor;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.content.page.TemplateKey;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.api.content.page.layout.LayoutTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateKey;

import static com.enonic.wem.api.command.Commands.page;

public final class LayoutRenderer
    extends PageComponentRenderer
{

    @Override
    protected Template getComponentTemplate( final TemplateKey componentTemplateKey, final SiteTemplateKey siteTemplateKey )
    {
        return client.execute( page().template().layout().getByKey().
            key( (LayoutTemplateKey) componentTemplateKey ).
            siteTemplateKey( siteTemplateKey ) );
    }

    @Override
    protected Descriptor getComponentDescriptor( final DescriptorKey descriptorKey )
    {
        return this.client.execute( page().descriptor().layout().getByKey( (LayoutDescriptorKey) descriptorKey ) );
    }
}
