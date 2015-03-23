package com.enonic.xp.schema.content;

import java.util.LinkedHashMap;

import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.inputtype.ContentSelectorConfig;
import com.enonic.xp.form.inputtype.InputTypes;
import com.enonic.xp.schema.relationship.RelationshipTypeName;

public final class ContentTypeForms
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
            required( true ).
            multiple( true ).
            build() ).
        build();

    public static final String SHORTCUT_TARGET_PROPERTY = "target";

    public static final Form SHORTCUT = Form.newForm().
        addFormItem( Input.newInput().
            name( SHORTCUT_TARGET_PROPERTY ).
            label( "Target" ).
            helpText( "Choose shortcut target" ).
            inputType( InputTypes.CONTENT_SELECTOR ).
            inputTypeConfig( ContentSelectorConfig.create().relationshipType( RelationshipTypeName.REFERENCE ).build() ).
            required( true ).
            build() ).
        build();

    public static final Form MEDIA_IMAGE = Form.newForm().

        addFormItem( Input.newInput().name( ContentPropertyNames.MEDIA ).
            inputType( InputTypes.IMAGE_UPLOADER ).build() ).
        addFormItem( Input.newInput().name( "caption" ).
            inputType( InputTypes.TEXT_AREA ).
            label( "Caption" ).
            occurrences( 0, 1 ).
            build() ).
        addFormItem( Input.newInput().name( "artist" ).
            inputType( InputTypes.TAG ).
            label( "Artist" ).
            occurrences( 0, 0 ).
            build() ).
        addFormItem( Input.newInput().name( "copyright" ).
            inputType( InputTypes.TEXT_LINE ).
            label( "Copyright" ).
            occurrences( 0, 1 ).
            build() ).
        addFormItem( Input.newInput().name( "tags" ).
            inputType( InputTypes.TAG ).
            label( "Tags" ).
            occurrences( 0, 0 ).
            build() ).
        build();

    public static final Form MEDIA_DEFAULT = Form.newForm().
        addFormItem( Input.newInput().name( ContentPropertyNames.MEDIA ).
            inputType( InputTypes.FILE_UPLOADER ).build() ).
        build();

    private final static LinkedHashMap<ContentTypeName, Form> FORM_BY_NAME = new LinkedHashMap<>();

    static
    {
        FORM_BY_NAME.put( ContentTypeName.pageTemplate(), PAGE_TEMPLATE );
    }

    public static Form get( final ContentTypeName name )
    {
        return FORM_BY_NAME.get( name );
    }
}
