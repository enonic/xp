package com.enonic.xp.xml.parser;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormItemType;
import com.enonic.xp.form.FormItems;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
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

    @Test
    public void testOptionSetParse()
        throws Exception
    {
        parse( this.parser, "-optionSet.xml" );
        assertOptionSetResult();
    }

    @Test
    public void testOptionSetParse_noNs()
        throws Exception
    {
        parseRemoveNs( this.parser, "-optionSet.xml" );
        assertOptionSetResult();
    }

    @Test
    public void testI18nParse()
        throws Exception
    {
        parse( this.parser, "-i18n.xml" );
        final ContentType result = this.builder.build();

        assertEquals( "translated.display-name", result.getDisplayNameI18nKey() );

    }

    @Test
    public void testI18n_formInput()
        throws Exception
    {
        parse( this.parser, "-i18n.xml" );
        final ContentType result = this.builder.build();

        final Input input = (Input) result.getForm().getFormItem( "textLine" );

        assertEquals( "translated.label", input.getLabelI18nKey() );
        assertEquals( "translated.help-text", input.getHelpTextI18nKey() );

    }

    private void assertOptionSetResult()
        throws Exception
    {
        final ContentType result = this.builder.build();
        assertEquals( "myapplication:mytype", result.getName().toString() );
        assertEquals( "OptionSet", result.getDisplayName() );
        assertEquals( "OptionSet for testing", result.getDescription() );
        assertEquals( "base:structured", result.getSuperType().toString() );
        assertEquals( false, result.isAbstract() );
        assertEquals( false, result.isFinal() );

        assertEquals( 2, result.getForm().size() );

        final FormItem item = result.getForm().getFormItem( "radioOptionSet" );
        assertNotNull( item );

        final FormOptionSet radioOptionSet = (FormOptionSet) item;
        assertEquals( FormItemType.FORM_OPTION_SET, radioOptionSet.getType() );

        final FormItems radioOptions = radioOptionSet.getFormItems();
        assertEquals( 2, radioOptions.size() );

        FormOptionSetOption radioOption1 = radioOptions.getItemByName( "option_1" ).toFormOptionSetOption();
        FormOptionSetOption radioOption2 = radioOptions.getItemByName( "option_2" ).toFormOptionSetOption();

        assertEquals( FormItemType.FORM_OPTION_SET_OPTION, radioOption1.getType() );
        assertEquals( FormItemType.FORM_OPTION_SET_OPTION, radioOption2.getType() );

        assertFalse( radioOption1.isDefaultOption() );
        assertFalse( radioOption2.isDefaultOption() );

        assertEquals( 0, radioOption2.getFormItems().size() );

        assertEquals( 2, radioOption1.getFormItems().size() );

        final Input textInput = radioOption1.getFormItems().getItemByName( "text-input" ).toInput();
        assertEquals( InputTypeName.TEXT_LINE.toString(), textInput.getInputType().toString() );
        assertEquals( "Text input", textInput.getHelpText() );

        final FormItemSet formItemSet = radioOption1.getFormItems().getItemByName( "minimum3" ).toFormItemSet();
        assertEquals( 2, formItemSet.getFormItems().size() );

        final FormOptionSet checkOptionSet = result.getForm().getFormItem( "checkOptionSet" ).toFormOptionSet();
        assertEquals( FormItemType.FORM_OPTION_SET, checkOptionSet.getType() );

        assertEquals( "Multi selection", checkOptionSet.getLabel() );
        assertTrue( checkOptionSet.isExpanded() );

        final Occurrences checkOptionSetOccurrences = checkOptionSet.getOccurrences();
        assertEquals( 0, checkOptionSetOccurrences.getMinimum() );
        assertEquals( 1, checkOptionSetOccurrences.getMaximum() );

        final FormItems checkOptions = checkOptionSet.getFormItems();
        assertEquals( 4, checkOptions.size() );

        //check option set 1st option
        final FormOptionSetOption checkOption1 = checkOptions.getItemByName( "option_1" ).toFormOptionSetOption();
        assertTrue( checkOption1.isDefaultOption() );
        assertEquals( 0, checkOption1.getFormItems().size() );

        //check option set 2nd option
        final FormOptionSetOption checkOption2 = checkOptions.getItemByName( "option_2" ).toFormOptionSetOption();
        assertTrue( checkOption2.isDefaultOption() );
        assertEquals( 1, checkOption2.getFormItems().size() );

        // nested option set
        final FormOptionSet nestedOptionSet = checkOption2.getFormItems().getItemByName( "nestedOptionSet" ).toFormOptionSet();
        final FormItems nestedSetOptions = nestedOptionSet.getFormItems();
        assertEquals( 2, nestedSetOptions.size() );
        assertFalse( nestedOptionSet.isExpanded() );

        final FormOptionSetOption nestedSetOption1 = nestedSetOptions.getItemByName( "option2_1" ).toFormOptionSetOption();
        assertFalse( nestedSetOption1.isDefaultOption() );
        assertEquals( 1, nestedSetOption1.getFormItems().size() );

        final FormOptionSetOption nestedSetOption2 = nestedSetOptions.getItemByName( "option2_2" ).toFormOptionSetOption();
        assertTrue( nestedSetOption2.isDefaultOption() );
        assertEquals( 1, nestedSetOption2.getFormItems().size() );

        //check option set 3rd option
        final FormOptionSetOption checkOption3 = checkOptions.getItemByName( "option_3" ).toFormOptionSetOption();
        assertFalse( checkOption3.isDefaultOption() );
        assertEquals( 1, checkOption3.getFormItems().size() );

        final Input imageSelectorInput = checkOption3.getFormItems().getItemByName( "imageselector" ).toInput();

        assertEquals( "myapplication:mytype", imageSelectorInput.getInputTypeConfig().getProperty( "allowContentType" ).getValue() );
        assertEquals( 2, imageSelectorInput.getInputTypeConfig().getProperties( "allowContentType" ).size() );
        assertEquals( "path1", imageSelectorInput.getInputTypeConfig().getProperty( "allowPath" ).getValue() );
        assertEquals( 2, imageSelectorInput.getInputTypeConfig().getProperties( "allowPath" ).size() );
        assertEquals( "system:reference", imageSelectorInput.getInputTypeConfig().getProperty( "relationshipType" ).getValue() );

        final Occurrences imageSelectorOccurrences = imageSelectorInput.getOccurrences();
        assertEquals( 1, imageSelectorOccurrences.getMinimum() );
        assertEquals( 1, imageSelectorOccurrences.getMaximum() );

        //check option set 4th option
        final FormOptionSetOption checkOption4 = checkOptions.getItemByName( "option_4" ).toFormOptionSetOption();
        assertFalse( checkOption4.isDefaultOption() );
        assertEquals( 2, checkOption4.getFormItems().size() );

        final Input doubleInput = checkOption4.getFormItems().getItemByName( "double" ).toInput();
        final Input longInput = checkOption4.getFormItems().getItemByName( "long" ).toInput();
        assertEquals( InputTypeName.DOUBLE.toString(), doubleInput.getInputType().toString() );
        assertEquals( InputTypeName.LONG.toString(), longInput.getInputType().toString() );

        final Occurrences longInputOccurrences = longInput.getOccurrences();
        assertEquals( 0, longInputOccurrences.getMinimum() );
        assertEquals( 1, longInputOccurrences.getMaximum() );
    }
}
