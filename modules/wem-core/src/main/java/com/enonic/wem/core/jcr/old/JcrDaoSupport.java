package com.enonic.wem.core.jcr.old;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class JcrDaoSupport
{
    private JcrTemplate template;

    protected JcrTemplate getTemplate()
    {
        return template;
    }

    @Autowired
    public void setTemplate( JcrTemplate template )
    {
        this.template = template;
    }
}
