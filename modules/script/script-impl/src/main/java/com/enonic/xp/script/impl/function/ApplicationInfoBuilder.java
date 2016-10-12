package com.enonic.xp.script.impl.function;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import com.enonic.xp.app.Application;
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
        result.put( "name", this.application.getKey().getName() );
        result.put( "version", this.application.getVersion().toString() );

        final ScriptObjectMirror config = this.javascriptHelper.newJsObject();
        config.putAll( this.application.getConfig() );

        result.put( "config", config );
        return result;
    }
}
