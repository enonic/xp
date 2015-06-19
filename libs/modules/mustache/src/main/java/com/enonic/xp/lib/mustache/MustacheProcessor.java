package com.enonic.xp.lib.mustache;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.MustacheException;
import com.samskivert.mustache.Template;

import com.enonic.xp.portal.script.ScriptValue;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProblemException;

public final class MustacheProcessor
{
    private final Mustache.Compiler compiler;

    private ResourceKey view;

    private ScriptValue model;

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
        return template.execute( this.model.getMap() );
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
