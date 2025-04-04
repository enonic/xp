package com.enonic.xp.web.impl.dispatch.mapping;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface FilterDefinition
    extends ResourceDefinition<Filter>
{
    boolean doFilter( HttpServletRequest req, HttpServletResponse res, FilterChain chain )
        throws IOException, ServletException;
}
