package com.enonic.xp.lib.mustache;

import java.util.Map;

import com.google.common.collect.Maps;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.MustacheException;
import com.samskivert.mustache.Template;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;

public final class MustacheProcessor
{
    private final Mustache.Compiler compiler;

    private ResourceKey view;

    private ScriptValue model;

    private ResourceService resourceService;

    public MustacheProcessor( final Mustache.Compiler compiler )
    {
        this.compiler = compiler;
    }

    public void setView( final ResourceKey view )
    {
        this.view = view;
    }

    public void setModel( final ScriptValue model )
    {
        this.model = model;
    }

    public String process()
    {
        final Trace trace = Tracer.newTrace( "mustache.render" );
        if ( trace == null )
        {
            return execute();
        }

        return Tracer.trace( trace, () -> {
            trace.put( "path", this.view.getPath() );
            trace.put( "app", this.view.getApplicationKey().toString() );
            return execute();
        } );
    }

    public String execute()
    {
        try
        {
            return doExecute();
        }
        catch ( final RuntimeException e )
        {
            throw handleError( e );
        }
    }

    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    private String doExecute()
    {
        final Resource resource = resourceService.getResource( this.view );
        final Template template = this.compiler.compile( resource.readString() );

        final Map<String, Object> map = this.model != null ? this.model.getMap() : Maps.newHashMap();
        return template.execute( map );
    }

    private RuntimeException handleError( final RuntimeException e )
    {
        if ( e instanceof MustacheException.Context )
        {
            return handleError( (MustacheException.Context) e );
        }

        return e;
    }

    private RuntimeException handleError( final MustacheException.Context e )
    {
        return ResourceProblemException.create().
            lineNumber( e.lineNo ).
            resource( this.view ).
            cause( e ).
            message( e.getMessage() ).
            build();
    }
}
