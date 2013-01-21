package com.enonic.wem.core.content;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.GenerateContentName;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import static org.junit.Assert.*;

public class GenerateContentNameHandlerTest
    extends AbstractCommandHandlerTest
{
    private GenerateContentNameHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        handler = new GenerateContentNameHandler();
    }

    @Test
    public void test_generate_content_name()
        throws Exception
    {

        GenerateContentName generateContentName = Commands.content().generateContentName().displayName( "displayname" );
        handler.handle( this.context, generateContentName );

        String contentName = generateContentName.getResult();

        assertNotNull( contentName );
    }

}
