package com.enonic.wem.portal.internal.script.lib;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.script.ScriptLibrary;

@Singleton
@Deprecated
public final class SystemScriptBean
    implements ScriptLibrary
{
    private final static Logger LOG = LoggerFactory.getLogger( SystemScriptBean.class );

    private final static String NAME = "system";

    @Inject
    protected ContentServiceScriptBean contentService;

    @Override
    public String getName()
    {
        return NAME;
    }

    public Object getMustache()
    {
        throw new RuntimeException( "Converted to new format. Use require('view/mustache') instead." );
    }

    public Object getXslt()
    {
        throw new RuntimeException( "Converted to new format. Use require('view/xslt') instead." );
    }

    public Object getThymeleaf()
    {
        throw new RuntimeException( "Converted to new format. Use require('view/thymeleaf') instead." );
    }

    public ContentServiceScriptBean getContentService()
    {
        return this.contentService;
    }

    public void log( String message )
    {
        LOG.info( message );
    }
}
