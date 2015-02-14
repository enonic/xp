package com.enonic.xp.core.impl.schema.content;

import java.io.InputStream;
import java.time.Instant;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.osgi.service.component.annotations.Component;

import com.google.common.collect.Lists;

import com.enonic.xp.icon.Icon;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeForms;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeProvider;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.mixin.MixinNames;
import com.enonic.xp.core.impl.schema.mixin.BuiltinMixinProvider;

import static com.enonic.xp.schema.content.ContentType.newContentType;

@Component(immediate = true)
public final class BuiltinContentTypeProvider
    implements ContentTypeProvider
{
    private static final String CONTENT_TYPES_FOLDER = "content-types";

    // System Content Types
    private static final ContentType STRUCTURED = createSystemType( ContentTypeName.structured() ).
        setFinal( false ).setAbstract( true ).build();

    private static final ContentType UNSTRUCTURED = createSystemType( ContentTypeName.unstructured() ).
        setFinal( false ).setAbstract( false ).build();

    public static final ContentType FOLDER = createSystemType( ContentTypeName.folder() ).
        setFinal( false ).setAbstract( false ).build();

    private static final ContentType SITE = createSystemType( ContentTypeName.site() ).
        description( "Root content for sites" ).
        setFinal( true ).
        setAbstract( false ).
        form( ContentTypeForms.SITE ).
        superType( ContentTypeName.structured() ).
        build();

    private static final ContentType TEMPLATE_FOLDER = createSystemType( ContentTypeName.templateFolder() ).
        setFinal( true ).
        setAbstract( false ).
        superType( ContentTypeName.folder() ).
        build();

    private static final ContentType PAGE_TEMPLATE = createSystemType( ContentTypeName.pageTemplate() ).
        allowChildContent( false ).
        setFinal( false ).
        setAbstract( false ).
        form( ContentTypeForms.PAGE_TEMPLATE ).
        superType( ContentTypeName.structured() ).
        build();

    private static final ContentType SHORTCUT = createSystemType( ContentTypeName.shortcut() ).
        setFinal( true ).setAbstract( false ).build();

    private static final ContentType MEDIA = createSystemType( ContentTypeName.media() ).
        setFinal( false ).setAbstract( false ).allowChildContent( false ).build();

    private static final ContentType MEDIA_TEXT = createSystemType( ContentTypeName.textMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).form( ContentTypeForms.MEDIA_DEFAULT ).build();

    private static final ContentType MEDIA_DATA = createSystemType( ContentTypeName.dataMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).form( ContentTypeForms.MEDIA_DEFAULT ).build();

    private static final ContentType MEDIA_AUDIO = createSystemType( ContentTypeName.audioMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).form( ContentTypeForms.MEDIA_DEFAULT ).build();

    private static final ContentType MEDIA_VIDEO = createSystemType( ContentTypeName.videoMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).form( ContentTypeForms.MEDIA_DEFAULT ).build();

    private static final ContentType MEDIA_IMAGE = createSystemType( ContentTypeName.imageMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).form( ContentTypeForms.MEDIA_IMAGE ).
        metadata( MixinNames.from( BuiltinMixinProvider.IMAGE_INFO_METADATA_NAME, BuiltinMixinProvider.PHOTO_INFO_METADATA_NAME, BuiltinMixinProvider.GPS_INFO_METADATA_NAME
        ) ).build();

    private static final ContentType MEDIA_VECTOR = createSystemType( ContentTypeName.vectorMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).form( ContentTypeForms.MEDIA_DEFAULT ).build();

    private static final ContentType MEDIA_ARCHIVE =
        createSystemType( ContentTypeName.archiveMedia() ).superType( ContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).form( ContentTypeForms.MEDIA_DEFAULT ).build();

    private static final ContentType MEDIA_DOCUMENT =
        createSystemType( ContentTypeName.documentMedia() ).superType( ContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).form( ContentTypeForms.MEDIA_DEFAULT ).build();

    private static final ContentType MEDIA_SPREADSHEET =
        createSystemType( ContentTypeName.spreadsheetMedia() ).superType( ContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).form( ContentTypeForms.MEDIA_DEFAULT ).build();

    private static final ContentType MEDIA_PRESENTATION =
        createSystemType( ContentTypeName.presentationMedia() ).superType( ContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).form( ContentTypeForms.MEDIA_DEFAULT ).build();

    private static final ContentType MEDIA_CODE = createSystemType( ContentTypeName.codeMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).form( ContentTypeForms.MEDIA_DEFAULT ).build();

    private static final ContentType MEDIA_EXECUTABLE =
        createSystemType( ContentTypeName.executableMedia() ).superType( ContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).form( ContentTypeForms.MEDIA_DEFAULT ).build();

    private static final ContentType MEDIA_UNKNOWN =
        createSystemType( ContentTypeName.unknownMedia() ).superType( ContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).form( ContentTypeForms.MEDIA_DEFAULT ).build();

    private static final ContentType[] SYSTEM_TYPES =
        {UNSTRUCTURED, STRUCTURED, FOLDER, SHORTCUT, MEDIA, MEDIA_TEXT, MEDIA_DATA, MEDIA_AUDIO, MEDIA_VIDEO, MEDIA_IMAGE, MEDIA_VECTOR,
            MEDIA_ARCHIVE, MEDIA_DOCUMENT, MEDIA_SPREADSHEET, MEDIA_PRESENTATION, MEDIA_CODE, MEDIA_EXECUTABLE, MEDIA_UNKNOWN, SITE,
            TEMPLATE_FOLDER, PAGE_TEMPLATE};

    private final ContentTypes types;

    public BuiltinContentTypeProvider()
    {
        this.types = ContentTypes.from( generateSystemContentTypes() );
    }

    private static ContentType.Builder createSystemType( final ContentTypeName contentTypeName )
    {
        final String displayName = WordUtils.capitalize( contentTypeName.getLocalName() );
        return newContentType().
            name( contentTypeName ).
            displayName( displayName ).
            setBuiltIn();
    }

    private List<ContentType> generateSystemContentTypes()
    {
        final List<ContentType> systemContentTypes = Lists.newArrayList();
        for ( ContentType contentType : SYSTEM_TYPES )
        {
            contentType = newContentType( contentType ).
                icon( loadSchemaIcon( CONTENT_TYPES_FOLDER, contentType.getName().getLocalName() ) ).
                build();
            systemContentTypes.add( contentType );
        }
        return systemContentTypes;
    }

    @Override
    public ContentTypes get()
    {
        return this.types;
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
