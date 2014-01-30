package com.enonic.wem.portal.view;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

import com.enonic.wem.portal.view.mustache.MustacheViewProcessor;
import com.enonic.wem.portal.view.thymeleaf.ThymeleafViewProcessor;
import com.enonic.wem.portal.view.xslt.XsltViewProcessor;

public final class ViewModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        final Multibinder<ViewProcessor> processors = Multibinder.newSetBinder( binder(), ViewProcessor.class );
        processors.addBinding().to( MustacheViewProcessor.class ).in( Singleton.class );
        processors.addBinding().to( ThymeleafViewProcessor.class ).in( Singleton.class );
        processors.addBinding().to( XsltViewProcessor.class ).in( Singleton.class );

        bind( ViewService.class ).to( ViewServiceImpl.class ).in( Singleton.class );
    }
}
