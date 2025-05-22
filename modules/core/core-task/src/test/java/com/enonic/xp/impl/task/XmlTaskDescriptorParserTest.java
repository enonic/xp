package com.enonic.xp.impl.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.task.TaskDescriptor;
import com.enonic.xp.xml.parser.XmlModelParserTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class XmlTaskDescriptorParserTest
    extends XmlModelParserTest
{
    private XmlTaskDescriptorParser parser;

    private TaskDescriptor.Builder builder;

    @BeforeEach
    public void setup()
    {
        this.parser = new XmlTaskDescriptorParser();
        this.parser.currentApplication( ApplicationKey.from( "myapplication" ) );

        this.builder = TaskDescriptor.create();
        this.builder.key( DescriptorKey.from( "myapplication:mytask" ) );
        this.parser.builder( this.builder );
    }

    @Test
    public void testParse()
        throws Exception
    {
        parse( this.parser, ".xml" );
        assertResult();
    }

    @Test
    public void testParse_noNs()
        throws Exception
    {
        parseRemoveNs( this.parser, ".xml" );
        assertResult();
    }

    private void assertResult()
        throws Exception
    {
        final TaskDescriptor result = this.builder.build();
        assertEquals( "myapplication:mytask", result.getKey().toString() );
        assertEquals( "mytask", result.getName() );
        assertEquals( "My task", result.getDescription() );

        final Form config = result.getConfig();
        assertNotNull( config );
        final Input input = config.getInput( "param1" );
        assertNotNull( input );
    }

}
