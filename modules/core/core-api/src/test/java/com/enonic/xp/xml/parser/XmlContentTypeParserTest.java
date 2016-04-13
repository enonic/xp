package com.enonic.xp.xml.parser;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.Occurrences;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.Assert.*;

public class XmlContentTypeParserTest
    extends XmlModelParserTest
{
    private XmlContentTypeParser parser;

    private ContentType.Builder builder;

    @Before
    public void setup()
    {
        this.parser = new XmlContentTypeParser();
        this.parser.currentApplication( ApplicationKey.from( "myapplication" ) );

        this.builder = ContentType.create();
        this.builder.name( ContentTypeName.from( "myapplication:mytype" ) );
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
        final ContentType result = this.builder.build();
        assertEquals( "myapplication:mytype", result.getName().toString() );
        assertEquals( "All the Base Types", result.getDisplayName() );
        assertEquals( "description", result.getDescription() );
        assertEquals( "$('firstName') + ' ' + $('lastName')", result.getContentDisplayNameScript() );
        assertEquals( "myapplication:content", result.getSuperType().toString() );
        assertEquals( "[myapplication:metadata]", result.getMetadata().toString() );
        assertEquals( false, result.isAbstract() );
        assertEquals( true, result.isFinal() );

        assertEquals( 4, result.getForm().size() );
        assertEquals( "[myapplication:metadata]", result.getMetadata().toString() );

        final FormItem item = result.getForm().getFormItem( "myDate" );
        assertNotNull( item );

        final Input input = (Input) item;
        assertEquals( InputTypeName.DATE.toString(), input.getInputType().toString() );

        final FormItem contentSelectorItem = result.getForm().getFormItem( "someonesParent" );
        assertNotNull( contentSelectorItem );

        final Input contentSelectorInput = (Input) contentSelectorItem;
        assertEquals( InputTypeName.CONTENT_SELECTOR.toString(), contentSelectorInput.getInputType().toString() );

        assertEquals( "myapplication:mytype", contentSelectorInput.getInputTypeConfig().getProperty( "allowContentType" ).getValue() );
        assertEquals( 2, contentSelectorInput.getInputTypeConfig().getProperties( "allowContentType" ).size() );
        assertEquals( "path1", contentSelectorInput.getInputTypeConfig().getProperty( "allowPath" ).getValue() );
        assertEquals( 2, contentSelectorInput.getInputTypeConfig().getProperties( "allowPath" ).size() );
        assertEquals( "system:reference", contentSelectorInput.getInputTypeConfig().getProperty( "relationshipType" ).getValue() );

        final InputTypeConfig config = input.getInputTypeConfig();
        assertNotNull( config );

        final Input defaultOccurrencesInput = result.getForm().getInput( "defaultOccurrences" );
        final Occurrences defaultOccurrences = defaultOccurrencesInput.getOccurrences();
        assertEquals( 0, defaultOccurrences.getMinimum() );
        assertEquals( 1, defaultOccurrences.getMaximum() );
    }
}
