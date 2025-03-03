package com.enonic.xp.web.impl.dispatch.pipeline;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.web.impl.dispatch.mapping.FilterDefinition;

public interface FilterPipeline
    extends ResourcePipeline<FilterDefinition>
{
    void filter( HttpServletRequest req, HttpServletResponse res, ServletPipeline servletPipeline )
        throws ServletException, IOException;
}
