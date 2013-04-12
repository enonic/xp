package com.enonic.wem.core.content.schema.content;

import org.apache.commons.lang.WordUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.schema.content.CreateContentType;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.content.schema.content.editor.ContentTypeEditor;
import com.enonic.wem.api.content.schema.content.form.Form;
import com.enonic.wem.api.content.schema.content.form.Input;
import com.enonic.wem.api.content.schema.content.form.inputtype.InputTypes;
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

    static final ContentType MEDIA = createSystemType( QualifiedContentTypeName.media() ).
        setFinal( false ).setAbstract( false ).allowChildren( false ).build();

    static final ContentType MEDIA_TEXT =
        createSystemType( QualifiedContentTypeName.textMedia() ).superType( QualifiedContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildren( false ).build();

    static final ContentType MEDIA_DATA =
        createSystemType( QualifiedContentTypeName.dataMedia() ).superType( QualifiedContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildren( false ).build();

    static final ContentType MEDIA_AUDIO =
        createSystemType( QualifiedContentTypeName.audioMedia() ).superType( QualifiedContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildren( false ).build();

    static final ContentType MEDIA_VIDEO =
        createSystemType( QualifiedContentTypeName.videoMedia() ).superType( QualifiedContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildren( false ).build();

    static final ContentType MEDIA_IMAGE =
        createSystemType( QualifiedContentTypeName.imageMedia() ).superType( QualifiedContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildren( false ).form( createMediaImageForm() ).build();

    static final ContentType MEDIA_VECTOR =
        createSystemType( QualifiedContentTypeName.vectorMedia() ).superType( QualifiedContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildren( false ).build();

    static final ContentType MEDIA_ARCHIVE =
        createSystemType( QualifiedContentTypeName.archiveMedia() ).superType( QualifiedContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildren( false ).build();

    static final ContentType MEDIA_DOCUMENT =
        createSystemType( QualifiedContentTypeName.documentMedia() ).superType( QualifiedContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildren( false ).build();

    static final ContentType MEDIA_SPREADSHEET =
        createSystemType( QualifiedContentTypeName.spreadsheetMedia() ).superType( QualifiedContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildren( false ).build();

    static final ContentType MEDIA_PRESENTATION =
        createSystemType( QualifiedContentTypeName.presentationMedia() ).superType( QualifiedContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildren( false ).build();

    static final ContentType MEDIA_CODE =
        createSystemType( QualifiedContentTypeName.codeMedia() ).superType( QualifiedContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildren( false ).build();

    static final ContentType MEDIA_EXECUTABLE =
        createSystemType( QualifiedContentTypeName.executableMedia() ).superType( QualifiedContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildren( false ).build();

    private static final ContentType[] SYSTEM_TYPES =
        {SPACE, STRUCTURED, UNSTRUCTURED, FOLDER, PAGE, SHORTCUT, MEDIA, MEDIA_TEXT, MEDIA_DATA, MEDIA_AUDIO, MEDIA_VIDEO, MEDIA_IMAGE,
            MEDIA_VECTOR, MEDIA_ARCHIVE, MEDIA_DOCUMENT, MEDIA_SPREADSHEET, MEDIA_PRESENTATION, MEDIA_CODE, MEDIA_EXECUTABLE};

    private static final String[] DEMO_CONTENT_TYPES =
        {"demo-contenttype-htmlarea.json", "demo-contenttype-fieldset.json", "demo-contenttype-set.json", "demo-contenttype-blog.json",
            "demo-contenttype-article1.json", "demo-contenttype-article2.json", "demo-contenttype-relation.json",
            "demo-contenttype-occurrences.json", "demo-contenttype-contentDisplayNameScript.json", "demo-contenttype-mixin.json",
            "demo-contenttype-image.json", "demo-contenttype-relation-article.json", "demo-contenttype-layout.json"};

    private final ContentTypeJsonSerializer contentTypeJsonSerializer = new ContentTypeJsonSerializer();

    protected ContentTypesInitializer()
    {
        super( "content-types" );
    }

    private static Form createMediaImageForm()
    {
        return Form.newForm().
            addFormItem( Input.newInput().name( "binary" ).inputType( InputTypes.IMAGE_UPLOAD ).label( "Binary" ).build() ).
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
                allowChildren( contentType.allowChildren() ).
                moduleName( contentType.getModuleName() ).
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
        final ContentType.Builder builder = newContentType();
        builder.module( Module.SYSTEM.getName() );
        builder.name( contentTypeName );
        builder.displayName( displayName );
        return builder;
    }
}
