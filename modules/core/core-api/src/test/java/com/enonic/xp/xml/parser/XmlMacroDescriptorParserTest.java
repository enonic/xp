package com.enonic.xp.xml.parser;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroKey;

import static org.junit.Assert.*;

public class XmlMacroDescriptorParserTest
    extends XmlModelParserTest
{
    private XmlMacroDescriptorParser parser;

    private MacroDescriptor.Builder builder;

    @Before
    public void setup()
    {
        this.parser = new XmlMacroDescriptorParser();
        this.parser.currentApplication( ApplicationKey.from( "myapplication" ) );

        this.builder = MacroDescriptor.create();
        this.builder.key( MacroKey.from( "myapplication:mymacro" ) );
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
        final MacroDescriptor result = this.builder.build();
        assertEquals( "myapplication:mymacro", result.getKey().toString() );
        assertEquals( "My macro", result.getDisplayName() );
        assertEquals( "key.display-name", result.getDisplayNameI18nKey() );
        assertEquals( "key.description", result.getDescriptionI18nKey() );

        assertEquals( "This macro is a test", result.getDescription() );

        assertEquals( 3, result.getForm().size() );

        final FormItem item = result.getForm().getFormItem( "myDate" );
        assertNotNull( item );

        final Input input = (Input) item;
        assertEquals( InputTypeName.DATE.toString(), input.getInputType().toString() );
        assertEquals( "key.label", input.getLabelI18nKey() );
        assertEquals( "key.help-text", input.getHelpTextI18nKey() );

        final FormItem contentSelectorItem = result.getForm().getFormItem( "someonesParent" );
        assertNotNull( contentSelectorItem );

        final Input contentSelectorInput = (Input) contentSelectorItem;
        assertEquals( InputTypeName.CONTENT_SELECTOR.toString(), contentSelectorInput.getInputType().toString() );
        assertEquals( "key.parent", contentSelectorInput.getLabelI18nKey() );

        assertEquals( "myapplication:mytype", contentSelectorInput.getInputTypeConfig().getProperty( "allowContentType" ).getValue() );
        assertEquals( 2, contentSelectorInput.getInputTypeConfig().getProperties( "allowContentType" ).size() );
        assertEquals( "path1", contentSelectorInput.getInputTypeConfig().getProperty( "allowPath" ).getValue() );
        assertEquals( 2, contentSelectorInput.getInputTypeConfig().getProperties( "allowPath" ).size() );
        assertEquals( "system:reference", contentSelectorInput.getInputTypeConfig().getProperty( "relationshipType" ).getValue() );

        final InputTypeConfig config = input.getInputTypeConfig();
        assertNotNull( config );
    }
}