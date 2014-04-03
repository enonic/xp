package com.enonic.wem.core.schema.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.schema.content.ContentTypeService;
import com.enonic.wem.api.command.schema.content.CreateContentTypeParams;
import com.enonic.wem.api.command.schema.content.GetContentTypesParams;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.validator.InvalidContentTypeException;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static org.junit.Assert.*;

public class CreateContentTypeCommandTest
    extends AbstractCommandHandlerTest
{
    private CreateContentTypeCommand command;

    private ContentTypeDao contentTypeDao;

    private ContentTypeService contentTypeService;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        this.contentTypeDao = Mockito.mock( ContentTypeDao.class );
        this.contentTypeService = Mockito.mock( ContentTypeService.class );

        command = new CreateContentTypeCommand().contentTypeDao( this.contentTypeDao ).contentTypeService( this.contentTypeService );
    }

    @Test
    public void createContentType()
        throws Exception
    {
        // setup
        Mockito.when( contentTypeService.getByNames( Mockito.isA( GetContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( ContentTypesInitializer.STRUCTURED ) );

        final ContentType contentType = newContentType().
            name( "my_content_type" ).
            displayName( "My content type" ).
            description( "description" ).
            setAbstract( false ).
            superType( ContentTypeName.structured() ).
            build();

        final CreateContentTypeParams params = new CreateContentTypeParams().
            name( contentType.getName() ).
            displayName( contentType.getDisplayName() ).
            description( contentType.getDescription() ).
            setAbstract( contentType.isAbstract() ).
            setFinal( contentType.isFinal() ).
            form( contentType.form() ).
            schemaIcon( contentType.getIcon() ).
            contentDisplayNameScript( contentType.getContentDisplayNameScript() );

        Mockito.when( contentTypeDao.createContentType( Mockito.isA( ContentType.class ) ) ).thenReturn( contentType );

        // exercise
        final ContentType createdContentType = this.command.params( params ).execute();

        // verify
        assertNotNull( createdContentType );
        assertEquals( "my_content_type", createdContentType.getName().toString() );
    }

    @Test(expected = InvalidContentTypeException.class)
    public void given_superType_that_is_final_when_handle_then_InvalidContentTypeException()
        throws Exception
    {
        //setup
        Mockito.when( contentTypeService.getByNames( Mockito.isA( GetContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( ContentTypesInitializer.SHORTCUT ) );

        final ContentType contentType = newContentType().
            name( "my_content_type" ).
            displayName( "Inheriting a final ContentType" ).
            displayName( "A description" ).
            setAbstract( false ).
            superType( ContentTypeName.shortcut() ).
            build();

        // exercise
        final CreateContentTypeParams params = new CreateContentTypeParams().
            name( contentType.getName() ).
            displayName( contentType.getDisplayName() ).
            description( contentType.getDescription() ).
            superType( contentType.getSuperType() ).
            setAbstract( contentType.isAbstract() ).
            setFinal( contentType.isFinal() ).
            form( contentType.form() ).
            schemaIcon( contentType.getIcon() ).
            contentDisplayNameScript( contentType.getContentDisplayNameScript() );

        this.command.params( params ).execute();
    }

}
