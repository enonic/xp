package com.enonic.xp.script.impl;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.condition.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.core.internal.Dictionaries;

@Component(immediate = true)
public class ScriptRuntimeFactoryActivator
{
    private static final Logger LOG = LoggerFactory.getLogger( ScriptRuntimeFactoryActivator.class );

    private static final String GRAAL_JS_SCRIPT_ENGINE = "GraalJS";

    public static final String NASHORN_SCRIPT_ENGINE = "Nashorn";

    private static String engineName()
    {
        final String scriptEngine = System.getProperty( "xp.script-engine", NASHORN_SCRIPT_ENGINE );
        if ( scriptEngine.equalsIgnoreCase( GRAAL_JS_SCRIPT_ENGINE ) )
        {
            return GRAAL_JS_SCRIPT_ENGINE;
        }
        else if ( scriptEngine.equalsIgnoreCase( NASHORN_SCRIPT_ENGINE ) )
        {
            return NASHORN_SCRIPT_ENGINE;
        }
        else
        {
            throw new IllegalArgumentException( "Unsupported script engine " + scriptEngine );
        }
    }

    @Activate
    public void activate( final BundleContext bundleContext )
    {
        final String scriptEngine = engineName();
        bundleContext.registerService( Condition.class, Condition.INSTANCE, Dictionaries.of( Condition.CONDITION_ID, scriptEngine ) );
        LOG.info( "Using {} as script engine", scriptEngine );
    }
}
