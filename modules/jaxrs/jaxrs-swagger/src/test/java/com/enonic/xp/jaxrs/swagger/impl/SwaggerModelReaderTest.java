package com.enonic.xp.jaxrs.swagger.impl;

import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import io.swagger.models.Swagger;

import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.jaxrs.JaxRsService;

import static org.junit.Assert.*;

public class SwaggerModelReaderTest
{
    private JaxRsService jaxRsService;

    private List<JaxRsComponent> components;

    public SwaggerModelReaderTest()
    {
        this.components = Lists.newArrayList();
        this.jaxRsService = Mockito.mock( JaxRsService.class );
        Mockito.when( this.jaxRsService.getComponents() ).thenReturn( this.components );
    }

    private Swagger generate()
    {
        return new SwaggerModelReader( this.jaxRsService ).generate();
    }

    @Test
    public void testGenerate()
    {
        this.components.add( new TestSwaggerResource() );

        final Swagger swagger = generate();
        assertNotNull( swagger.getPaths() );
        assertEquals( 1, swagger.getPaths().size() );
    }

    @Test
    public void testGenerate_noResources()
    {
        final Swagger swagger = generate();
        assertNull( swagger.getPaths() );
    }
}
