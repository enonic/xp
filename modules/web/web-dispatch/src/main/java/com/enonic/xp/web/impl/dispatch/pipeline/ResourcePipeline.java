package com.enonic.xp.web.impl.dispatch.pipeline;

import java.util.List;

import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinition;

public interface ResourcePipeline<T extends ResourceDefinition<?>>
{
    List<T> list();
}
