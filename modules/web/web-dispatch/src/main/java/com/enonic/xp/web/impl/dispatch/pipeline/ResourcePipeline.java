package com.enonic.xp.web.impl.dispatch.pipeline;

import java.util.List;

import jakarta.servlet.ServletContext;

import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinition;

public interface ResourcePipeline<T extends ResourceDefinition<?>>
{
    void init( ServletContext context );

    void destroy();

    List<T> list();
}
