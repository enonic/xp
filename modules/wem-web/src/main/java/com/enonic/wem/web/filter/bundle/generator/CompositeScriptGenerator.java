package com.enonic.wem.web.filter.bundle.generator;

import org.springframework.stereotype.Component;

import com.enonic.wem.web.filter.bundle.BundleRequest;

@Component
public final class CompositeScriptGenerator
    implements ScriptGenerator
{
    private final ScriptGenerator[] generators;

    public CompositeScriptGenerator()
    {
        this.generators = new ScriptGenerator[]{new JoinScriptGenerator(), new TemplateScriptGenerator()};
    }

    @Override
    public String generate( final BundleRequest req )
        throws Exception
    {
        final StringBuilder str = new StringBuilder();
        for ( final ScriptGenerator generator : this.generators )
        {
            final String result = generator.generate( req );
            if ( result != null )
            {
                str.append( result ).append( "\n" );
            }
        }

        return str.toString();
    }
}
