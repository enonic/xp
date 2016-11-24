package com.enonic.xp.script.impl.function;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import com.enonic.xp.app.Application;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.script.impl.util.JavascriptHelper;

public final class ApplicationInfoBuilder
{
    private Application application;

    private JavascriptHelper javascriptHelper;

    public ApplicationInfoBuilder application( final Application application )
    {
        this.application = application;
        return this;
    }

    public ApplicationInfoBuilder javascriptHelper( final JavascriptHelper javascriptHelper )
    {
        this.javascriptHelper = javascriptHelper;
        return this;
    }

    public ScriptObjectMirror build()
    {
        final ScriptObjectMirror result = this.javascriptHelper.newJsObject();
        result.put( "name", toString( this.application.getKey() ) );
        result.put( "version", toString( this.application.getVersion() ) );
        result.put( "config", buildConfig() );
        return result;
    }

    private ScriptObjectMirror buildConfig()
    {
        final ScriptObjectMirror result = this.javascriptHelper.newJsObject();
        final Configuration config = this.application.getConfig();

        if ( config != null )
        {
            result.putAll( config.asMap() );
        }

        return result;
    }

    private String toString( final Object value )
    {
        return value != null ? value.toString() : "";
    }
}
