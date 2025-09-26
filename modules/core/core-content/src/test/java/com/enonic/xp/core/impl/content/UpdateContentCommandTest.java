package com.enonic.xp.core.impl.content;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentDataValidationException;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.content.processor.ContentProcessor;
import com.enonic.xp.core.impl.content.processor.ProcessCreateParams;
import com.enonic.xp.core.impl.content.processor.ProcessCreateResult;
import com.enonic.xp.core.impl.content.processor.ProcessUpdateParams;
import com.enonic.xp.core.impl.content.processor.ProcessUpdateResult;
import com.enonic.xp.core.impl.content.serializer.ContentDataSerializer;
import com.enonic.xp.core.impl.content.validate.ContentNameValidator;
import com.enonic.xp.core.impl.content.validate.ExtraDataValidator;
import com.enonic.xp.core.impl.content.validate.OccurrenceValidator;
import com.enonic.xp.core.impl.content.validate.SiteConfigsValidator;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.node.PatchNodeResult;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UpdateContentCommandTest
{
    private static final Instant CREATED_TIME = LocalDateTime.of( 2013, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC );

    private final ContentTypeService contentTypeService = mock( ContentTypeService.class );

    private final XDataService xDataService = mock( XDataService.class );

    private final SiteService siteService = mock( SiteService.class );

    private final NodeService nodeService = mock( NodeService.class );

    private final PageDescriptorService pageDescriptorService = mock( PageDescriptorService.class );

    private final PartDescriptorService partDescriptorService = mock( PartDescriptorService.class );

    private final LayoutDescriptorService layoutDescriptorService = mock( LayoutDescriptorService.class );

    private final ContentNodeTranslator translator = mock( ContentNodeTranslator.class );

    private final EventPublisher eventPublisher = mock( EventPublisher.class );

    private final MediaInfo mediaInfo = MediaInfo.create().mediaType( "image/jpeg" ).build();

    @Test
    void given_content_not_found_when_handle_then_NOT_FOUND_is_returned()
    {
        ContentId contentId = ContentId.from( "mycontent" );

        UpdateContentCommand command = createCommand( new UpdateContentParams().contentId( contentId ).editor( edit -> {
        } ) );

        when( nodeService.getById( isA( NodeId.class ) ) ).thenThrow( new NodeNotFoundException( "Node not found" ) );

        assertThrows( ContentNotFoundException.class, command::execute );
    }

    @Test
    void contentDao_update_not_invoked_when_nothing_is_changed()
    {
        Content existingContent = createContent( new PropertyTree() );

        UpdateContentCommand command = createCommand( new UpdateContentParams().contentId( existingContent.getId() ).editor( edit -> {
        } ) );

        Node mockNode = Node.create().build();
        when( nodeService.getById( NodeId.from( existingContent.getId() ) ) ).thenReturn( mockNode );
        when( translator.fromNode( mockNode ) ).thenReturn( existingContent );
        when( contentTypeService.getByName( isA( GetContentTypeParams.class ) ) ).thenReturn( mock( ContentType.class ) );

        command.execute();

        verify( nodeService, never() ).update( isA( UpdateNodeParams.class ) );
    }

    @Test
    void stop_inherit_content()
    {
        Content existingContent =
            Content.create( createContent( new PropertyTree() ) ).setInherit( EnumSet.of( ContentInheritType.CONTENT ) ).build();

        UpdateContentParams params = new UpdateContentParams().stopInherit( true )
            .contentId( existingContent.getId() )
            .editor( edit -> edit.language = Locale.CANADA );

        UpdateContentCommand command = createCommand( params );

        Node mockNode = Node.create().build();
        when( nodeService.getById( NodeId.from( existingContent.getId() ) ) ).thenReturn( mockNode );
        when( translator.fromNode( mockNode ) ).thenReturn( existingContent );
        when( translator.getContentDataSerializer() ).thenReturn( new ContentDataSerializer() );

        ContentType contentType = mock( ContentType.class );
        when( contentTypeService.getByName( isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );
        when( contentType.getForm() ).thenReturn( Form.create().build() );
        when( nodeService.patch( isA( PatchNodeParams.class ) ) ).thenReturn( PatchNodeResult.create().build() );

        ContextBuilder.from( ContextAccessor.current() ).branch( ContentConstants.BRANCH_DRAFT ).build().runWith( command::execute );

        verify( nodeService, times( 1 ) ).commit( isA( NodeCommitEntry.class ),
                                                  eq( NodeIds.from( NodeId.from( existingContent.getId().toString() ) ) ) );
    }

    @Test
    void site_config_modified_no_project_owner_role_throws_exception() {
        final Content existingContent = Site.create()
            .id( ContentId.from( "mycontent" ) )
            .name( "myContentName" )
            .type( ContentTypeName.site() )
            .parentPath( ContentPath.ROOT )
            .data( new PropertyTree() )
            .siteConfigs( SiteConfigs.create()
                              .add( SiteConfig.create().config( new PropertyTree() ).application( ApplicationKey.SYSTEM ).build() )
                              .build() )
            .build();

        final UpdateContentParams params =
            new UpdateContentParams().editor( c -> c.data.removeProperties( "siteConfig" ) ).contentId( existingContent.getId() );

        final UpdateContentCommand command = UpdateContentCommand.create( createCommand( params ) )
            .params( params )
            .build();

        Node mockNode = Node.create().build();
        when( nodeService.getById( NodeId.from( existingContent.getId() ) ) ).thenReturn( mockNode );
        when( translator.fromNode( mockNode ) ).thenReturn( existingContent );
        when( translator.getContentDataSerializer() ).thenReturn( new ContentDataSerializer() );

        final ContentType contentType = ContentType.create()
            .superType( ContentTypeName.structured() )
            .name( "myapplication:my_type" )
            .addFormItem( FieldSet.create()
                              .label( "My layout" )
                              .addFormItem( FormItemSet.create()
                                                .name( "mySet" )
                                                .required( true )
                                                .addFormItem( Input.create()
                                                                  .name( "myInput" )
                                                                  .label( "Input" )
                                                                  .inputType( InputTypeName.TEXT_LINE )
                                                                  .build() )
                                                .build() )
                              .build() )
            .build();

        when( contentTypeService.getByName( isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        assertThrows( ForbiddenAccessException.class, () -> ContextBuilder.from( ContextAccessor.current() )
            .repositoryId(  "com.enonic.cms.context-repo" )
            .branch( ContentConstants.BRANCH_DRAFT )
            .build()
            .runWith( command::execute ) );
    }

    @Test
    void processors_can_modify_content()
    {
        Content existingContent = createContent( new PropertyTree() );

        final UpdateContentParams params = new UpdateContentParams().contentId( existingContent.getId() );

        final UpdateContentCommand command = UpdateContentCommand.create( createCommand( params ) )
            .params( params )
            .contentProcessors( List.of( new ContentProcessor()
            {
                @Override
                public boolean supports( final ContentTypeName contentType )
                {
                    return true;
                }

                @Override
                public ProcessCreateResult processCreate( final ProcessCreateParams params )
                {
                    return null;
                }

                @Override
                public ProcessUpdateResult processUpdate( final ProcessUpdateParams params )
                {
                    // without content modification node API will not be called
                    return new ProcessUpdateResult( Content.create(params.getContent()).name( "newName" ).build() );
                }
            } ) )
            .build();

        Node mockNode = Node.create().build();
        when( nodeService.getById( NodeId.from( existingContent.getId() ) ) ).thenReturn( mockNode );
        when( translator.fromNode( mockNode ) ).thenReturn( existingContent );
        when( translator.getContentDataSerializer() ).thenReturn( new ContentDataSerializer() );

        ContentType contentType = mock( ContentType.class );
        when( contentTypeService.getByName( any() ) ).thenReturn( contentType );
        when( contentType.getForm() ).thenReturn( Form.empty() );
        when( nodeService.patch( any() ) ).thenReturn( PatchNodeResult.create().build() );

        ContextBuilder.from( ContextAccessor.current() ).branch( ContentConstants.BRANCH_DRAFT ).build().runWith( command::execute );

        final ArgumentCaptor<PatchNodeParams> captor = ArgumentCaptor.forClass( PatchNodeParams.class );
        verify( nodeService, times(1) ).patch( captor.capture() );
    }

    @Test
    void update_to_invalid_with_require_valid()
    {
        final Content existingContent =
            Content.create( createContent( new PropertyTree() ) ).setInherit( EnumSet.of( ContentInheritType.CONTENT ) ).build();

        final UpdateContentParams params =
            new UpdateContentParams().stopInherit( true ).contentId( existingContent.getId() ).requireValid( true )

                .editor( edit -> edit.language = Locale.CANADA );

        final UpdateContentCommand command = UpdateContentCommand.create( createCommand( params ) )
            .params( params )
            .contentValidators( List.of( new ContentNameValidator(), new SiteConfigsValidator( siteService ), new OccurrenceValidator(),
                                         new ExtraDataValidator( xDataService ) ) )
            .build();

        Node mockNode = Node.create().build();
        when( nodeService.getById( NodeId.from( existingContent.getId() ) ) ).thenReturn( mockNode );
        when( translator.fromNode( mockNode ) ).thenReturn( existingContent );
        when( translator.getContentDataSerializer() ).thenReturn( new ContentDataSerializer() );

        final ContentType contentType = ContentType.create()
            .superType( ContentTypeName.structured() )
            .name( "myapplication:my_type" )
            .addFormItem( FieldSet.create()
                              .label( "My layout" )
                              .addFormItem( FormItemSet.create()
                                                .name( "mySet" )
                                                .required( true )
                                                .addFormItem( Input.create()
                                                                  .name( "myInput" )
                                                                  .label( "Input" )
                                                                  .inputType( InputTypeName.TEXT_LINE )
                                                                  .build() )
                                                .build() )
                              .build() )
            .build();

        when( contentTypeService.getByName( isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );
        when( nodeService.patch( isA( PatchNodeParams.class ) ) ).thenReturn( PatchNodeResult.create().build() );

        assertThrows( ContentDataValidationException.class, () -> ContextBuilder.from( ContextAccessor.current() )
            .branch( ContentConstants.BRANCH_DRAFT )
            .build()
            .runWith( command::execute ) );
    }

    private UpdateContentCommand createCommand( UpdateContentParams params )
    {
        return UpdateContentCommand.create( params )
            .contentTypeService( contentTypeService )
            .nodeService( nodeService )
            .translator( translator )
            .eventPublisher( eventPublisher )
            .mediaInfo( mediaInfo )
            .xDataService( xDataService )
            .siteService( siteService )
            .pageDescriptorService( pageDescriptorService )
            .partDescriptorService( partDescriptorService )
            .layoutDescriptorService( layoutDescriptorService )
            .build();
    }

    private Content createContent( PropertyTree contentData )
    {
        return Content.create()
            .id( ContentId.from( "1" ) )
            .parentPath( ContentPath.ROOT )
            .name( "mycontent" )
            .createdTime( CREATED_TIME )
            .displayName( "MyContent" )
            .owner( PrincipalKey.from( "user:system:admin" ) ).creator( PrincipalKey.from( "user:system:admin" ) )
            .data( contentData )
            .build();
    }
}
