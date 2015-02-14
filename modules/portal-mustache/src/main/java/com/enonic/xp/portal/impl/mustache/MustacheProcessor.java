package com.enonic.xp.portal.impl.mustache;

import java.util.Map;

import com.google.common.collect.Maps;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.MustacheException;
import com.samskivert.mustache.Template;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProblemException;

final class MustacheProcessor
{
    private final Mustache.Compiler compiler;

    private ResourceKey view;

    private final Map<String, Object> parameters;

    public MustacheProcessor( final Mustache.Compiler compiler )
    {
        this.compiler = compiler;
        this.parameters = Maps.newHashMap();
    }

    public MustacheProcessor view( final ResourceKey view )
    {
        this.view = view;
        return this;
    }

    public MustacheProcessor parameters( final Map<String, Object> parameters )
    {
        this.parameters.putAll( parameters );
        return this;
    }

    public String process()
    {
        try
        {
            return doProcess();
        }
        catch ( final RuntimeException e )
        {
            throw handleError( e );
        }
    }

    private String doProcess()
    {
        final Resource resource = Resource.from( this.view );
        final Template template = this.compiler.compile( resource.readString() );
        return template.execute( this.parameters );
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
        return ResourceProblemException.newBuilder().
            lineNumber( e.lineNo ).
            resource( this.view ).
            cause( e ).
            message( e.getMessage() ).
            build();
    }
}
