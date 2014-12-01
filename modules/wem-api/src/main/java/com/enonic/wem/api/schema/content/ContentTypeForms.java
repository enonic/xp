package com.enonic.wem.api.schema.content;


import java.util.LinkedHashMap;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;

public class ContentTypeForms
{
    public static final Form SITE = Form.newForm().
        addFormItem( Input.newInput().
            name( "description" ).
            label( "Description" ).
            inputType( InputTypes.TEXT_AREA ).
            occurrences( 0, 1 ).
            helpText( "Description of the site. Optional" ).
            build() ).
        addFormItem( Input.newInput().
            name( "moduleConfig" ).
            label( "Modules" ).
            helpText( "Configure modules needed for the Site" ).
            inputType( InputTypes.MODULE_CONFIGURATOR ).
            required( false ).
            multiple( true ).
            build() ).
        build();

    public static final Form PAGE_TEMPLATE = Form.newForm().
        addFormItem( Input.newInput().
            name( "supports" ).
            label( "Supports" ).
            helpText( "Choose which content types this page template supports" ).
            inputType( InputTypes.CONTENT_TYPE_FILTER ).
            required( false ).
            multiple( true ).
            build() ).
        build();

    public static final Form MEDIA_IMAGE = Form.newForm().
        addFormItem( Input.newInput().name( "image" ).
            inputType( InputTypes.IMAGE ).build() ).
        addFormItem( Input.newInput().name( "mimeType" ).
            inputType( InputTypes.TEXT_LINE ).
            label( "Mime type" ).
            occurrences( 1, 1 ).
            build() ).
        build();

    private final static LinkedHashMap<ContentTypeName, Form> formByName = new LinkedHashMap<>();

    static
    {
        formByName.put( ContentTypeName.pageTemplate(), PAGE_TEMPLATE );
    }

    public static Form get( final ContentTypeName name )
    {
        return formByName.get( name );
    }
}
