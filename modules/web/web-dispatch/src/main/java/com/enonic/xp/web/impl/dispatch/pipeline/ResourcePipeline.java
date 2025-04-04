package com.enonic.xp.web.impl.dispatch.pipeline;

import java.util.List;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinition;

public interface ResourcePipeline<T extends ResourceDefinition<?>>
{
    void init( ServletContext context )
        throws ServletException;

    void destroy();

    List<T> list();
}
