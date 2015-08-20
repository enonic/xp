package com.enonic.xp.core.impl.schema.content;

import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.WordUtils;
import org.osgi.service.component.annotations.Component;

import com.google.common.collect.Lists;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.mixin.MixinNames;
import com.enonic.xp.schema.relationship.RelationshipTypeName;

@Component(immediate = true)
public final class BuiltinContentTypeLoader
{
    private static final String CONTENT_TYPES_FOLDER = "content-types";

    // System Content Types
    private static final ContentType STRUCTURED = createSystemType( ContentTypeName.structured() ).
        setFinal( false ).setAbstract( true ).build();

    private static final ContentType UNSTRUCTURED = createSystemType( ContentTypeName.unstructured() ).
        setFinal( false ).setAbstract( false ).build();

    public static final ContentType FOLDER = createSystemType( ContentTypeName.folder() ).
        setFinal( false ).setAbstract( false ).build();

    public static final Form SITE_FORM = Form.create().
        addFormItem( Input.create().
            name( "description" ).
            label( "Description" ).
            inputType( InputTypeName.TEXT_AREA ).
            occurrences( 0, 1 ).
            helpText( "Description of the site. Optional" ).
            build() ).
        addFormItem( Input.create().
            name( "siteConfig" ).
            label( "Site config" ).
            helpText( "Configure applications needed for the Site" ).
            inputType( InputTypeName.SITE_CONFIGURATOR ).
            required( false ).
            multiple( true ).
            maximizeUIInputWidth( true ).
            build() ).
        build();

    public static final Form SHORTCUT_FORM = Form.create().
        addFormItem( Input.create().
            name( "target" ).
            label( "Target" ).
            helpText( "Choose shortcut target" ).
            inputType( InputTypeName.CONTENT_SELECTOR ).
            inputTypeProperty( InputTypeProperty.create( "relationshipType", RelationshipTypeName.REFERENCE.toString() ).build() ).
            required( true ).
            build() ).
        build();

    public static final Form MEDIA_IMAGE_FORM = Form.create().
        addFormItem( Input.create().name( ContentPropertyNames.MEDIA ).
            label( "Image" ).
            maximizeUIInputWidth( true ).
            inputType( InputTypeName.IMAGE_UPLOADER ).build() ).
        addFormItem( Input.create().name( "caption" ).
            inputType( InputTypeName.TEXT_AREA ).
            label( "Caption" ).
            occurrences( 0, 1 ).
            build() ).
        addFormItem( Input.create().name( "artist" ).
            inputType( InputTypeName.TAG ).
            label( "Artist" ).
            occurrences( 0, 0 ).
            build() ).
        addFormItem( Input.create().name( "copyright" ).
            inputType( InputTypeName.TEXT_LINE ).
            label( "Copyright" ).
            occurrences( 0, 1 ).
            build() ).
        addFormItem( Input.create().name( "tags" ).
            inputType( InputTypeName.TAG ).
            label( "Tags" ).
            occurrences( 0, 0 ).
            build() ).
        build();

    public static final Form MEDIA_DEFAULT_FORM = Form.create().
        addFormItem( Input.create().name( ContentPropertyNames.MEDIA ).
            label( "Media" ).
            inputType( InputTypeName.FILE_UPLOADER ).build() ).
        build();

    private static final ContentType SITE = createSystemType( ContentTypeName.site() ).
        description( "Root content for sites" ).
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

    public static final Form PAGE_TEMPLATE_FORM = Form.create().
        addFormItem( Input.create().
            name( "supports" ).
            label( "Supports" ).
            helpText( "Choose which content types this page template supports" ).
            inputType( InputTypeName.CONTENT_TYPE_FILTER ).
            required( true ).
            multiple( true ).
            build() ).
        build();

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
        metadata( MixinNames.from( MediaInfo.IMAGE_INFO_METADATA_NAME, MediaInfo.CAMERA_INFO_METADATA_NAME,
                                   MediaInfo.GPS_INFO_METADATA_NAME ) ).build();

    private static final ContentType MEDIA_VECTOR = createSystemType( ContentTypeName.vectorMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).form( MEDIA_DEFAULT_FORM ).build();

    private static final ContentType MEDIA_ARCHIVE =
        createSystemType( ContentTypeName.archiveMedia() ).superType( ContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).form( MEDIA_DEFAULT_FORM ).build();

    private static final ContentType MEDIA_DOCUMENT =
        createSystemType( ContentTypeName.documentMedia() ).superType( ContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).form( MEDIA_DEFAULT_FORM ).build();

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
                           MEDIA_UNKNOWN, SITE, TEMPLATE_FOLDER, PAGE_TEMPLATE );

    private static ContentType.Builder createSystemType( final ContentTypeName contentTypeName )
    {
        final String displayName = WordUtils.capitalize( contentTypeName.getLocalName() );
        return ContentType.create().
            name( contentTypeName ).
            displayName( displayName ).
            setBuiltIn();
    }

    private List<ContentType> generateSystemContentTypes()
    {
        return generateSystemContentTypes( CONTENT_TYPES );
    }

    private List<ContentType> generateSystemContentTypes( Iterable<ContentType> contentTypes )
    {
        final List<ContentType> generatedSystemContentTypes = Lists.newArrayList();
        for ( ContentType contentType : contentTypes )
        {
            contentType = ContentType.create( contentType ).
                icon( loadSchemaIcon( CONTENT_TYPES_FOLDER, contentType.getName().getLocalName() ) ).
                build();
            generatedSystemContentTypes.add( contentType );
        }
        return generatedSystemContentTypes;
    }

    public ContentTypes load()
    {
        final List<ContentType> generatedSystemContentTypes = generateSystemContentTypes();
        return ContentTypes.from( generatedSystemContentTypes );
    }

    public ContentTypes loadByApplication( final ApplicationKey applicationKey )
    {
        final List<ContentType> systemContentTypesByApplicationKey = CONTENT_TYPES.stream().
            filter( contentType -> contentType.getName().getApplicationKey().equals( applicationKey ) ).
            collect( Collectors.toList() );
        final List<ContentType> generatedSystemContentTypes = generateSystemContentTypes( systemContentTypesByApplicationKey );
        return ContentTypes.from( generatedSystemContentTypes );
    }

    private Icon loadSchemaIcon( final String metaInfFolderName, final String name )
    {
        final String metaInfFolderBasePath = "/" + "META-INF" + "/" + metaInfFolderName;
        final String filePath = metaInfFolderBasePath + "/" + name.toLowerCase() + ".png";
        try (final InputStream stream = this.getClass().getResourceAsStream( filePath ))
        {
            if ( stream == null )
            {
                return null;
            }
            return Icon.from( stream, "image/png", Instant.now() );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Failed to load icon file: " + filePath, e );
        }
    }
}
