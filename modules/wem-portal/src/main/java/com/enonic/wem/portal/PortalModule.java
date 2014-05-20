package com.enonic.wem.portal;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

import com.enonic.wem.portal.content.ComponentResource;
import com.enonic.wem.portal.content.ContentResource;
import com.enonic.wem.portal.exception.mapper.DefaultExceptionMapper;
import com.enonic.wem.portal.exception.mapper.PortalWebExceptionMapper;
import com.enonic.wem.portal.exception.mapper.SourceExceptionMapper;
import com.enonic.wem.portal.exception.mapper.WebApplicationExceptionMapper;
import com.enonic.wem.portal.postprocess.PostProcessModule;
import com.enonic.wem.portal.rendering.RenderingModule;
import com.enonic.wem.portal.script.ScriptModule;
import com.enonic.wem.portal.underscore.ImageByIdResource;
import com.enonic.wem.portal.underscore.ImageResource;
import com.enonic.wem.portal.underscore.PublicResource;
import com.enonic.wem.portal.underscore.ServicesResource;
import com.enonic.wem.portal.view.ViewModule;
import com.enonic.wem.portal.xslt.saxon.SaxonXsltModule;

public final class PortalModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        install( new ScriptModule() );
        install( new PostProcessModule() );
        install( new RenderingModule() );
        install( new SaxonXsltModule() );
        install( new ViewModule() );

        bind( ImageResource.class ).in( Singleton.class );
        bind( ImageByIdResource.class ).in( Singleton.class );
        bind( PublicResource.class ).in( Singleton.class );
        bind( ContentResource.class ).in( Singleton.class );
        bind( ComponentResource.class ).in( Singleton.class );
        bind( ServicesResource.class ).in( Singleton.class );
        bind( SourceExceptionMapper.class ).in( Singleton.class );
        bind( PortalWebExceptionMapper.class ).in( Singleton.class );
        bind( DefaultExceptionMapper.class ).in( Singleton.class );
        bind( WebApplicationExceptionMapper.class ).in( Singleton.class );
    }
}
