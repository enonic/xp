package com.enonic.wem.core.script.service;

import java.net.URI;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.ModuleScript;
import org.mozilla.javascript.commonjs.module.ModuleScriptProvider;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceKeyResolver;
import com.enonic.wem.api.resource.ResourceService;
import com.enonic.wem.core.script.compiler.ScriptCompiler;

final class ScriptProviderImpl
    implements ModuleScriptProvider
{
    protected ScriptContextImpl scriptContext;

    protected ResourceService resourceService;

    protected ScriptCompiler scriptCompiler;

    @Override
    public ModuleScript getModuleScript( final Context context, final String moduleId, final URI moduleUri, final URI baseUri,
                                         final Scriptable paths )
        throws Exception
    {
        final String jsName = moduleId.endsWith( ".js" ) ? moduleId : ( moduleId + ".js" );

        final ResourceKey parentKey = this.scriptContext.getResourceKey();
        final ResourceKeyResolver resourceKeyResolver = this.scriptContext.getResourceKeyResolver();
        final ResourceKey resourceKey = resourceKeyResolver.resolve( parentKey, jsName );

        final Resource resource = this.resourceService.getResource( resourceKey );
        final Script script = this.scriptCompiler.compile( context, resource );

        return new ModuleScript( script, new URI( resourceKey.toString() ), null );
    }
}
