package com.enonic.xp.lib.content;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.site.SiteDescriptor;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CreateContentHandlerTest
    extends BaseContentHandlerTest
{
    private void mockCreateContent()
    {
        when( this.contentService.create( any( CreateContentParams.class ) ) ).thenAnswer(
            mock -> createContent( (CreateContentParams) mock.getArguments()[0] ) );

        final FormItemSet eSet = FormItemSet.create()
            .name( "e" )
            .addFormItem( Input.create().label( "f" ).name( "f" ).inputType( InputTypeName.DOUBLE ).build() )
            .addFormItem( Input.create().label( "g" ).name( "g" ).inputType( InputTypeName.CHECK_BOX ).build() )
            .build();

        final FormItemSet timeSet = FormItemSet.create()
            .name( "times" )
            .addFormItem( Input.create().label( "time" ).name( "time" ).inputType( InputTypeName.TIME ).build() )
            .addFormItem( Input.create().label( "date" ).name( "date" ).inputType( InputTypeName.DATE ).build() )
            .addFormItem( Input.create().label( "dateTime" ).name( "dateTime" ).inputType( InputTypeName.DATE_TIME ).build() )
            .addFormItem( Input.create()
                              .label( "instant" )
                              .name( "instant" )
                              .inputType( InputTypeName.DATE_TIME )
                              .inputTypeConfig(
                                  InputTypeConfig.create().property( InputTypeProperty.create( "timezone", "true" ).build() ).build() )
                              .build() )
            .build();

        final FormItemSet dSet = FormItemSet.create().name( "d" ).addFormItem( eSet ).build();

        final ContentType contentType = ContentType.create()
            .name( "test:myContentType" )
            .superType( ContentTypeName.structured() )
            .addFormItem( Input.create().label( "a" ).name( "a" ).inputType( InputTypeName.LONG ).build() )
            .addFormItem( Input.create().label( "b" ).name( "b" ).inputType( InputTypeName.LONG ).build() )
            .addFormItem( Input.create().label( "c" ).name( "c" ).occurrences( 0, 10 ).inputType( InputTypeName.TEXT_LINE ).build() )
            .addFormItem( dSet )
            .addFormItem( timeSet )
            .build();

        GetContentTypeParams getContentType = GetContentTypeParams.from( ContentTypeName.from( "test:myContentType" ) );
        when( this.contentTypeService.getByName( Mockito.eq( getContentType ) ) ).thenReturn( contentType );

        final PropertyTree extraData = new PropertyTree();
        extraData.addDouble( "a", 1.0 );
        extraData.addBoolean( "b", true );

        final XData xData = XData.create()
            .name( XDataName.from( "com.enonic.myapplication:myschema" ) )
            .addFormItem( Input.create().label( "a" ).name( "a" ).inputType( InputTypeName.DOUBLE ).build() )
            .addFormItem( Input.create().label( "b" ).name( "b" ).inputType( InputTypeName.CHECK_BOX ).build() )
            .build();

        final SiteDescriptor siteDescriptor1 = SiteDescriptor.create()
            .form( Form.create()
                       .addFormItem( Input.create().label( "a" ).name( "a" ).inputType( InputTypeName.TEXT_LINE ).build() )
                       .addFormItem( Input.create().label( "b" ).name( "b" ).inputType( InputTypeName.CHECK_BOX ).build() )
                       .build() )
            .build();

        final SiteDescriptor siteDescriptor2 = SiteDescriptor.create()
            .form( Form.create().addFormItem( Input.create().label( "c" ).name( "c" ).inputType( InputTypeName.LONG ).build() ).build() )
            .build();

        when( this.siteService.getDescriptor( ApplicationKey.from( "appKey1" ) ) ).thenReturn( siteDescriptor1 );
        when( this.siteService.getDescriptor( ApplicationKey.from( "appKey2" ) ) ).thenReturn( siteDescriptor2 );

        when( this.xDataService.getByName( Mockito.eq( XDataName.from( "com.enonic.myapplication:myschema" ) ) ) ).thenReturn( xData );
        when( this.mixinService.inlineFormItems( any( Form.class ) ) ).then( returnsFirstArg() );
    }

    @Test
    public void testExample()
    {
        mockCreateContent();
        runScript( "/lib/xp/examples/content/create.js" );
    }

    @Test
    public void createContent()
        throws Exception
    {
        mockCreateContent();
        runFunction( "/test/CreateContentHandlerTest.js", "createContent" );
    }

    @Test
    public void createContentWithChildOrder()
        throws Exception
    {
        mockCreateContent();
        runFunction( "/test/CreateContentHandlerTest.js", "createContentWithChildOrder" );
    }

    @Test
    public void createContentAlreadyExists()
        throws Exception
    {
        final Exception alreadyExistException =
            new ContentAlreadyExistsException( ContentPath.from( "/a/b/mycontent" ), RepositoryId.from( "some.repo" ),
                                               Branch.from( "draft" ) );
        when( this.contentService.create( any( CreateContentParams.class ) ) ).thenThrow( alreadyExistException );

        final ContentType contentType = ContentType.create().name( "test:myContentType" ).superType( ContentTypeName.structured() ).build();

        GetContentTypeParams getContentType = GetContentTypeParams.from( ContentTypeName.from( "test:myContentType" ) );
        when( this.contentTypeService.getByName( Mockito.eq( getContentType ) ) ).thenReturn( contentType );

        runFunction( "/test/CreateContentHandlerTest.js", "createContentNameAlreadyExists" );
    }

    @Test
    public void createContentAutoGenerateName()
        throws Exception
    {
        when( this.contentService.create( any( CreateContentParams.class ) ) ).thenAnswer(
            mock -> createContent( (CreateContentParams) mock.getArguments()[0] ) );

        final ContentType contentType = ContentType.create().name( "test:myContentType" ).superType( ContentTypeName.structured() ).build();

        GetContentTypeParams getContentType = GetContentTypeParams.from( ContentTypeName.from( "test:myContentType" ) );
        when( this.contentTypeService.getByName( Mockito.eq( getContentType ) ) ).thenReturn( contentType );

        runFunction( "/test/CreateContentHandlerTest.js", "createContentAutoGenerateName" );
    }

    @Test
    public void createContentAutoGenerateNameWithExistingName()
        throws Exception
    {
        when( this.contentService.create( any( CreateContentParams.class ) ) ).thenAnswer(
            mock -> createContent( (CreateContentParams) mock.getArguments()[0] ) );

        when( this.contentService.contentExists( Mockito.eq( ContentPath.from( "/a/b/my-content" ) ) ) ).thenReturn( true );
        when( this.contentService.contentExists( Mockito.eq( ContentPath.from( "/a/b/my-content-1" ) ) ) ).thenReturn( true );
        when( this.contentService.contentExists( Mockito.eq( ContentPath.from( "/a/b/my-content-2" ) ) ) ).thenReturn( true );

        final ContentType contentType = ContentType.create().name( "test:myContentType" ).superType( ContentTypeName.structured() ).build();

        GetContentTypeParams getContentType = GetContentTypeParams.from( ContentTypeName.from( "test:myContentType" ) );
        when( this.contentTypeService.getByName( Mockito.eq( getContentType ) ) ).thenReturn( contentType );

        runFunction( "/test/CreateContentHandlerTest.js", "createContentAutoGenerateNameWithExistingName" );
    }

    @Test
    public void createContentWithWorkflow()
        throws Exception
    {
        when( this.contentService.create( any( CreateContentParams.class ) ) ).thenAnswer(
            mock -> createContent( (CreateContentParams) mock.getArguments()[0] ) );

        final ContentType contentType = ContentType.create().name( "test:myContentType" ).superType( ContentTypeName.structured() ).build();

        GetContentTypeParams getContentType = GetContentTypeParams.from( ContentTypeName.from( "test:myContentType" ) );
        when( this.contentTypeService.getByName( Mockito.eq( getContentType ) ) ).thenReturn( contentType );

        runFunction( "/test/CreateContentHandlerTest.js", "createContentWithWorkflow" );
    }

    private Content createContent( final CreateContentParams params )
    {
        final Content.Builder builder = Content.create();
        builder.id( ContentId.from( "123456" ) );
        builder.name( params.getName() );
        builder.parentPath( params.getParent() );
        builder.displayName( params.getDisplayName() );
        builder.valid( false );
        builder.type( params.getType() );
        builder.data( params.getData() );
        builder.creator( PrincipalKey.ofAnonymous() );
        builder.createdTime( Instant.parse( "1975-01-08T00:00:00Z" ) );
        builder.language( params.getLanguage() );
        builder.childOrder( params.getChildOrder() );
        builder.workflowInfo( params.getWorkflowInfo() );

        if ( params.getExtraDatas() != null )
        {
            builder.extraDatas( ExtraDatas.from( params.getExtraDatas() ) );
        }

        return builder.build();
    }

}
