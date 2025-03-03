package com.enonic.xp.web.impl.dispatch.pipeline;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.web.impl.dispatch.mapping.ServletDefinition;

public interface ServletPipeline
    extends ResourcePipeline<ServletDefinition>
{
    void service( HttpServletRequest req, HttpServletResponse res )
        throws ServletException, IOException;
}
