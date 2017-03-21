package com.enonic.xp.web.impl.dispatch.pipeline;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enonic.xp.web.impl.dispatch.mapping.FilterDefinition;

public interface FilterPipeline
    extends ResourcePipeline<FilterDefinition>
{
    void filter( HttpServletRequest req, HttpServletResponse res, ServletPipeline servletPipeline )
        throws ServletException, IOException;
}
