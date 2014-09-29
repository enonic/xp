package com.enonic.wem.portal.internal;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

import com.enonic.wem.portal.internal.controller.JsControllerFactory;
import com.enonic.wem.portal.internal.controller.JsControllerFactoryImpl;
import com.enonic.wem.portal.internal.postprocess.PostProcessor;
import com.enonic.wem.portal.internal.postprocess.PostProcessorImpl;
import com.enonic.wem.portal.internal.postprocess.injection.LiveEditInjection;
import com.enonic.wem.portal.internal.postprocess.injection.PostProcessInjection;
import com.enonic.wem.portal.internal.postprocess.instruction.ComponentInstruction;
import com.enonic.wem.portal.internal.postprocess.instruction.PostProcessInstruction;
import com.enonic.wem.portal.internal.rendering.RenderingModule;
import com.enonic.wem.portal.internal.restlet.RestletModule;

public final class PortalModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( JsControllerFactory.class ).to( JsControllerFactoryImpl.class ).in( Singleton.class );

        final Multibinder<PostProcessInstruction> instructions = Multibinder.newSetBinder( binder(), PostProcessInstruction.class );
        instructions.addBinding().to( ComponentInstruction.class );

        final Multibinder<PostProcessInjection> injections = Multibinder.newSetBinder( binder(), PostProcessInjection.class );
        injections.addBinding().to( LiveEditInjection.class );

        bind( PostProcessor.class ).to( PostProcessorImpl.class );

        install( new RenderingModule() );
        install( new RestletModule() );
    }
}
