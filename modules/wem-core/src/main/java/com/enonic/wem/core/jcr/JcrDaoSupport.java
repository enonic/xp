package com.enonic.wem.core.jcr;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class JcrDaoSupport
{
    @Autowired
    private JcrTemplate template;

    protected JcrTemplate getTemplate()
    {
        return template;
    }

}
