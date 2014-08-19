package com.enonic.wem.thymeleaf;

import org.junit.Test;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceUrlRegistry;
import com.enonic.wem.api.resource.ResourceUrlTestHelper;
import com.enonic.wem.script.ScriptRunner;
import com.enonic.wem.script.internal.RhinoScriptRunnerFactory;
import com.enonic.wem.thymeleaf.internal.ThymeleafScriptContributor;

public class ThymeleafJavascriptTest
{
    @Test
    public void renderTest()
        throws Exception
    {
        final ResourceUrlRegistry urlRegistry = ResourceUrlTestHelper.mockModuleScheme();
        urlRegistry.modulesClassLoader( getClass().getClassLoader() );

        final SimpleScriptEnvironment environment = new SimpleScriptEnvironment();
        environment.addContributor( ModuleKey.from( "library-1.0.0" ), new ThymeleafScriptContributor() );

        final RhinoScriptRunnerFactory runnerFactory = new RhinoScriptRunnerFactory();
        runnerFactory.setEnvironment( environment );
        final ScriptRunner runner = runnerFactory.newRunner();
        runner.property( "test", new JavascriptTestHelper() );

        runner.source( ResourceKey.from( "mymodule-1.0.0:/render-test.js" ) );
        runner.execute();
    }
}
