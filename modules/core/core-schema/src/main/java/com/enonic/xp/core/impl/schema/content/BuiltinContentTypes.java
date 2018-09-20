package com.enonic.xp.core.impl.schema.content;

import java.util.List;

import org.apache.commons.lang.WordUtils;

import com.google.common.collect.Lists;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.core.impl.schema.SchemaHelper;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.schema.xdata.XDataNames;

final class BuiltinContentTypes
{
    private static final String CONTENT_TYPES_FOLDER = "content-types";

    // System Content Types
    private static final ContentType STRUCTURED = createSystemType( ContentTypeName.structured() ).
        setFinal( false ).setAbstract( true ).build();

    private static final ContentType UNSTRUCTURED = createSystemType( ContentTypeName.unstructured() ).
        setFinal( false ).setAbstract( false ).build();

    private static final ContentType FOLDER = createSystemType( ContentTypeName.folder() ).
        setFinal( false ).setAbstract( false ).build();

    private static final Form SITE_FORM = Form.create().
        addFormItem( Input.create().
            name( "description" ).
            label( "Description" ).
            labelI18nKey( "portal.site.description.label" ).
            inputType( InputTypeName.TEXT_AREA ).
            occurrences( 0, 1 ).
            build() ).
        addFormItem( Input.create().
            name( "siteConfig" ).
            label( "Applications" ).
            labelI18nKey( "portal.site.applications.label" ).
            helpText( "Configure applications used by this site" ).
            helpTextI18nKey( "portal.site.applications.helpText" ).
            inputType( InputTypeName.SITE_CONFIGURATOR ).
            required( false ).
            multiple( true ).
            build() ).
        build();

    private static final Form SHORTCUT_FORM = Form.create().
        addFormItem( Input.create().
            name( "target" ).
            label( "Target" ).
            labelI18nKey( "base.shortcut.target.label" ).
            helpText( "Choose shortcut target" ).
            helpTextI18nKey( "base.shortcut.target.helpText" ).
            inputType( InputTypeName.CONTENT_SELECTOR ).
            inputTypeProperty( InputTypeProperty.create( "relationshipType", RelationshipTypeName.REFERENCE.toString() ).build() ).
            required( true ).
            build() ).
        addFormItem( FormItemSet.create().name( "parameters" ).
            label( "Parameters" ).
            labelI18nKey( "base.shortcut.parameters.label" ).
            helpText( "HTTP Parameters" ).
            helpTextI18nKey( "base.shortcut.parameters.helpText" ).
            multiple( true ).required( false ).
            addFormItem( Input.create().
                name( "name" ).
                label( "Name" ).
                labelI18nKey( "base.shortcut.parameters.name.label" ).
                helpText( "HTTP parameter name" ).
                helpTextI18nKey( "base.shortcut.parameters.name.helpText" ).
                inputType( InputTypeName.TEXT_LINE ).
                occurrences( 1, 1 ).
                build() ).
            addFormItem( Input.create().
                name( "value" ).
                label( "Value" ).
                labelI18nKey( "base.shortcut.parameters.value.label" ).
                helpText( "HTTP parameter value" ).
                helpTextI18nKey( "base.shortcut.parameters.value.helpText" ).
                inputType( InputTypeName.TEXT_LINE ).
                occurrences( 1, 1 ).
                build() ).
            build() ).
        build();

    private static final Form MEDIA_IMAGE_FORM = Form.create().
        addFormItem( Input.create().name( ContentPropertyNames.MEDIA ).
            label( "Image" ).
            labelI18nKey( "media.image.media.label" ).
            inputType( InputTypeName.IMAGE_UPLOADER ).build() ).
        addFormItem( Input.create().name( "caption" ).
            inputType( InputTypeName.TEXT_AREA ).
            label( "Caption" ).
            labelI18nKey( "media.image.caption.label" ).
            occurrences( 0, 1 ).
            build() ).
        addFormItem( Input.create().name( "artist" ).
            inputType( InputTypeName.TAG ).
            label( "Artist" ).
            labelI18nKey( "media.image.artist.label" ).
            occurrences( 0, 0 ).
            build() ).
        addFormItem( Input.create().name( "copyright" ).
            inputType( InputTypeName.TEXT_LINE ).
            label( "Copyright" ).
            labelI18nKey( "media.image.copyright.label" ).
            occurrences( 0, 1 ).
            build() ).
        addFormItem( Input.create().name( "tags" ).
            inputType( InputTypeName.TAG ).
            label( "Tags" ).
            labelI18nKey( "media.image.tags.label" ).
            occurrences( 0, 0 ).
            build() ).
        build();

    private static final Form MEDIA_VECTOR_FORM = Form.create().
        addFormItem( Input.create().name( ContentPropertyNames.MEDIA ).
            label( "Media" ).
            labelI18nKey( "media.vector.media.label" ).
            inputType( InputTypeName.MEDIA_UPLOADER ).build() ).
        addFormItem( Input.create().name( "caption" ).
            inputType( InputTypeName.TEXT_AREA ).
            label( "Caption" ).
            labelI18nKey( "media.vector.caption.label" ).
            occurrences( 0, 1 ).
            build() ).
        addFormItem( Input.create().name( "artist" ).
            inputType( InputTypeName.TAG ).
            label( "Artist" ).
            labelI18nKey( "media.vector.artist.label" ).
            occurrences( 0, 0 ).
            build() ).
        addFormItem( Input.create().name( "copyright" ).
            inputType( InputTypeName.TEXT_LINE ).
            label( "Copyright" ).
            labelI18nKey( "media.vector.copyright.label" ).
            occurrences( 0, 1 ).
            build() ).
        addFormItem( Input.create().name( "tags" ).
            inputType( InputTypeName.TAG ).
            label( "Tags" ).
            labelI18nKey( "media.vector.tags.label" ).
            occurrences( 0, 0 ).
            build() ).
        build();

    private static final Form MEDIA_DOCUMENT_FORM = Form.create().
        addFormItem( Input.create().name( ContentPropertyNames.MEDIA ).
            label( "Media" ).
            labelI18nKey( "media.document.media.label" ).
            inputType( InputTypeName.MEDIA_UPLOADER ).build() ).
        addFormItem( Input.create().name( "abstract" ).
            inputType( InputTypeName.TEXT_AREA ).
            label( "Abstract" ).
            labelI18nKey( "media.document.abstract.label" ).
            occurrences( 0, 1 ).
            build() ).
        addFormItem( Input.create().name( "tags" ).
            inputType( InputTypeName.TAG ).
            label( "Tags" ).
            labelI18nKey( "media.document.tags.label" ).
            occurrences( 0, 0 ).
            build() ).
        build();

    private static final Form MEDIA_DEFAULT_FORM = Form.create().
        addFormItem( Input.create().name( ContentPropertyNames.MEDIA ).
            label( "Media" ).
            labelI18nKey( "media.default.media.label" ).
            inputType( InputTypeName.MEDIA_UPLOADER ).build() ).
        addFormItem( Input.create().name( "tags" ).
            inputType( InputTypeName.TAG ).
            label( "Tags" ).
            labelI18nKey( "media.default.tags.label" ).
            occurrences( 0, 0 ).
            build() ).
        build();

    private static final ContentType SITE = createSystemType( ContentTypeName.site() ).
        description( "Root content for sites" ).
        descriptionI18nKey( "portal.site.description" ).
        setFinal( true ).
        setAbstract( false ).
        form( SITE_FORM ).
        superType( ContentTypeName.structured() ).
        build();

    private static final ContentType TEMPLATE_FOLDER = createSystemType( ContentTypeName.templateFolder() ).
        setFinal( true ).
        setAbstract( false ).
        superType( ContentTypeName.folder() ).
        build();

    private static final Form PAGE_TEMPLATE_FORM = Form.create().
        addFormItem( Input.create().
            name( "supports" ).
            label( "Supports" ).
            labelI18nKey( "portal.page-template.supports.label" ).
            helpText( "Choose which content types this page template supports" ).
            helpTextI18nKey( "portal.page-template.supports.helpText" ).
            inputType( InputTypeName.CONTENT_TYPE_FILTER ).
            required( true ).
            multiple( true ).
            build() ).
        build();

    private static final Form FRAGMENT_FORM = Form.create().build();

    private static final ContentType PAGE_TEMPLATE = createSystemType( ContentTypeName.pageTemplate() ).
        allowChildContent( false ).
        setFinal( false ).
        setAbstract( false ).
        form( PAGE_TEMPLATE_FORM ).
        superType( ContentTypeName.structured() ).
        build();

    private static final ContentType SHORTCUT = createSystemType( ContentTypeName.shortcut() ).
        setFinal( true ).
        setAbstract( false ).
        form( SHORTCUT_FORM ).
        superType( ContentTypeName.shortcut() ).
        build();

    private static final ContentType FRAGMENT = createSystemType( ContentTypeName.fragment() ).
        allowChildContent( true ).
        setFinal( true ).
        setAbstract( false ).
        form( FRAGMENT_FORM ).
        superType( ContentTypeName.structured() ).
        build();

    private static final ContentType MEDIA = createSystemType( ContentTypeName.media() ).
        setFinal( false ).setAbstract( false ).allowChildContent( false ).build();

    private static final ContentType MEDIA_TEXT = createSystemType( ContentTypeName.textMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).form( MEDIA_DEFAULT_FORM ).build();

    private static final ContentType MEDIA_DATA = createSystemType( ContentTypeName.dataMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).form( MEDIA_DEFAULT_FORM ).build();

    private static final ContentType MEDIA_AUDIO = createSystemType( ContentTypeName.audioMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).form( MEDIA_DEFAULT_FORM ).build();

    private static final ContentType MEDIA_VIDEO = createSystemType( ContentTypeName.videoMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).form( MEDIA_DEFAULT_FORM ).build();

    private static final ContentType MEDIA_IMAGE = createSystemType( ContentTypeName.imageMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).form( MEDIA_IMAGE_FORM ).
        metadata( XDataNames.from( MediaInfo.IMAGE_INFO_METADATA_NAME, MediaInfo.CAMERA_INFO_METADATA_NAME,
                                   MediaInfo.GPS_INFO_METADATA_NAME ) ).build();

    private static final ContentType MEDIA_VECTOR = createSystemType( ContentTypeName.vectorMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).form( MEDIA_VECTOR_FORM ).build();

    private static final ContentType MEDIA_ARCHIVE =
        createSystemType( ContentTypeName.archiveMedia() ).superType( ContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).form( MEDIA_DEFAULT_FORM ).build();

    private static final ContentType MEDIA_DOCUMENT =
        createSystemType( ContentTypeName.documentMedia() ).superType( ContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).form( MEDIA_DOCUMENT_FORM ).build();

    private static final ContentType MEDIA_SPREADSHEET =
        createSystemType( ContentTypeName.spreadsheetMedia() ).superType( ContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).form( MEDIA_DEFAULT_FORM ).build();

    private static final ContentType MEDIA_PRESENTATION =
        createSystemType( ContentTypeName.presentationMedia() ).superType( ContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).form( MEDIA_DEFAULT_FORM ).build();

    private static final ContentType MEDIA_CODE = createSystemType( ContentTypeName.codeMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).form( MEDIA_DEFAULT_FORM ).build();

    private static final ContentType MEDIA_EXECUTABLE =
        createSystemType( ContentTypeName.executableMedia() ).superType( ContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).form( MEDIA_DEFAULT_FORM ).build();

    private static final ContentType MEDIA_UNKNOWN =
        createSystemType( ContentTypeName.unknownMedia() ).superType( ContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).form( MEDIA_DEFAULT_FORM ).build();

    private static final ContentTypes CONTENT_TYPES =
        ContentTypes.from( UNSTRUCTURED, STRUCTURED, FOLDER, SHORTCUT, MEDIA, MEDIA_TEXT, MEDIA_DATA, MEDIA_AUDIO, MEDIA_VIDEO, MEDIA_IMAGE,
                           MEDIA_VECTOR, MEDIA_ARCHIVE, MEDIA_DOCUMENT, MEDIA_SPREADSHEET, MEDIA_PRESENTATION, MEDIA_CODE, MEDIA_EXECUTABLE,
                           MEDIA_UNKNOWN, SITE, TEMPLATE_FOLDER, PAGE_TEMPLATE, FRAGMENT );

    private static ContentType.Builder createSystemType( final ContentTypeName contentTypeName )
    {
        final String displayName = WordUtils.capitalize( contentTypeName.getLocalName() );
        final String app = contentTypeName.getApplicationKey().getName();
        return ContentType.create().
            name( contentTypeName ).
            displayName( displayName ).
            displayNameI18nKey( app + "." + contentTypeName.getLocalName().toLowerCase() + ".displayName" ).
            setBuiltIn();
    }

    private final ContentTypes contentTypes;

    public BuiltinContentTypes()
    {
        this.contentTypes = processTypes( CONTENT_TYPES );
    }

    private ContentType processType( final ContentType type )
    {
        return ContentType.create( type ).
            icon( loadSchemaIcon( CONTENT_TYPES_FOLDER, type.getName().getLocalName() ) ).
            build();
    }

    private ContentTypes processTypes( final ContentTypes types )
    {
        final List<ContentType> result = Lists.newArrayList();
        for ( final ContentType type : types )
        {
            result.add( processType( type ) );
        }

        return ContentTypes.from( result );
    }

    public ContentTypes getAll()
    {
        return this.contentTypes;
    }

    public ContentTypes getByApplication( final ApplicationKey key )
    {
        return this.contentTypes.filter( ( type ) -> type.getName().getApplicationKey().equals( key ) );
    }

    private Icon loadSchemaIcon( final String metaInfFolderName, final String name )
    {
        return SchemaHelper.loadIcon( getClass(), metaInfFolderName, name );
    }
}
