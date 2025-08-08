package com.enonic.xp.xml.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormItemType;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.Occurrences;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XmlContentTypeParserTest
    extends XmlModelParserTest
{
    private XmlContentTypeParser parser;

    private ContentType.Builder builder;

    @BeforeEach
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
        assertEquals( "${firstName} ${lastName}", result.getDisplayNameExpression() );
        assertEquals( "Display Name Label", result.getDisplayNameLabel() );
        assertEquals( "myapplication:content", result.getSuperType().toString() );
        assertEquals( false, result.isAbstract() );
        assertEquals( true, result.isFinal() );

        assertEquals( 4, result.getForm().size() );

        final Input item = result.getForm().getInput( "myDate" );

        assertEquals( InputTypeName.DATE.toString(), item.getInputType().toString() );

        final Input contentSelectorInput = result.getForm().getInput( "someonesParent" );
        assertNotNull( contentSelectorInput );

        assertEquals( InputTypeName.CONTENT_SELECTOR.toString(), contentSelectorInput.getInputType().toString() );

        assertEquals( "mytype", contentSelectorInput.getInputTypeConfig().getProperty( "allowContentType" ).getValue() );
        assertEquals( 2, contentSelectorInput.getInputTypeConfig().getProperties( "allowContentType" ).size() );
        assertEquals( "path1", contentSelectorInput.getInputTypeConfig().getProperty( "allowPath" ).getValue() );
        assertEquals( 2, contentSelectorInput.getInputTypeConfig().getProperties( "allowPath" ).size() );

        final InputTypeConfig config = item.getInputTypeConfig();
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
        assertEquals( "translated.description", result.getDescriptionI18nKey() );
        assertEquals( "translated.displayNameLabel", result.getDisplayNameLabelI18nKey() );

    }

    @Test
    public void testI18n_formInput()
        throws Exception
    {
        parse( this.parser, "-i18n.xml" );
        final ContentType result = this.builder.build();

        final Input input = result.getForm().getInput( "textLine" );

        assertEquals( "translated.label", input.getLabelI18nKey() );
        assertEquals( "translated.help-text", input.getHelpTextI18nKey() );

    }

    @Test
    public void testI18n_optionSet()
        throws Exception
    {
        parse( this.parser, "-i18n.xml" );
        final ContentType result = this.builder.build();

        final Form form = result.getForm();
        FormOptionSet radioOptionSet = form.getOptionSet( "radioOptionSet" );

        assertEquals( FormItemType.FORM_OPTION_SET, radioOptionSet.getType() );

        assertEquals( "translated.help-text", radioOptionSet.getHelpTextI18nKey() );
        assertEquals( "translated.label", radioOptionSet.getLabelI18nKey() );

        final Input inputInsideOption = form.getInput( "radioOptionSet.option_1.text-input" );

        assertEquals( "translated.help-text", inputInsideOption.getHelpTextI18nKey() );
        assertEquals( "translated.label", inputInsideOption.getLabelI18nKey() );

        final FormOptionSetOption radioOption = radioOptionSet.getOption( "option_1" );

        assertEquals( "translated.help-text", radioOption.getHelpTextI18nKey() );
        assertEquals( "translated.label", radioOption.getLabelI18nKey() );
    }

    @Test
    public void testI18n_fieldSet()
        throws Exception
    {
        parse( this.parser, "-i18n.xml" );
        final ContentType result = this.builder.build();

        FieldSet fieldSet = null;
        for ( FormItem formItem : result.getForm() )
        {
            if ( FormItemType.LAYOUT.equals( formItem.getType() ) )
            {
                fieldSet = (FieldSet) formItem;
                break;
            }
        }
        assertNotNull( fieldSet );

        assertEquals( "translated.label", fieldSet.getLabelI18nKey() );

        final Input inputInsideFieldSet = result.getForm().getInput( "textLine2" );

        assertEquals( "translated.help-text", inputInsideFieldSet.getHelpTextI18nKey() );
        assertEquals( "translated.label", inputInsideFieldSet.getLabelI18nKey() );
    }

    @Test
    public void testI18n_itemSet()
        throws Exception
    {
        parse( this.parser, "-i18n.xml" );
        final ContentType result = this.builder.build();

        final FormItemSet formItemSet = result.getForm().getFormItemSet( "item-set" );
        assertNotNull( formItemSet );

        assertEquals( "translated.label", formItemSet.getLabelI18nKey() );
        assertEquals( "translated.help-text", formItemSet.getHelpTextI18nKey() );

        final Input inputInsideFormItemSet = formItemSet.getInput( "textLine1" );

        assertEquals( "translated.help-text", inputInsideFormItemSet.getHelpTextI18nKey() );
        assertEquals( "translated.label", inputInsideFormItemSet.getLabelI18nKey() );
    }

    @Test
    public void testI18nParseOmittingElementValue()
        throws Exception
    {
        parse( this.parser, "-i18n-only-attribute.xml" );
        final ContentType result = this.builder.build();

        assertEquals( "translated.display-name", result.getDisplayNameI18nKey() );
        assertEquals( "translated.description", result.getDescriptionI18nKey() );

        // input type
        final Input input = result.getForm().getInput( "textLine" );

        assertEquals( "translated.label", input.getLabelI18nKey() );
        assertEquals( "translated.label", input.getLabel() );
        assertEquals( "translated.help-text", input.getHelpTextI18nKey() );
        assertEquals( "translated.help-text", input.getHelpText() );

        // option set
        final FormOptionSet radioOptionSet = result.getForm().getOptionSet( "radioOptionSet" );
        assertNotNull( radioOptionSet );

        assertEquals( FormItemType.FORM_OPTION_SET, radioOptionSet.getType() );

        assertEquals( "translated.help-text", radioOptionSet.getHelpTextI18nKey() );
        assertEquals( "translated.help-text", radioOptionSet.getHelpText() );
        assertEquals( "translated.label", radioOptionSet.getLabelI18nKey() );
        assertEquals( "translated.label", radioOptionSet.getLabel() );

        final FormOptionSetOption radioOption = radioOptionSet.getOption( "option_1" );;

        assertEquals( "translated.help-text", radioOption.getHelpTextI18nKey() );
        assertEquals( "translated.help-text", radioOption.getHelpText() );
        assertEquals( "translated.label", radioOption.getLabelI18nKey() );
        assertEquals( "translated.label", radioOption.getLabel() );

        final Input inputInsideOption = radioOptionSet.getOption( "option_1" ).getInput( "text-input" );

        assertEquals( "translated.help-text", inputInsideOption.getHelpTextI18nKey() );
        assertEquals( "translated.help-text", inputInsideOption.getHelpText() );
        assertEquals( "translated.label", inputInsideOption.getLabelI18nKey() );
        assertEquals( "translated.label", inputInsideOption.getLabel() );


        // field set
        FieldSet fieldSet = null;
        for ( FormItem formItem : result.getForm() )
        {
            if ( FormItemType.LAYOUT.equals( formItem.getType() ) )
            {
                fieldSet = (FieldSet) formItem;
                break;
            }
        }
        assertNotNull( fieldSet );

        assertEquals( "translated.label", fieldSet.getLabelI18nKey() );
        assertEquals( "translated.label", fieldSet.getLabel() );

        final Input inputInsideFieldSet = result.getForm().getInput( "textLine2" );

        assertEquals( "translated.help-text", inputInsideFieldSet.getHelpTextI18nKey() );
        assertEquals( "translated.help-text", inputInsideFieldSet.getHelpText() );
        assertEquals( "translated.label", inputInsideFieldSet.getLabelI18nKey() );
        assertEquals( "translated.label", inputInsideFieldSet.getLabel() );

        // item set
        final FormItemSet formItemSet = result.getForm().getFormItemSet( "item-set" );
        assertNotNull( formItemSet );

        assertEquals( "translated.label", formItemSet.getLabelI18nKey() );
        assertEquals( "translated.label", formItemSet.getLabel() );
        assertEquals( "translated.help-text", formItemSet.getHelpTextI18nKey() );
        assertEquals( "translated.help-text", formItemSet.getHelpText() );

        final Input inputInsideFormItemSet = formItemSet.getInput( "textLine1" );

        assertEquals( "translated.help-text", inputInsideFormItemSet.getHelpTextI18nKey() );
        assertEquals( "translated.help-text", inputInsideFormItemSet.getHelpText() );
        assertEquals( "translated.label", inputInsideFormItemSet.getLabelI18nKey() );
        assertEquals( "translated.label", inputInsideFormItemSet.getLabel() );
    }

    private void assertOptionSetResult()
        throws Exception
    {
        final ContentType result = this.builder.build();
        assertEquals( "myapplication:mytype", result.getName().toString() );
        assertEquals( "OptionSet", result.getDisplayName() );
        assertEquals( "OptionSet for testing", result.getDescription() );
        assertEquals( "base:structured", result.getSuperType().toString() );
        assertFalse( result.isAbstract() );
        assertFalse( result.isFinal() );

        assertEquals( 2, result.getForm().size() );

        final FormOptionSet item = result.getForm().getOptionSet( "radioOptionSet" );
        assertNotNull( item );

        final FormOptionSet radioOptionSet = (FormOptionSet) item;
        assertEquals( FormItemType.FORM_OPTION_SET, radioOptionSet.getType() );

        assertThat( radioOptionSet ).size().isEqualTo( 2 );

        FormOptionSetOption radioOption1 = radioOptionSet.getOption("option_1");
        FormOptionSetOption radioOption2 = radioOptionSet.getOption( "option_2" );

        assertEquals( FormItemType.FORM_OPTION_SET_OPTION, radioOption1.getType() );
        assertEquals( FormItemType.FORM_OPTION_SET_OPTION, radioOption2.getType() );

        assertFalse( radioOption1.isDefaultOption() );
        assertFalse( radioOption2.isDefaultOption() );

        assertThat( radioOption2 ).isEmpty();
        assertThat( radioOption1 ).size().isEqualTo( 2 );

        final Input textInput = radioOption1.getInput("text-input" );
        assertEquals( InputTypeName.TEXT_LINE.toString(), textInput.getInputType().toString() );
        assertEquals( "Text input", textInput.getHelpText() );

        final FormItemSet formItemSet = radioOption1.getFormItemSet( "minimum3" ).toFormItemSet();
        assertThat( formItemSet ).size().isEqualTo( 2 );

        final FormOptionSet checkOptionSet = result.getForm().getOptionSet( "checkOptionSet" );
        assertEquals( FormItemType.FORM_OPTION_SET, checkOptionSet.getType() );

        assertEquals( "Multi selection", checkOptionSet.getLabel() );
        assertTrue( checkOptionSet.isExpanded() );

        final Occurrences checkOptionSetOccurrences = checkOptionSet.getOccurrences();
        assertEquals( 0, checkOptionSetOccurrences.getMinimum() );
        assertEquals( 1, checkOptionSetOccurrences.getMaximum() );

        assertThat( checkOptionSet ).size().isEqualTo( 4 );

        //check option set 1st option
        final FormOptionSetOption checkOption1 = checkOptionSet.getOption( "option_1" );
        assertTrue( checkOption1.isDefaultOption() );
        assertThat( checkOption1 ).isEmpty();

        //check option set 2nd option
        final FormOptionSetOption checkOption2 = checkOptionSet.getOption( "option_2" );
        assertTrue( checkOption2.isDefaultOption() );

        assertThat( checkOption2 ).size().isEqualTo( 1 );

        // nested option set
        final FormOptionSet nestedOptionSet = checkOption2.getOptionSet( "nestedOptionSet" );

        assertThat( nestedOptionSet ).size().isEqualTo( 2 );
        assertFalse( nestedOptionSet.isExpanded() );

        final FormOptionSetOption nestedSetOption1 = nestedOptionSet.getOption( "option2_1" );
        assertFalse( nestedSetOption1.isDefaultOption() );
        assertThat( nestedSetOption1 ).size().isEqualTo( 1 );

        final FormOptionSetOption nestedSetOption2 = nestedOptionSet.getOption( "option2_2" );
        assertTrue( nestedSetOption2.isDefaultOption() );
        assertThat( nestedSetOption2 ).size().isEqualTo( 1 );

        //check option set 3rd option
        final FormOptionSetOption checkOption3 = checkOptionSet.getOption( "option_3" );
        assertFalse( checkOption3.isDefaultOption() );

        assertThat( checkOption3 ).size().isEqualTo( 1 );

        final Input imageSelectorInput = checkOption3.getInput( "imageselector" );

        assertEquals( "mytype", imageSelectorInput.getInputTypeConfig().getProperty( "allowContentType" ).getValue() );
        assertEquals( 2, imageSelectorInput.getInputTypeConfig().getProperties( "allowContentType" ).size() );
        assertEquals( "path1", imageSelectorInput.getInputTypeConfig().getProperty( "allowPath" ).getValue() );
        assertEquals( 2, imageSelectorInput.getInputTypeConfig().getProperties( "allowPath" ).size() );

        final Occurrences imageSelectorOccurrences = imageSelectorInput.getOccurrences();
        assertEquals( 1, imageSelectorOccurrences.getMinimum() );
        assertEquals( 1, imageSelectorOccurrences.getMaximum() );

        //check option set 4th option
        final FormOptionSetOption checkOption4 = checkOptionSet.getOption( "option_4" );
        assertFalse( checkOption4.isDefaultOption() );
        assertThat( checkOption4 ).size().isEqualTo( 2 );

        final Input doubleInput = checkOption4.getInput( "double" );
        final Input longInput = checkOption4.getInput( "long" );
        assertEquals( InputTypeName.DOUBLE.toString(), doubleInput.getInputType().toString() );
        assertEquals( InputTypeName.LONG.toString(), longInput.getInputType().toString() );

        final Occurrences longInputOccurrences = longInput.getOccurrences();
        assertEquals( 0, longInputOccurrences.getMinimum() );
        assertEquals( 1, longInputOccurrences.getMaximum() );
    }
}
