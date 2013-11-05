package com.enonic.wem.core.schema.content;

import org.apache.commons.lang.WordUtils;

import com.enonic.wem.api.command.schema.content.CreateContentType;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.editor.ContentTypeEditor;
import com.enonic.wem.core.schema.content.serializer.ContentTypeJsonSerializer;
import com.enonic.wem.core.support.BaseInitializer;

import static com.enonic.wem.api.command.Commands.contentType;
import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.schema.content.editor.SetContentTypeEditor.newSetContentTypeEditor;


public class ContentTypesInitializer
    extends BaseInitializer
{
    public static Form MEDIA_IMAGE_FORM = createMediaImageForm();

    static final ContentType SPACE = createSystemType( ContentTypeName.space() ).
        setFinal( true ).setAbstract( false ).build();

    static final ContentType STRUCTURED = createSystemType( ContentTypeName.structured() ).
        setFinal( false ).setAbstract( true ).build();

    static final ContentType UNSTRUCTURED = createSystemType( ContentTypeName.unstructured() ).
        setFinal( false ).setAbstract( false ).build();

    static final ContentType FOLDER = createSystemType( ContentTypeName.folder() ).
        setFinal( false ).setAbstract( false ).build();

    static final ContentType PAGE = createSystemType( ContentTypeName.page() ).setFinal( true ).setAbstract( false ).build();

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
        {UNSTRUCTURED, STRUCTURED, SPACE, FOLDER, PAGE, SHORTCUT, MEDIA, MEDIA_TEXT, MEDIA_DATA, MEDIA_AUDIO, MEDIA_VIDEO, MEDIA_IMAGE,
            MEDIA_VECTOR, MEDIA_ARCHIVE, MEDIA_DOCUMENT, MEDIA_SPREADSHEET, MEDIA_PRESENTATION, MEDIA_CODE, MEDIA_EXECUTABLE};

    private static final String[] DEMO_CONTENT_TYPES =
        {"demo-contenttype-textarea.json", "demo-contenttype-htmlarea.json", "demo-contenttype-fieldset.json",
            "demo-contenttype-formItemset.json", "demo-contenttype-blog.json", "demo-contenttype-article1.json",
            "demo-contenttype-article2.json", "demo-contenttype-relation.json", "demo-contenttype-occurrences.json",
            "demo-contenttype-contentDisplayNameScript.json", "demo-contenttype-mixin-address.json",
            "demo-contenttype-mixin-norwegian-counties.json", "demo-contenttype-relation-article.json", "demo-contenttype-layout.json",
            "demo-contenttype-trampoline.json", "demo-contenttype-formItemset-min-occurrences.json",
            "demo-contenttype-singleSelectors.json", "demo-contenttype-comboBox.json", "demo-contenttype-all-input-types.json",
            "demo-contenttype-imageselector.json"};

    private final ContentTypeJsonSerializer contentTypeJsonSerializer = new ContentTypeJsonSerializer();

    protected ContentTypesInitializer()
    {
        super( 10, "content-types" );
    }

    @Override
    public void initialize()
        throws Exception
    {
        systemContentTypes();
        demoContentTypes();
    }

    private void systemContentTypes()
    {
        for ( ContentType contentType : SYSTEM_TYPES )
        {
            contentType = newContentType( contentType ).
                icon( loadIcon( contentType.getQualifiedName().toString() ) ).
                build();
            createOrUpdate( contentType );
        }
    }

    private void demoContentTypes()
    {
        for ( String testContentTypeFile : DEMO_CONTENT_TYPES )
        {
            final ContentType contentType = contentTypeJsonSerializer.toObject( loadFileAsString( testContentTypeFile ) );
            createOrUpdate( contentType );
        }
    }

    private void createOrUpdate( final ContentType contentType )
    {
        final ContentTypeNames qualifiedNames = ContentTypeNames.from( contentType.getQualifiedName() );
        final boolean contentTypeExists = !client.execute( contentType().get().qualifiedNames( qualifiedNames ) ).isEmpty();
        if ( !contentTypeExists )
        {
            final CreateContentType createCommand = contentType().create().
                name( contentType.getName() ).
                displayName( contentType.getDisplayName() ).
                superType( contentType.getSuperType() ).
                setAbstract( contentType.isAbstract() ).
                setFinal( contentType.isFinal() ).
                allowChildContent( contentType.allowChildContent() ).
                builtIn( contentType.isBuiltIn() ).
                form( contentType.form() ).
                icon( contentType.getIcon() ).
                contentDisplayNameScript( contentType.getContentDisplayNameScript() );
            client.execute( createCommand );
        }
        else
        {
            final ContentTypeEditor editor = newSetContentTypeEditor().
                displayName( contentType.getDisplayName() ).
                icon( contentType.getIcon() ).
                superType( contentType.getSuperType() ).
                setAbstract( contentType.isAbstract() ).
                setFinal( contentType.isFinal() ).
                contentDisplayNameScript( contentType.getContentDisplayNameScript() ).
                form( contentType.form() ).
                build();
            client.execute( contentType().update().qualifiedName( contentType.getQualifiedName() ).editor( editor ) );
        }
    }

    private static ContentType.Builder createSystemType( final ContentTypeName qualifiedName )
    {
        final String displayName = WordUtils.capitalize( qualifiedName.getContentTypeName() );
        final String contentTypeName = qualifiedName.getContentTypeName();
        return newContentType().
            name( contentTypeName ).
            displayName( displayName ).
            builtIn( true );
    }

    private static Form createMediaImageForm()
    {
        return Form.newForm().
            addFormItem( Input.newInput().name( "image" ).
                inputType( InputTypes.IMAGE ).label( "Media" ).build() ).
            addFormItem( Input.newInput().name( "mimeType" ).
                inputType( InputTypes.TEXT_LINE ).
                label( "Mime type" ).
                occurrences( 1, 1 ).
                build() ).

            build();
    }
}
