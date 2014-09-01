package com.enonic.wem.core.schema.content;

import java.util.List;

import org.apache.commons.lang.WordUtils;

import com.google.common.collect.Lists;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.SchemaProvider;
import com.enonic.wem.api.schema.Schemas;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.core.schema.content.serializer.ContentTypeJsonSerializer;
import com.enonic.wem.core.support.BaseCoreSchemaProvider;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;

public final class CoreContentTypesProvider
    extends BaseCoreSchemaProvider
    implements SchemaProvider
{

    public static Form MEDIA_IMAGE_FORM = createMediaImageForm();

    static final ContentType STRUCTURED = createSystemType( ContentTypeName.structured() ).
        setFinal( false ).setAbstract( true ).build();

    static final ContentType UNSTRUCTURED = createSystemType( ContentTypeName.unstructured() ).
        setFinal( false ).setAbstract( false ).build();

    static final ContentType FOLDER = createSystemType( ContentTypeName.folder() ).
        setFinal( false ).setAbstract( false ).build();

    static final ContentType PAGE = createSystemType( ContentTypeName.page() ).setFinal( true ).setAbstract( false ).build();

    static final ContentType SITE =
        createSystemType( ContentTypeName.site() ).description( "Root content for sites" ).setFinal( true ).setAbstract( false ).build();

    static final ContentType SHORTCUT = createSystemType( ContentTypeName.shortcut() ).
        setFinal( true ).setAbstract( false ).build();

    static final ContentType MEDIA = createSystemType( ContentTypeName.media() ).
        setFinal( false ).setAbstract( false ).allowChildContent( false ).build();

    static final ContentType MEDIA_TEXT = createSystemType( ContentTypeName.textMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    static final ContentType MEDIA_DATA = createSystemType( ContentTypeName.dataMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    static final ContentType MEDIA_AUDIO = createSystemType( ContentTypeName.audioMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    static final ContentType MEDIA_VIDEO = createSystemType( ContentTypeName.videoMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    static final ContentType MEDIA_IMAGE = createSystemType( ContentTypeName.imageMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).form( MEDIA_IMAGE_FORM ).build();

    static final ContentType MEDIA_VECTOR = createSystemType( ContentTypeName.vectorMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    static final ContentType MEDIA_ARCHIVE = createSystemType( ContentTypeName.archiveMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    static final ContentType MEDIA_DOCUMENT = createSystemType( ContentTypeName.documentMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    static final ContentType MEDIA_SPREADSHEET =
        createSystemType( ContentTypeName.spreadsheetMedia() ).superType( ContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    static final ContentType MEDIA_PRESENTATION =
        createSystemType( ContentTypeName.presentationMedia() ).superType( ContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    static final ContentType MEDIA_CODE = createSystemType( ContentTypeName.codeMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    static final ContentType MEDIA_EXECUTABLE = createSystemType( ContentTypeName.executableMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    private static final ContentType[] SYSTEM_TYPES =
        {UNSTRUCTURED, STRUCTURED, FOLDER, PAGE, SHORTCUT, MEDIA, MEDIA_TEXT, MEDIA_DATA, MEDIA_AUDIO, MEDIA_VIDEO, MEDIA_IMAGE,
            MEDIA_VECTOR, MEDIA_ARCHIVE, MEDIA_DOCUMENT, MEDIA_SPREADSHEET, MEDIA_PRESENTATION, MEDIA_CODE, MEDIA_EXECUTABLE, SITE};

    private static final String[] DEMO_CONTENT_TYPES =
        {"demo-contenttype-textline.json", "demo-contenttype-textarea.json", "demo-contenttype-htmlarea.json",
            "demo-contenttype-fieldset.json", "demo-contenttype-formItemset.json", "demo-contenttype-blog.json",
            "demo-contenttype-article1.json", "demo-contenttype-article2.json", "demo-contenttype-relation.json",
            "demo-contenttype-occurrences.json", "demo-contenttype-contentDisplayNameScript.json", "demo-contenttype-mixin-address.json",
            "demo-contenttype-mixin-norwegian-counties.json", "demo-contenttype-relation-article.json", "demo-contenttype-layout.json",
            "demo-contenttype-formItemset-min-occurrences.json", "demo-contenttype-singleSelectors.json", "demo-contenttype-comboBox.json",
            "demo-contenttype-all-input-types.json", "demo-contenttype-imageselector.json",
            "demo-contenttype-several-levels-of-formItemset.json", "demo-contenttype-tag.json", "demo-contenttype-checkbox.json",
            "demo-contenttype-long.json", "demo-geo-location.json"};

    private final ContentTypeJsonSerializer contentTypeJsonSerializer = new ContentTypeJsonSerializer();

    public CoreContentTypesProvider()
    {
        super( "content-types" );
    }

    private static ContentType.Builder createSystemType( final ContentTypeName contentTypeName )
    {
        final String displayName = WordUtils.capitalize( contentTypeName.getContentTypeName() );
        return newContentType().
            name( contentTypeName ).
            displayName( displayName ).
            setBuiltIn();
    }

    private static Form createMediaImageForm()
    {
        return Form.newForm().
            addFormItem( Input.newInput().name( "image" ).
                inputType( InputTypes.IMAGE ).build() ).
            addFormItem( Input.newInput().name( "mimeType" ).
                inputType( InputTypes.TEXT_LINE ).
                label( "Mime type" ).
                occurrences( 1, 1 ).
                build() ).

            build();
    }

    private List<ContentType> generateSystemContentTypes()
    {
        final List<ContentType> systemContentTypes = Lists.newArrayList();
        for ( ContentType contentType : SYSTEM_TYPES )
        {
            contentType = newContentType( contentType ).
                icon( loadSchemaIcon( contentType.getName().toString() ) ).
                build();
            systemContentTypes.add( contentType );
        }
        return systemContentTypes;
    }

    private List<ContentType> generateDemoContentTypes()
    {
        final List<ContentType> demoContentTypes = Lists.newArrayList();
        for ( String testContentTypeFile : DEMO_CONTENT_TYPES )
        {
            final ContentType contentType = contentTypeJsonSerializer.toObject( loadFileAsString( testContentTypeFile ) );
            demoContentTypes.add( contentType );
        }
        return demoContentTypes;
    }


    @Override
    public Schemas getSchemas()
    {
        final List<Schema> schemas = Lists.newArrayList();
        final List<ContentType> systemContentTypes = generateSystemContentTypes();
        final List<ContentType> demoContentTypes = generateDemoContentTypes();
        schemas.addAll( systemContentTypes );
        schemas.addAll( demoContentTypes );
        return Schemas.from( schemas );
    }

}
