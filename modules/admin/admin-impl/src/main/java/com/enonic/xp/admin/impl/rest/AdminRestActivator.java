package com.enonic.xp.admin.impl.rest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.impl.rest.resource.content.CmsResourceDynamicFeature;
import com.enonic.xp.jaxrs.JaxRsService;
import com.enonic.xp.jaxrs.JaxRsServiceFactory;
import com.enonic.xp.web.dispatch.DispatchConstants;

@Component(immediate = true)
public final class AdminRestActivator
{
    private JaxRsService service;

    @Activate
    public void activate()
    {
        this.service.init();
    }

    @Deactivate
    public void deactivate()
    {
        this.service.destroy();
    }

    @Reference
    public void setJaxRsServiceFactory( final JaxRsServiceFactory factory )
    {
        service = factory.newService( "admin", "/admin/rest", DispatchConstants.XP_CONNECTOR );
        service.add( new CmsResourceDynamicFeature() );
    }
}
