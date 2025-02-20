package com.enonic.xp.core.impl.content;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.ValidationError;
import com.enonic.xp.content.ValidationErrorCode;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.core.impl.content.serializer.ContentDataSerializer;
import com.enonic.xp.core.impl.content.validate.ContentNameValidator;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.ModifyNodeResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.RenameNodeParams;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RenameContentCommandTest
{
    Node mockNode;

    ContentTypeService contentTypeService;

    ContentService contentService;

    EventPublisher eventPublisher;

    ContentNodeTranslator translator;

    NodeService nodeService;

    PageDescriptorService pageDescriptorService;

    PartDescriptorService partDescriptorService;

    LayoutDescriptorService layoutDescriptorService;

    XDataService xDataService;

    ContentDataSerializer contentDataSerializer;

    @BeforeEach
    void setUp()
    {
        this.contentTypeService = mock( ContentTypeService.class );
        this.contentService = mock( ContentService.class );
        this.nodeService = mock( NodeService.class );
        this.eventPublisher = mock( EventPublisher.class );
        this.translator = mock( ContentNodeTranslator.class );
        this.xDataService = mock( XDataService.class );
        this.pageDescriptorService = mock( PageDescriptorService.class );
        this.partDescriptorService = mock( PartDescriptorService.class );
        this.layoutDescriptorService = mock( LayoutDescriptorService.class );
        this.contentDataSerializer = new ContentDataSerializer();

        final ContentType contentType =
            ContentType.create().superType( ContentTypeName.documentMedia() ).name( ContentTypeName.dataMedia() ).build();

        when( contentTypeService.getByName( isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        mockNode = Node.create().id( NodeId.from( "testId" ) ).build();

        when( nodeService.rename( isA( RenameNodeParams.class ) ) ).thenReturn( mockNode );
        when( nodeService.modify( isA( UpdateNodeParams.class ) ) ).thenReturn( mock( ModifyNodeResult.class ) );
        when( nodeService.getById( mockNode.id() ) ).thenReturn( mockNode );
        when( translator.getContentDataSerializer() ).thenReturn( new ContentDataSerializer() );
    }

    @Test
    void test_valid_changed()
    {
        final Content content = createContent( true );

        when( this.nodeService.getById( any( NodeId.class ) ) ).thenReturn( mockNode );
        when( translator.fromNode( mockNode, false ) ).thenReturn( content );
        when( translator.fromNode( mockNode, true ) ).thenReturn( content );

        final RenameContentParams params =
            RenameContentParams.create().contentId( content.getId() ).newName( ContentName.unnamed() ).build();

        createCommand( params ).execute();

        verify( nodeService, times( 1 ) ).modify( isA( UpdateNodeParams.class ) );
    }

    @Test
    void test_valid_not_changed()
    {
        final Content content = createContent( false );

        when( translator.fromNode( mockNode, false ) ).thenReturn( content );
        when( translator.fromNode( mockNode, true ) ).thenReturn( content );

        final RenameContentParams params =
            RenameContentParams.create().contentId( content.getId() ).newName( ContentName.unnamed() ).build();

        createCommand( params ).execute();

        verify( contentService, never() ).update( isA( UpdateContentParams.class ) );
    }

    @Test
    void test_valid_not_changed_but_fiwer_errors()
    {
        final Content content = createContent( false );

        when( translator.fromNode( mockNode, false ) ).thenReturn( content );
        when( translator.fromNode( mockNode, true ) ).thenReturn( content );

        final RenameContentParams params =
            RenameContentParams.create().contentId( content.getId() ).newName( ContentName.unnamed() ).build();

        createCommand( params ).execute();

        verify( contentService, never() ).update( isA( UpdateContentParams.class ) );
    }

    @Test
    void test_already_exists()
    {
        Node mockNode = Node.create().id( NodeId.from( "testId" ) ).build();
        final RepositoryId repositoryId = RepositoryId.from( "some.repo" );
        final Branch branch = Branch.from( "somebranch" );
        when( nodeService.rename( isA( RenameNodeParams.class ) ) ).thenThrow(
            new NodeAlreadyExistAtPathException( new NodePath( "/content/mycontent2" ), repositoryId, branch ) );
        when( nodeService.getById( mockNode.id() ) ).thenReturn( mockNode );

        final Content content = createContent( true );

        final RenameContentCommand command =
            createCommand( RenameContentParams.create().contentId( content.getId() ).newName( ContentName.from( "mycontent2" ) ).build() );

        final ContentAlreadyExistsException exception = assertThrows( ContentAlreadyExistsException.class, command::execute );
        assertEquals( branch, exception.getBranch() );
        assertEquals( repositoryId, exception.getRepositoryId() );
    }

    private Content createContent( final boolean valid )
    {
        return Content.create()
            .id( ContentId.from( "testId" ) )
            .path( "/mycontent" )
            .creator( PrincipalKey.from( "user:system:anonymous" ) )
            .type( ContentTypeName.folder() )
            .data( new PropertyTree() )
            .valid( valid )
            .validationErrors( valid
                                   ? null
                                   : ValidationErrors.create()
                                       .add(
                                           ValidationError.dataError( ValidationErrorCode.from( ApplicationKey.from( "app" ), "SOME_CODE" ),
                                                                      PropertyPath.from( "" ) ).build() )
                                       .build() )
            .build();
    }

    private RenameContentCommand createCommand( final RenameContentParams params )
    {
        return RenameContentCommand.create( params )
            .contentTypeService( this.contentTypeService )
            .nodeService( this.nodeService )
            .translator( this.translator )
            .eventPublisher( this.eventPublisher )
            .xDataService( this.xDataService )
            .pageDescriptorService( this.pageDescriptorService )
            .partDescriptorService( this.partDescriptorService )
            .contentValidators( List.of( new ContentNameValidator() ) )
            .layoutDescriptorService( this.layoutDescriptorService )
            .build();
    }
}
