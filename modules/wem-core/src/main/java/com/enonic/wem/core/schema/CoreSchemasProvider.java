package com.enonic.wem.core.schema;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.time.Instant;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.WordUtils;

import com.google.common.collect.Lists;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.SchemaProvider;
import com.enonic.wem.api.schema.Schemas;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.core.schema.content.serializer.ContentTypeJsonSerializer;
import com.enonic.wem.core.schema.mixin.MixinJsonSerializer;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.schema.relationship.RelationshipType.newRelationshipType;

public final class CoreSchemasProvider
    implements SchemaProvider
{
    private static final String CONTENT_TYPES_FOLDER = "content-types";

    private static final String MIXINS_FOLDER = "mixins";

    private static final String RELATIONSHIP_TYPES_FOLDER = "relationship-types";


    // System Content Types
    public static Form MEDIA_IMAGE_FORM = createMediaImageForm();

    private static final ContentType STRUCTURED = createSystemType( ContentTypeName.structured() ).
        setFinal( false ).setAbstract( true ).build();

    private static final ContentType UNSTRUCTURED = createSystemType( ContentTypeName.unstructured() ).
        setFinal( false ).setAbstract( false ).build();

    private static final ContentType FOLDER = createSystemType( ContentTypeName.folder() ).
        setFinal( false ).setAbstract( false ).build();

    private static final ContentType PAGE = createSystemType( ContentTypeName.page() ).setFinal( true ).setAbstract( false ).build();

    private static final ContentType SITE =
        createSystemType( ContentTypeName.site() ).description( "Root content for sites" ).setFinal( true ).setAbstract( false ).build();

    private static final ContentType SHORTCUT = createSystemType( ContentTypeName.shortcut() ).
        setFinal( true ).setAbstract( false ).build();

    private static final ContentType MEDIA = createSystemType( ContentTypeName.media() ).
        setFinal( false ).setAbstract( false ).allowChildContent( false ).build();

    private static final ContentType MEDIA_TEXT = createSystemType( ContentTypeName.textMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    private static final ContentType MEDIA_DATA = createSystemType( ContentTypeName.dataMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    private static final ContentType MEDIA_AUDIO = createSystemType( ContentTypeName.audioMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    private static final ContentType MEDIA_VIDEO = createSystemType( ContentTypeName.videoMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    private static final ContentType MEDIA_IMAGE = createSystemType( ContentTypeName.imageMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).form( MEDIA_IMAGE_FORM ).build();

    private static final ContentType MEDIA_VECTOR = createSystemType( ContentTypeName.vectorMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    private static final ContentType MEDIA_ARCHIVE =
        createSystemType( ContentTypeName.archiveMedia() ).superType( ContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    private static final ContentType MEDIA_DOCUMENT =
        createSystemType( ContentTypeName.documentMedia() ).superType( ContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    private static final ContentType MEDIA_SPREADSHEET =
        createSystemType( ContentTypeName.spreadsheetMedia() ).superType( ContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    private static final ContentType MEDIA_PRESENTATION =
        createSystemType( ContentTypeName.presentationMedia() ).superType( ContentTypeName.media() ).
            setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    private static final ContentType MEDIA_CODE = createSystemType( ContentTypeName.codeMedia() ).superType( ContentTypeName.media() ).
        setFinal( true ).setAbstract( false ).allowChildContent( false ).build();

    private static final ContentType MEDIA_EXECUTABLE =
        createSystemType( ContentTypeName.executableMedia() ).superType( ContentTypeName.media() ).
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

    // System Relationship Types
    private static final RelationshipType DEFAULT =
        createRelationshipType( RelationshipTypeName.DEFAULT, "Default", "relates to", "related of" );

    private static final RelationshipType PARENT = createRelationshipType( RelationshipTypeName.PARENT, "Parent", "parent of", "child of" );

    private static final RelationshipType LINK = createRelationshipType( RelationshipTypeName.LINK, "Link", "links to", "linked by" );

    private static final RelationshipType LIKE = createRelationshipType( RelationshipTypeName.LIKE, "Like", "likes", "liked by" );

    private static final RelationshipType CITATION =
        createRelationshipType( RelationshipTypeName.from( "mymodule-1.0.0:citation" ), "Citation", "citation in", "cited by",
                                ContentTypeNames.from( "mymodule-1.0.0:article" ) );

    private static final RelationshipType IMAGE =
        createRelationshipType( RelationshipTypeName.from( "mymodule-1.0.0:image" ), "Image", "relates to image", "related of image",
                                ContentTypeNames.from( "mymodule-1.0.0:image" ) );

    private static final RelationshipType[] RELATIONSHIP_TYPES = {DEFAULT, PARENT, LINK, LIKE, CITATION, IMAGE};

    // Demo Mixins
    private static final String[] DEMO_MIXINS = {"demo-mixin-address.json", "demo-mixin-norwegian-counties.json"};

    private final ContentTypeJsonSerializer contentTypeJsonSerializer = new ContentTypeJsonSerializer();

    private final MixinJsonSerializer mixinJsonSerializer = new MixinJsonSerializer();


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

    private static RelationshipType createRelationshipType( final RelationshipTypeName relationshipTypeName, final String displayName,
                                                            final String fromSemantic, final String toSemantic )
    {
        return createRelationshipType( relationshipTypeName, displayName, fromSemantic, toSemantic, ContentTypeNames.empty() );
    }

    private static RelationshipType createRelationshipType( final RelationshipTypeName relationshipTypeName, final String displayName,
                                                            final String fromSemantic, final String toSemantic,
                                                            final ContentTypeNames toContentTypes )
    {
        return newRelationshipType().
            name( relationshipTypeName ).
            displayName( displayName ).
            fromSemantic( fromSemantic ).
            toSemantic( toSemantic ).
            addAllowedToTypes( toContentTypes ).
            build();
    }

    private List<ContentType> generateSystemContentTypes()
    {
        final List<ContentType> systemContentTypes = Lists.newArrayList();
        for ( ContentType contentType : SYSTEM_TYPES )
        {
            contentType = newContentType( contentType ).
                icon( loadSchemaIcon( CONTENT_TYPES_FOLDER, contentType.getName().toString() ) ).
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
            final ContentType contentType =
                contentTypeJsonSerializer.toObject( loadFileAsString( CONTENT_TYPES_FOLDER, testContentTypeFile ) );
            demoContentTypes.add( contentType );
        }
        return demoContentTypes;
    }

    private List<Mixin> generateDemoMixins()
    {
        final List<Mixin> mixins = Lists.newArrayList();
        for ( String demoMixinFileName : DEMO_MIXINS )
        {
            final String mixinJson = loadFileAsString( MIXINS_FOLDER, demoMixinFileName );
            Mixin mixin = mixinJsonSerializer.toMixin( mixinJson );
            mixin = Mixin.newMixin( mixin ).
                icon( loadSchemaIcon( MIXINS_FOLDER, mixin.getName().toString() ) ).
                build();

            mixins.add( mixin );
        }
        return mixins;
    }

    private List<RelationshipType> generateSystemRelationshipTypes()
    {
        final List<RelationshipType> relationshipTypes = Lists.newArrayList();
        for ( RelationshipType relationshipType : RELATIONSHIP_TYPES )
        {
            relationshipType = RelationshipType.newRelationshipType( relationshipType ).
                icon( loadSchemaIcon( RELATIONSHIP_TYPES_FOLDER, relationshipType.getName().toString() ) ).
                build();
            relationshipTypes.add( relationshipType );
        }
        return relationshipTypes;
    }

    @Override
    public Schemas getSchemas()
    {
        final List<Schema> schemas = Lists.newArrayList();
        schemas.addAll( generateSystemContentTypes() );
        schemas.addAll( generateDemoContentTypes() );
        schemas.addAll( generateDemoMixins() );
        schemas.addAll( generateSystemRelationshipTypes() );
        return Schemas.from( schemas );
    }

    private String loadFileAsString( final String metaInfFolderName, final String name )
    {
        final String metaInfFolderBasePath = "/" + "META-INF" + "/" + metaInfFolderName;
        final String filePath = metaInfFolderBasePath + "/" + name;
        final StringWriter writer = new StringWriter();
        try
        {
            IOUtils.copy( getClass().getResourceAsStream( filePath ), writer );
            return writer.toString();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to load file: " + filePath, e );
        }
    }

    private Icon loadSchemaIcon( final String metaInfFolderName, final String name )
    {
        final String metaInfFolderBasePath = "/" + "META-INF" + "/" + metaInfFolderName;
        final String filePath = metaInfFolderBasePath + "/" + name.toLowerCase() + ".png";
        try
        {
            final InputStream stream = this.getClass().getResourceAsStream( filePath );
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
