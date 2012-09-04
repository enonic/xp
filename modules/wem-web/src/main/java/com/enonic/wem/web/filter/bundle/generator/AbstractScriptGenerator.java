package com.enonic.wem.web.filter.bundle.generator;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.context.support.ServletContextResourcePatternResolver;

import com.google.common.collect.Lists;

import com.enonic.wem.web.filter.bundle.BundleRequest;

abstract class AbstractScriptGenerator
    implements ScriptGenerator
{
    protected final List<String> findResources( final BundleRequest req, final List<String> locations )
        throws Exception
    {
        final List<String> list = Lists.newArrayList();

        if (locations != null) {
            findResources( list, req, locations );
        }

        return list;
    }

    private void findResources( final List<String> list, final BundleRequest req, final List<String> locations )
        throws Exception
    {
        for ( final String location : locations )
        {
            findResources( list, req, location );
        }
    }

    private void findResources( final List<String> list, final BundleRequest req, final String pattern )
        throws Exception
    {
        final ServletContextResourcePatternResolver resolver = new ServletContextResourcePatternResolver( req.getServletContext() );
        for ( final Resource res : resolver.getResources( req.getBasePath() + "/" + pattern ) )
        {
            if ( res instanceof ServletContextResource )
            {
                list.add( ( (ServletContextResource) res ).getPath() );
            }
        }
    }

    protected final String getRelativePath( final BundleRequest req, final String absolutePath )
    {
        return absolutePath.substring( req.getBasePath().length() + 1 );
    }
}
