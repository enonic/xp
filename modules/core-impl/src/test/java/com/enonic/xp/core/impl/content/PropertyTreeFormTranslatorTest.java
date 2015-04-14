package com.enonic.xp.core.impl.content;

import org.junit.Test;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypeException;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.inputtype.DateTimeConfig;
import com.enonic.xp.form.inputtype.InputTypes;

import static org.junit.Assert.*;

public class PropertyTreeFormTranslatorTest
{
    @Test
    public void nestedProperties()
        throws Exception
    {
        PropertyTree data = new PropertyTree();
        final PropertySet myProps = data.addSet( "myProps" );
        myProps.addString( "myHtmlArea", "<h1>This is HTML</1>" );
        final PropertySet myOtherProps = data.addSet( "myOtherProps" );
        myOtherProps.addString( "myHtmlArea", "<h1>This is HTML</1>" );

        final Form form = Form.newForm().
            addFormItem( FormItemSet.newFormItemSet().
                name( "myProps" ).
                addFormItem( Input.newInput().
                    name( "myHtmlArea" ).
                    inputType( InputTypes.HTML_AREA ).
                    build() ).
                build() ).
            addFormItem( FormItemSet.newFormItemSet().
                name( "myOtherProps" ).
                addFormItem( Input.newInput().
                    name( "myHtmlArea" ).
                    inputType( InputTypes.HTML_AREA ).
                    build() ).
                build() ).
            build();

        final PropertyTree transformedTree = PropertyTreeFormTranslator.transform( data, form );

        assertNotNull( transformedTree.getProperty( "myProps.myHtmlArea" ) );
        assertEquals( ValueTypes.STRING.toString(), transformedTree.getProperty( "myProps.myHtmlArea" ).getType().toString() );
        assertEquals( ValueTypes.STRING.toString(), transformedTree.getProperty( "myOtherProps.myHtmlArea" ).getType().toString() );
    }

    @Test
    public void datetime()
        throws Exception
    {
        PropertyTree data = new PropertyTree();
        data.addString( "noTimezone", "2015-04-13T10:00:00" );
        data.addString( "timezone", "2015-04-13T10:00:00+02:00" );

        final Form form = Form.newForm().
            addFormItem( Input.newInput().
                name( "noTimezone" ).
                inputType( InputTypes.DATE_TIME ).
                inputTypeConfig( DateTimeConfig.create().
                    withTimezone( false ).
                    build() ).
                build() ).
            addFormItem( Input.newInput().
                name( "timezone" ).
                inputType( InputTypes.DATE_TIME ).
                inputTypeConfig( DateTimeConfig.create().
                    withTimezone( true ).
                    build() ).
                build() ).
            build();

        final PropertyTree transformedTree = PropertyTreeFormTranslator.transform( data, form );

        assertNotNull( transformedTree.getProperty( "noTimezone" ) );
        assertNotNull( transformedTree.getProperty( "timezone" ) );
        assertEquals( ValueTypes.LOCAL_DATE_TIME.toString(), transformedTree.getProperty( "noTimezone" ).getType().toString() );
        assertEquals( ValueTypes.DATE_TIME.toString(), transformedTree.getProperty( "timezone" ).getType().toString() );
    }

    @Test(expected = ValueTypeException.class)
    public void dateTime_invalid_value()
        throws Exception
    {
        PropertyTree data = new PropertyTree();
        data.addString( "noTimezone", "fiskekake" );
        //data.addString( "timezone", "2015-04-13T10:00:00+02:00" );

        final Form form = Form.newForm().
            addFormItem( Input.newInput().
                name( "noTimezone" ).
                inputType( InputTypes.DATE_TIME ).
                build() ).
            build();

        final PropertyTree transformedTree = PropertyTreeFormTranslator.transform( data, form );

        assertNotNull( transformedTree.getProperty( "noTimezone" ) );
        assertNotNull( transformedTree.getProperty( "timezone" ) );
        assertEquals( ValueTypes.LOCAL_DATE_TIME.toString(), transformedTree.getProperty( "noTimezone" ).getType().toString() );
        assertEquals( ValueTypes.DATE_TIME.toString(), transformedTree.getProperty( "timezone" ).getType().toString() );
    }

}