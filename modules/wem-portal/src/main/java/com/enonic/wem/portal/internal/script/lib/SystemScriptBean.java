package com.enonic.wem.portal.internal.script.lib;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SystemScriptBean
{
    private final static Logger LOG = LoggerFactory.getLogger( SystemScriptBean.class );

    public final static String NAME = "system";

    @Inject
    protected MustacheScriptBean mustache;

    @Inject
    protected XsltScriptBean xslt;

    @Inject
    protected ThymeleafScriptBean thymeleaf;

    @Inject
    protected ContentServiceScriptBean contentService;

    public MustacheScriptBean getMustache()
    {
        return this.mustache;
    }

    public XsltScriptBean getXslt()
    {
        return this.xslt;
    }

    public ThymeleafScriptBean getThymeleaf()
    {
        return this.thymeleaf;
    }

    public ContentServiceScriptBean getContentService()
    {
        return this.contentService;
    }

    public void log(String message) {
        LOG.info( message );
    }
}
