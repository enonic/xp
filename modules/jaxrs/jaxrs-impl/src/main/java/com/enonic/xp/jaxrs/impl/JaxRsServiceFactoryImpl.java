package com.enonic.xp.jaxrs.impl;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.jaxrs.JaxRsService;
import com.enonic.xp.jaxrs.JaxRsServiceFactory;
import com.enonic.xp.jaxrs.impl.security.SecurityFeature;
import com.enonic.xp.web.multipart.MultipartService;

@Component
public final class JaxRsServiceFactoryImpl
    implements JaxRsServiceFactory
{
    private BundleContext context;

    private MultipartService multipartService;

    @Activate
    public void activate( final BundleContext context )
    {
        this.context = context;
    }

    @Override
    public JaxRsService newService( final String group, final String path, final String connector )
    {
        final JaxRsService service = new JaxRsServiceImpl( this.context, group, path, connector );
        service.add( new SecurityFeature() );
        service.add( new CommonFeature( this.multipartService ) );
        return service;
    }

    @Reference
    public void setMultipartService( final MultipartService multipartService )
    {
        this.multipartService = multipartService;
    }
}
