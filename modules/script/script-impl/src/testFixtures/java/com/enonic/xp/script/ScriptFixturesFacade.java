package com.enonic.xp.script;

import java.util.Arrays;

import com.enonic.xp.app.Application;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.impl.executor.ScriptExecutor;
import com.enonic.xp.script.impl.service.ServiceRegistry;
import com.enonic.xp.script.impl.standard.ScriptRuntimeImpl;
import com.enonic.xp.script.impl.value.ScriptValueFactory;
import com.enonic.xp.script.runtime.ScriptRuntime;
import com.enonic.xp.script.runtime.ScriptRuntimeFactory;
import com.enonic.xp.script.runtime.ScriptSettings;

public interface ScriptFixturesFacade
{
    default ScriptRuntimeFactory scriptRuntimeFactory( ResourceService resourceService, ServiceRegistry services,
                                                       Application... applications )
    {
        return new ScriptRuntimeFactory()
        {

            @Override
            public ScriptRuntime create( final ScriptSettings settings )
            {
                return new ScriptRuntimeImpl( applicationKey -> createExecutor( settings, services, resourceService,
                                                                                Arrays.stream( applications )
                                                                                    .filter( app -> app.getKey().equals( applicationKey ) )
                                                                                    .findFirst()
                                                                                    .orElseThrow() ) );
            }

            @Override
            public void dispose( final ScriptRuntime runtime )
            {

            }
        };
    }

    ScriptValueFactory<?> scriptValueFactory();

    ScriptExecutor createExecutor( ScriptSettings scriptSettings, ServiceRegistry serviceRegistry, ResourceService resourceService,
                                   Application application );

    static ScriptFixturesFacade getInstance()
    {
        return getInstance( className() );
    }

    static ScriptFixturesFacade getInstance( String facadeClassName )
    {

        try
        {
            return (ScriptFixturesFacade) Class.forName( facadeClassName ).getDeclaredConstructor().newInstance();
        }
        catch ( Exception e )
        {
            throw new IllegalArgumentException( "Unsupported engine facade " + facadeClassName, e );
        }
    }

    private static String className()
    {
        final String scriptEngine = System.getProperty( "xp.script-engine", "Nashorn" );
        if ( scriptEngine.equalsIgnoreCase( "GraalJS" ) )
        {
            return "com.enonic.xp.script.graal.GraalJsScriptFixturesFacade";
        }
        else if ( scriptEngine.equalsIgnoreCase( "Nashorn" ) )
        {
            return "com.enonic.xp.script.impl.NashornScriptFixturesFacade";
        }
        else
        {
            throw new IllegalArgumentException( "Unsupported script engine '" + scriptEngine + "'" );
        }
    }
}
