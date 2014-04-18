package com.enonic.wem.core.script;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

import com.enonic.wem.core.script.compiler.ScriptCache;
import com.enonic.wem.core.script.compiler.ScriptCacheImpl;
import com.enonic.wem.core.script.compiler.ScriptCompiler;
import com.enonic.wem.core.script.compiler.ScriptCompilerImpl;
import com.enonic.wem.core.script.contributor.PortalScriptContributor;
import com.enonic.wem.core.script.contributor.SystemScriptContributor;
import com.enonic.wem.core.script.service.ScriptServiceImpl;

public final class ScriptModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( ScriptCache.class ).to( ScriptCacheImpl.class ).in( Singleton.class );
        bind( ScriptCompiler.class ).to( ScriptCompilerImpl.class ).in( Singleton.class );
        bind( ScriptService.class ).to( ScriptServiceImpl.class ).in( Singleton.class );

        final Multibinder<ScriptContributor> scriptContributors = Multibinder.newSetBinder( binder(), ScriptContributor.class );
        scriptContributors.addBinding().to( SystemScriptContributor.class ).in( Singleton.class );
        scriptContributors.addBinding().to( PortalScriptContributor.class ).in( Singleton.class );
    }
}
