package com.enonic.wem.core.content.schema.content;

import org.apache.commons.lang.WordUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.content.schema.content.editor.ContentTypeEditor;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.core.content.schema.content.serializer.ContentTypeJsonSerializer;
import com.enonic.wem.core.initializer.InitializerTask;
import com.enonic.wem.core.support.BaseInitializer;

import static com.enonic.wem.api.command.Commands.contentType;
import static com.enonic.wem.api.content.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.content.schema.content.editor.SetContentTypeEditor.newSetContentTypeEditor;

@Component
@Order(10)
public class ContentTypesInitializer
    extends BaseInitializer
    implements InitializerTask
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

    static final ContentType FILE = createSystemType( QualifiedContentTypeName.file() ).
        setFinal( false ).setAbstract( false ).build();

    static final ContentType FILE_TEXT =
        createSystemType( QualifiedContentTypeName.textFile() ).superType( QualifiedContentTypeName.file() ).
            setFinal( true ).setAbstract( false ).build();

    static final ContentType FILE_DATA =
        createSystemType( QualifiedContentTypeName.dataFile() ).superType( QualifiedContentTypeName.file() ).
            setFinal( true ).setAbstract( false ).build();

    static final ContentType FILE_AUDIO =
        createSystemType( QualifiedContentTypeName.audioFile() ).superType( QualifiedContentTypeName.file() ).
            setFinal( true ).setAbstract( false ).build();

    static final ContentType FILE_VIDEO =
        createSystemType( QualifiedContentTypeName.videoFile() ).superType( QualifiedContentTypeName.file() ).
            setFinal( true ).setAbstract( false ).build();

    static final ContentType FILE_IMAGE =
        createSystemType( QualifiedContentTypeName.imageFile() ).superType( QualifiedContentTypeName.file() ).
            setFinal( true ).setAbstract( false ).build();

    static final ContentType FILE_VECTOR =
        createSystemType( QualifiedContentTypeName.vectorFile() ).superType( QualifiedContentTypeName.file() ).
            setFinal( true ).setAbstract( false ).build();

    static final ContentType FILE_ARCHIVE =
        createSystemType( QualifiedContentTypeName.archiveFile() ).superType( QualifiedContentTypeName.file() ).
            setFinal( true ).setAbstract( false ).build();

    static final ContentType FILE_DOCUMENT =
        createSystemType( QualifiedContentTypeName.documentFile() ).superType( QualifiedContentTypeName.file() ).
            setFinal( true ).setAbstract( false ).build();

    static final ContentType FILE_SPREADSHEET =
        createSystemType( QualifiedContentTypeName.spreadsheetFile() ).superType( QualifiedContentTypeName.file() ).
            setFinal( true ).setAbstract( false ).build();

    static final ContentType FILE_PRESENTATION =
        createSystemType( QualifiedContentTypeName.presentationFile() ).superType( QualifiedContentTypeName.file() ).
            setFinal( true ).setAbstract( false ).build();

    static final ContentType FILE_CODE =
        createSystemType( QualifiedContentTypeName.codeFile() ).superType( QualifiedContentTypeName.file() ).
            setFinal( true ).setAbstract( false ).build();

    static final ContentType FILE_EXECUTABLE =
        createSystemType( QualifiedContentTypeName.executableFile() ).superType( QualifiedContentTypeName.file() ).
            setFinal( true ).setAbstract( false ).build();

    private static final ContentType[] SYSTEM_TYPES =
        {SPACE, STRUCTURED, UNSTRUCTURED, FOLDER, PAGE, SHORTCUT, FILE, FILE_TEXT, FILE_DATA, FILE_AUDIO, FILE_VIDEO, FILE_IMAGE,
            FILE_VECTOR, FILE_ARCHIVE, FILE_DOCUMENT, FILE_SPREADSHEET, FILE_PRESENTATION, FILE_CODE, FILE_EXECUTABLE};

    private static final String[] DEMO_CONTENT_TYPES =
        {"demo-contenttype-htmlarea.json", "demo-contenttype-fieldset.json", "demo-contenttype-set.json", "demo-contenttype-blog.json",
            "demo-contenttype-article1.json", "demo-contenttype-article2.json", "demo-contenttype-relation.json",
            "demo-contenttype-occurrences.json", "demo-contenttype-contentDisplayNameScript.json", "demo-contenttype-mixin.json"};

    private final ContentTypeJsonSerializer contentTypeJsonSerializer = new ContentTypeJsonSerializer();

    protected ContentTypesInitializer()
    {
        super( "content-types" );
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
            client.execute( contentType().create().contentType( contentType ) );
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
        final ContentType.Builder builder = newContentType();
        builder.module( Module.SYSTEM.getName() );
        builder.name( contentTypeName );
        builder.displayName( displayName );
        return builder;
    }
}
