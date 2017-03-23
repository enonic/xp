package com.enonic.xp.web.impl.dispatch.mapping;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface FilterDefinition
    extends ResourceDefinition<Filter>
{
    boolean doFilter( HttpServletRequest req, HttpServletResponse res, FilterChain chain )
        throws IOException, ServletException;
}
