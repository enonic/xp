package com.enonic.xp.core.impl.site;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;

@Component(immediate = true)
public class SiteServiceImpl
    implements SiteService
{
    private SiteDescriptorRegistry siteDescriptorRegistry;

    private MixinService mixinService;

    @Override
    public SiteDescriptor getDescriptor( final ApplicationKey applicationKey )
    {
        final SiteDescriptor siteDescriptor = this.siteDescriptorRegistry.get( applicationKey );
        if ( siteDescriptor != null )
        {
            final Form form = mixinService.inlineFormItems( siteDescriptor.getForm() );
            return SiteDescriptor.copyOf( siteDescriptor ).
                form( form ).
                build();
        }
        return null;
    }

    @Reference
    public void setSiteDescriptorRegistry( final SiteDescriptorRegistry siteDescriptorRegistry )
    {
        this.siteDescriptorRegistry = siteDescriptorRegistry;
    }

    @Reference
    public void setMixinService( final MixinService mixinService )
    {
        this.mixinService = mixinService;
    }
}
