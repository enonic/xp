package com.enonic.xp.core.impl.content.index;

import org.junit.Test;

import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.Input;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.inputtype.InputTypeName;

import static org.junit.Assert.*;

public class IndexConfigVisitorTest
{
    @Test
    public void top_htmlArea()
        throws Exception
    {
        Input myTextLine = Input.create().
            name( "htmlArea" ).
            inputType( InputTypeName.HTML_AREA ).
            label( "htmlArea" ).
            required( true ).
            build();
        Form form = Form.create().
            addFormItem( myTextLine ).
            build();

        final PatternIndexConfigDocument.Builder builder = PatternIndexConfigDocument.create();

        final IndexConfigVisitor validationVisitor = new IndexConfigVisitor( "parent", builder );
        validationVisitor.traverse( form );

        final PatternIndexConfigDocument document = builder.build();
        assertEquals( 1, document.getPathIndexConfigs().size() );
        assertEquals( "htmlStripper", document.getConfigForPath( PropertyPath.from( "parent.htmlArea") ).getIndexValueProcessors().get( 0 ).getName() );
    }

    @Test
    public void htmlArea_in_itemSet()
        throws Exception
    {
        FormItemSet myFormItemSet = FormItemSet.create().
            name( "myFormItemSet" ).
            label( "My form item set" ).
            addFormItem( Input.create().
                name( "htmlArea" ).
                inputType( InputTypeName.HTML_AREA ).
                label( "htmlArea" ).
                required( false ).
                build() ).
            build();

        Form form = Form.create().
            addFormItem( myFormItemSet ).
            build();

        final PatternIndexConfigDocument.Builder builder = PatternIndexConfigDocument.create();

        final IndexConfigVisitor validationVisitor = new IndexConfigVisitor( "parent", builder );
        validationVisitor.traverse( form );

        final PatternIndexConfigDocument document = builder.build();
        assertEquals( 1, document.getPathIndexConfigs().size() );
        assertEquals( "htmlStripper", document.getConfigForPath( PropertyPath.from( "parent.myFormItemSet.htmlArea") ).getIndexValueProcessors().get( 0 ).getName() );
    }

    @Test
    public void htmlArea_in_optionSet()
        throws Exception
    {
        FormOptionSet formOptionSet = FormOptionSet.create().
            name( "myOptionSet" ).
            label( "My option set" ).
            helpText( "Option set help text" ).
            addOptionSetOption(
                FormOptionSetOption.create().name( "myOptionSetOption1" ).label( "option label1" ).helpText( "Option help text" ).
                    addFormItem( Input.create().name( "myTextLine1" ).label( "textArea" ).inputType(
                        InputTypeName.TEXT_AREA ).build() ).build() ).
            addOptionSetOption(
                FormOptionSetOption.create().name( "myOptionSetOption2" ).label( "option label2" ).helpText( "Option help text" ).
                    addFormItem( Input.create().name( "htmlArea" ).label( "htmlArea" ).inputType(
                        InputTypeName.HTML_AREA ).build() ).build() ).
            build();

        Form form = Form.create().
            addFormItem( formOptionSet ).
            build();

        final PatternIndexConfigDocument.Builder builder = PatternIndexConfigDocument.create();

        final IndexConfigVisitor validationVisitor = new IndexConfigVisitor( "parent", builder );
        validationVisitor.traverse( form );

        final PatternIndexConfigDocument document = builder.build();
        assertEquals( 1, document.getPathIndexConfigs().size() );
        assertEquals( "htmlStripper", document.getConfigForPath( PropertyPath.from( "parent.myoptionset.myoptionsetoption2.htmlArea") ).getIndexValueProcessors().get( 0 ).getName() );
    }
}