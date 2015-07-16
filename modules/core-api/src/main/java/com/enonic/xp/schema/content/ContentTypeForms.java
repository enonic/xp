package com.enonic.xp.schema.content;

import java.util.LinkedHashMap;

import com.google.common.annotations.Beta;

import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.inputtype.ContentSelectorConfig;
import com.enonic.xp.form.inputtype.InputTypes;
import com.enonic.xp.schema.relationship.RelationshipTypeName;

@Beta
public final class ContentTypeForms
{
    public static final Form SITE = Form.create().
        addFormItem( Input.create().
            name( "description" ).
            label( "Description" ).
            inputType( InputTypes.TEXT_AREA ).
            occurrences( 0, 1 ).
            helpText( "Description of the site. Optional" ).
            build() ).
        addFormItem( Input.create().
            name( "siteConfig" ).
            label( "Site config" ).
            helpText( "Configure modules needed for the Site" ).
            inputType( InputTypes.SITE_CONFIGURATOR ).
            required( false ).
            multiple( true ).
            maximizeUIInputWidth( true ).
            build() ).
        build();

    public static final Form PAGE_TEMPLATE = Form.create().
        addFormItem( Input.create().
            name( "supports" ).
            label( "Supports" ).
            helpText( "Choose which content types this page template supports" ).
            inputType( InputTypes.CONTENT_TYPE_FILTER ).
            required( true ).
            multiple( true ).
            build() ).
        build();

    public static final String SHORTCUT_TARGET_PROPERTY = "target";

    public static final Form SHORTCUT = Form.create().
        addFormItem( Input.create().
            name( SHORTCUT_TARGET_PROPERTY ).
            label( "Target" ).
            helpText( "Choose shortcut target" ).
            inputType( InputTypes.CONTENT_SELECTOR ).
            inputTypeConfig( ContentSelectorConfig.create().relationshipType( RelationshipTypeName.REFERENCE ).build() ).
            required( true ).
            build() ).
        build();

    public static final Form MEDIA_IMAGE = Form.create().

        addFormItem( Input.create().name( ContentPropertyNames.MEDIA ).
            label( "Image" ).
            maximizeUIInputWidth( true ).
            inputType( InputTypes.IMAGE_UPLOADER ).build() ).
        addFormItem( Input.create().name( "caption" ).
            inputType( InputTypes.TEXT_AREA ).
            label( "Caption" ).
            occurrences( 0, 1 ).
            build() ).
        addFormItem( Input.create().name( "artist" ).
            inputType( InputTypes.TAG ).
            label( "Artist" ).
            occurrences( 0, 0 ).
            build() ).
        addFormItem( Input.create().name( "copyright" ).
            inputType( InputTypes.TEXT_LINE ).
            label( "Copyright" ).
            occurrences( 0, 1 ).
            build() ).
        addFormItem( Input.create().name( "tags" ).
            inputType( InputTypes.TAG ).
            label( "Tags" ).
            occurrences( 0, 0 ).
            build() ).
        build();

    public static final Form MEDIA_DEFAULT = Form.create().
        addFormItem( Input.create().name( ContentPropertyNames.MEDIA ).
            label( "Media" ).
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
