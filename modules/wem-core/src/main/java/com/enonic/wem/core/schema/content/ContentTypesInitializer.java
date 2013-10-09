package com.enonic.wem.core.schema.content;

import org.apache.commons.lang.WordUtils;

import com.enonic.wem.api.command.schema.content.CreateContentType;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.schema.content.editor.ContentTypeEditor;
import com.enonic.wem.api.schema.content.form.Form;
import com.enonic.wem.api.schema.content.form.Input;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.core.schema.content.serializer.ContentTypeJsonSerializer;
import com.enonic.wem.core.support.BaseInitializer;

import static com.enonic.wem.api.command.Commands.contentType;
import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.schema.content.editor.SetContentTypeEditor.newSetContentTypeEditor;


public class ContentTypesInitializer
    extends BaseInitializer
{
    static final ContentType SPACE = createSystemType( QualifiedContentTypeName.space() ).
        setFinal( true ).setAbstract( false ).build();

    static final ContentType STRUCTURED = createSystemType( QualifiedContentTypeName.structured() ).
        setFinal( false ).setAbstract( true ).build();

    static final ContentType UNSTRUCTURED = createSystemType( QualifiedContentTypeName.unstructured() ).
        setFinal( false ).setAbstract( false ).build();

    static final ContentType FOLDER = createSystemType( QualifiedContentTypeName.folder() ).
        setFinal( false ).setAbstract( false ).build();

    static final ContentType PAGE = createSystemType( QualifiedContentTypeName.page() ).setFinal( true ).setAbstract( false ).build();

    static final ContentType SHORTCUT = createSystemType( QualifiedContentTypeName.shortcut() ).
        setFinal( true ).setAbstract( false ).build();

    static final ContentType MEDIA = createSystemType( QualifiedContentTypeName.media() ).
        setFinal( false ).setAbstract( false ).allowChildContent( false ).build();

    static final ContentType MEDIA_TEXT =
        createSystemType( QualifiedContentTypeName.textMedia() ).superType( QualifiedContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    static final ContentType MEDIA_DATA =
        createSystemType( QualifiedContentTypeName.dataMedia() ).superType( QualifiedContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    static final ContentType MEDIA_AUDIO =
        createSystemType( QualifiedContentTypeName.audioMedia() ).superType( QualifiedContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    static final ContentType MEDIA_VIDEO =
        createSystemType( QualifiedContentTypeName.videoMedia() ).superType( QualifiedContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    static final ContentType MEDIA_IMAGE =
        createSystemType( QualifiedContentTypeName.imageMedia() ).superType( QualifiedContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).form( createMediaImageForm() ).build();

    static final ContentType MEDIA_VECTOR =
        createSystemType( QualifiedContentTypeName.vectorMedia() ).superType( QualifiedContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    static final ContentType MEDIA_ARCHIVE =
        createSystemType( QualifiedContentTypeName.archiveMedia() ).superType( QualifiedContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    static final ContentType MEDIA_DOCUMENT =
        createSystemType( QualifiedContentTypeName.documentMedia() ).superType( QualifiedContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    static final ContentType MEDIA_SPREADSHEET =
        createSystemType( QualifiedContentTypeName.spreadsheetMedia() ).superType( QualifiedContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    static final ContentType MEDIA_PRESENTATION =
        createSystemType( QualifiedContentTypeName.presentationMedia() ).superType( QualifiedContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    static final ContentType MEDIA_CODE =
        createSystemType( QualifiedContentTypeName.codeMedia() ).superType( QualifiedContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    static final ContentType MEDIA_EXECUTABLE =
        createSystemType( QualifiedContentTypeName.executableMedia() ).superType( QualifiedContentTypeName.media() ).
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
            "demo-contenttype-singleSelectors.json", "demo-contenttype-comboBox.json"};

    private final ContentTypeJsonSerializer contentTypeJsonSerializer = new ContentTypeJsonSerializer();

    protected ContentTypesInitializer()
    {
        super( 10, "content-types" );
    }

    private static Form createMediaImageForm()
    {
        return Form.newForm().
            addFormItem( Input.newInput().name( "image" ).inputType( InputTypes.IMAGE ).label( "Media" ).build() ).
            addFormItem( Input.newInput().name( "mimeType" ).inputType( InputTypes.TEXT_LINE ).label( "Mime type" ).build() ).
            build();
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
                icon( loadIcon( contentType.getQualifiedName() ) ).
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
        final QualifiedContentTypeNames qualifiedNames = QualifiedContentTypeNames.from( contentType.getQualifiedName() );
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

    private static ContentType.Builder createSystemType( final QualifiedContentTypeName qualifiedName )
    {
        final String displayName = WordUtils.capitalize( qualifiedName.getContentTypeName() );
        final String contentTypeName = qualifiedName.getContentTypeName();
        return newContentType().
            name( contentTypeName ).
            displayName( displayName ).
            builtIn( true );
    }
}
