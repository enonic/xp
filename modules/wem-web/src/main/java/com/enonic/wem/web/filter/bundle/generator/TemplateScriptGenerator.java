package com.enonic.wem.web.filter.bundle.generator;

import java.net.URL;
import java.util.List;

import org.springframework.web.util.JavaScriptUtils;
import org.springframework.web.util.WebUtils;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.wem.web.filter.bundle.BundleRequest;

final class TemplateScriptGenerator
    extends AbstractScriptGenerator
{
    @Override
    public String generate( final BundleRequest req )
        throws Exception
    {
        final List<String> paths = findResources( req, req.getBundleModel().getTemplatePaths() );
        return writeTemplates( req, paths );
    }

    private String writeTemplates( final BundleRequest req, final List<String> paths )
        throws Exception
    {
        final StringBuilder str = new StringBuilder();
        for ( final String path : paths )
        {
            writeTemplate( req, path, str );
        }

        return str.toString();
    }

    private void writeTemplate( final BundleRequest req, final String path, final StringBuilder out )
        throws Exception
    {
        final URL url = req.getServletContext().getResource( path );
        final List<String> contentLines = Resources.readLines( url, Charsets.UTF_8 );

        String name = WebUtils.extractFilenameFromUrlPath( path ).replace( '/', '.' );
        if (req.getBundleModel().getNamespace() != null) {
            name = req.getBundleModel().getNamespace() + "." + name;
        }

        out.append( "\n" );
        out.append( "//\n" );
        out.append( "// " ).append( path ).append( "\n" );
        out.append( "//\n" );
        out.append( "\n" );

        out.append( "Admin.lib.TemplateStore.add(\"" );
        out.append( name );
        out.append( "\",\"" );

        for ( final String line : contentLines )
        {
            final String trimmed = line.trim();
            if ( trimmed.length() > 0 )
            {
                out.append( JavaScriptUtils.javaScriptEscape( trimmed ) );
            }
        }

        out.append( "\");\n" );
    }
}
