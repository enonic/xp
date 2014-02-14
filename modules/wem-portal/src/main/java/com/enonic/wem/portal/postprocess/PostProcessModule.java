package com.enonic.wem.portal.postprocess;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

import com.enonic.wem.portal.postprocess.injection.LiveEditInjection;
import com.enonic.wem.portal.postprocess.injection.PostProcessInjection;
import com.enonic.wem.portal.postprocess.instruction.ComponentInstruction;
import com.enonic.wem.portal.postprocess.instruction.PostProcessInstruction;

public final class PostProcessModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        final Multibinder<PostProcessInstruction> instructions = Multibinder.newSetBinder( binder(), PostProcessInstruction.class );
        instructions.addBinding().to( ComponentInstruction.class );

        final Multibinder<PostProcessInjection> injections = Multibinder.newSetBinder( binder(), PostProcessInjection.class );
        injections.addBinding().to( LiveEditInjection.class );

        bind( PostProcessor.class ).to( PostProcessorImpl.class );
    }
}
