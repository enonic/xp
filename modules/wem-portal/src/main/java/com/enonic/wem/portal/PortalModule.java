package com.enonic.wem.portal;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

import com.enonic.wem.portal.content.OldComponentResource;
import com.enonic.wem.portal.content.OldContentResource;
import com.enonic.wem.portal.exception.mapper.DefaultExceptionMapper;
import com.enonic.wem.portal.exception.mapper.PortalWebExceptionMapper;
import com.enonic.wem.portal.exception.mapper.SourceExceptionMapper;
import com.enonic.wem.portal.exception.mapper.WebApplicationExceptionMapper;
import com.enonic.wem.portal.postprocess.PostProcessModule;
import com.enonic.wem.portal.rendering.RenderingModule;
import com.enonic.wem.portal.restlet.RestletModule;
import com.enonic.wem.portal.script.ScriptModule;
import com.enonic.wem.portal.underscore.OldImageByIdResource;
import com.enonic.wem.portal.underscore.OldImageResource;
import com.enonic.wem.portal.underscore.OldPublicResource;
import com.enonic.wem.portal.underscore.OldServicesResource;
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
        install( new RestletModule() );

        bind( OldImageResource.class ).in( Singleton.class );
        bind( OldImageByIdResource.class ).in( Singleton.class );
        bind( OldPublicResource.class ).in( Singleton.class );
        bind( OldContentResource.class ).in( Singleton.class );
        bind( OldComponentResource.class ).in( Singleton.class );
        bind( OldServicesResource.class ).in( Singleton.class );
        bind( SourceExceptionMapper.class ).in( Singleton.class );
        bind( PortalWebExceptionMapper.class ).in( Singleton.class );
        bind( DefaultExceptionMapper.class ).in( Singleton.class );
        bind( WebApplicationExceptionMapper.class ).in( Singleton.class );
    }
}
