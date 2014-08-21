package com.enonic.wem.portal.internal.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.api.rendering.RenderingMode;

import static org.junit.Assert.*;

public class JsHttpRequestTest
{
    @Test
    public void setMethod()
    {
        final JsHttpRequest request = new JsHttpRequest();
        assertNull( request.getMethod() );

        request.setMethod( "GET" );
        assertEquals( "GET", request.getMethod() );
    }

    @Test
    public void setMode()
    {
        final JsHttpRequest request = new JsHttpRequest();
        assertEquals( RenderingMode.LIVE, request.getMode() );

        request.setMode( "edit" );
        assertEquals( RenderingMode.EDIT, request.getMode() );
    }

    @Test
    public void setWorkspace()
        throws Exception
    {
        final JsHttpRequest request = new JsHttpRequest();
        assertEquals( JsHttpRequest.DEFAULT_WORKSPACE, request.getWorkspace() );

        request.setWorkspace( "another" );
        assertEquals( Workspace.from( "another" ), request.getWorkspace() );
    }

    @Test
    public void addParam()
    {
        final JsHttpRequest request = new JsHttpRequest();
        assertNotNull( request.getParams() );
        assertEquals( 0, request.getParams().size() );

        request.addParam( "name", "value" );
        assertEquals( 1, request.getParams().size() );
    }

    @Test
    public void addParamsList()
    {
        final Map<String, List<String>> map = Maps.newHashMap();
        map.put( "name", Collections.singletonList( "value" ) );

        final JsHttpRequest request = new JsHttpRequest();
        assertNotNull( request.getParams() );
        assertEquals( 0, request.getParams().size() );

        request.addParams( map );
        assertEquals( 1, request.getParams().size() );
    }

    @Test
    public void addParamsMultimap()
    {
        final Multimap<String, String> map = HashMultimap.create();
        map.put( "name", "value" );

        final JsHttpRequest request = new JsHttpRequest();
        assertNotNull( request.getParams() );
        assertEquals( 0, request.getParams().size() );

        request.addParams( map );
        assertEquals( 1, request.getParams().size() );
    }
}
