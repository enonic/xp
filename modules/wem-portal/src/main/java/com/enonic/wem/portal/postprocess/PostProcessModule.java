package com.enonic.wem.portal.postprocess;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

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

        bind( PostProcessor.class ).to( PostProcessorImpl.class );
    }
}
