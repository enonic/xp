package com.enonic.xp.jaxrs.swagger.impl;

import javax.ws.rs.Path;

import io.swagger.jaxrs.Reader;
import io.swagger.models.Info;
import io.swagger.models.Swagger;

import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.jaxrs.JaxRsService;
import com.enonic.xp.server.VersionInfo;

final class SwaggerModelReader
{
    private final JaxRsService jaxRsService;

    public SwaggerModelReader( final JaxRsService jaxRsService )
    {
        this.jaxRsService = jaxRsService;
    }

    public Swagger generate()
    {
        final Swagger swagger = new Swagger();
        swagger.setBasePath( "/" );
        swagger.setInfo( createInfo() );
        addResources( swagger );

        return swagger;
    }

    private void addResources( final Swagger swagger )
    {
        final Reader reader = new Reader( swagger );
        for ( final JaxRsComponent resource : this.jaxRsService.getComponents() )
        {
            addResource( reader, resource );
        }
    }

    private void addResource( final Reader reader, final Object resource )
    {
        final Class type = resource.getClass();
        if ( type.getAnnotation( Path.class ) != null )
        {
            reader.read( type );
        }
    }

    private Info createInfo()
    {
        final Info info = new Info();
        info.setTitle( "Enonic XP Rest API" );
        info.setDescription( "This document describes the public Enonic XP rest interfaces and rest extensions." );
        info.setVersion( VersionInfo.get().getVersion() );
        return info;
    }
}
