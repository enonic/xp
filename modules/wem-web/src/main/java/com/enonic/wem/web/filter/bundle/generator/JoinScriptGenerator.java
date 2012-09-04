package com.enonic.wem.web.filter.bundle.generator;

import java.net.URL;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.wem.web.filter.bundle.BundleRequest;

final class JoinScriptGenerator
    extends AbstractScriptGenerator
{
    @Override
    public String generate( final BundleRequest req )
        throws Exception
    {
        final List<String> paths = findResources( req, req.getBundleModel().getJsPaths() );

        if ( req.isDebugMode() )
        {
            return createJavaScriptLoader( req, paths );
        }
        else
        {
            return joinJavaScript( req, paths );
        }
    }

    private String joinJavaScript( final BundleRequest req, final List<String> paths )
        throws Exception
    {
        final StringBuilder str = new StringBuilder();
        for ( final String path : paths )
        {
            appendJavaScript( req, path, str );
        }

        return str.toString();
    }

    private void appendJavaScript( final BundleRequest req, final String path, final StringBuilder out )
        throws Exception
    {
        final URL url = req.getServletContext().getResource( path );
        final String content = Resources.toString( url, Charsets.UTF_8 );

        out.append( "\n" );
        out.append( "//\n" );
        out.append( "// " ).append( path ).append( "\n" );
        out.append( "//\n" );
        out.append( "\n" );
        out.append( content ).append( "\n" );
    }

    private String createJavaScriptLoader( final BundleRequest req, final List<String> paths )
        throws Exception
    {
        final StringBuilder str = new StringBuilder();
        for ( final String path : paths )
        {
            appendJavaScriptLoader( req, path, str );
        }

        return str.toString();
    }

    private void appendJavaScriptLoader( final BundleRequest req, final String path, final StringBuilder out )
        throws Exception
    {
        out.append( "document.write('" );
        out.append( "<script type=\"text/javascript\" charset=\"UTF-8\" src=\"" );
        out.append( path );
        out.append( "\"></script>" );
        out.append( "');\n" );
    }
}
