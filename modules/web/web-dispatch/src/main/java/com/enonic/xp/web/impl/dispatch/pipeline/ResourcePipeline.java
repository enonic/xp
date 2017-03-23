package com.enonic.xp.web.impl.dispatch.pipeline;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinition;

public interface ResourcePipeline<T extends ResourceDefinition>
    extends Iterable<T>
{
    void init( ServletContext context )
        throws ServletException;

    void destroy();
}
