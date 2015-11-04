package com.enonic.xp.lib.content;

import java.time.Instant;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAlreadyExistException;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.security.PrincipalKey;

public class CreateContentHandlerTest
    extends BaseContentHandlerTest
{
    private final static char[] REPLACE_WITH_HYPHEN_CHARS =
        {'$', '&', '|', ':', ';', '#', '/', '\\', '<', '>', '\"', '*', '+', ',', '=', '@', '%', '{', '}', '[', ']', '`', '~', '^', '_'};

    @Test
    public void createContent()
        throws Exception
    {
        Mockito.when( this.contentService.create( Mockito.any( CreateContentParams.class ) ) ).thenAnswer(
            mock -> createContent( (CreateContentParams) mock.getArguments()[0] ) );

        final FormItemSet eSet = FormItemSet.create().
            name( "e" ).
            addFormItem( Input.create().
                label( "f" ).
                name( "f" ).
                inputType( InputTypeName.DOUBLE ).
                build() ).
            addFormItem( Input.create().
                label( "g" ).
                name( "g" ).
                inputType( InputTypeName.CHECK_BOX ).
                build() ).
            build();

        final FormItemSet dSet = FormItemSet.create().
            name( "d" ).
            addFormItem( eSet ).
            build();

        final ContentType contentType = ContentType.create().
            name( "test:myContentType" ).
            superType( ContentTypeName.structured() ).
            addFormItem( Input.create().
                label( "a" ).
                name( "a" ).
                inputType( InputTypeName.LONG ).
                build() ).
            addFormItem( Input.create().
                label( "b" ).
                name( "b" ).
                inputType( InputTypeName.LONG ).
                build() ).
            addFormItem( Input.create().
                label( "c" ).
                name( "c" ).
                occurrences( 0, 10 ).
                inputType( InputTypeName.TEXT_LINE ).
                build() ).
            addFormItem( dSet ).
            build();

        GetContentTypeParams getContentType = GetContentTypeParams.from( ContentTypeName.from( "test:myContentType" ) );
        Mockito.when( this.contentTypeService.getByName( Mockito.eq( getContentType ) ) ).thenReturn( contentType );

        final PropertyTree extraData = new PropertyTree();
        extraData.addDouble( "a", 1.0 );

        final Mixin mixin = Mixin.create().
            name( "com.enonic.myapplication:myschema" ).
            addFormItem( Input.create().
                label( "a" ).
                name( "a" ).
                inputType( InputTypeName.DOUBLE ).
                build() ).
            build();
        Mockito.when( this.mixinService.getByName( Mockito.eq( MixinName.from( "com.enonic.myapplication:myschema" ) ) ) ).thenReturn(
            mixin );

        runTestFunction( "/test/CreateContentHandlerTest.js", "createContent" );
    }

    @Test
    public void createContentAlreadyExists()
        throws Exception
    {
        final Exception alreadyExistException = new ContentAlreadyExistException( ContentPath.from( "/a/b/mycontent" ) );
        Mockito.when( this.contentService.create( Mockito.any( CreateContentParams.class ) ) ).thenThrow( alreadyExistException );

        final ContentType contentType = ContentType.create().
            name( "test:myContentType" ).
            superType( ContentTypeName.structured() ).
            build();

        GetContentTypeParams getContentType = GetContentTypeParams.from( ContentTypeName.from( "test:myContentType" ) );
        Mockito.when( this.contentTypeService.getByName( Mockito.eq( getContentType ) ) ).thenReturn( contentType );

        runTestFunction( "/test/CreateContentHandlerTest.js", "createContentNameAlreadyExists" );
    }

    @Test
    public void createContentAutoGenerateName()
        throws Exception
    {
        Mockito.when( this.contentService.generateContentName( Mockito.anyString() ) ).thenAnswer(
            mock -> mockGenerateContentName( (String) mock.getArguments()[0] ) );
        Mockito.when( this.contentService.create( Mockito.any( CreateContentParams.class ) ) ).thenAnswer(
            mock -> createContent( (CreateContentParams) mock.getArguments()[0] ) );

        final ContentType contentType = ContentType.create().
            name( "test:myContentType" ).
            superType( ContentTypeName.structured() ).
            build();

        GetContentTypeParams getContentType = GetContentTypeParams.from( ContentTypeName.from( "test:myContentType" ) );
        Mockito.when( this.contentTypeService.getByName( Mockito.eq( getContentType ) ) ).thenReturn( contentType );

        runTestFunction( "/test/CreateContentHandlerTest.js", "createContentAutoGenerateName" );
    }

    @Test
    public void createContentAutoGenerateNameWithExistingName()
        throws Exception
    {
        Mockito.when( this.contentService.generateContentName( Mockito.anyString() ) ).thenAnswer(
            mock -> mockGenerateContentName( (String) mock.getArguments()[0] ) );
        Mockito.when( this.contentService.create( Mockito.any( CreateContentParams.class ) ) ).thenAnswer(
            mock -> createContent( (CreateContentParams) mock.getArguments()[0] ) );

        Mockito.when( this.contentService.contentExists( Mockito.eq( ContentPath.from( "/a/b/my-content" ) ) ) ).thenReturn( true );
        Mockito.when( this.contentService.contentExists( Mockito.eq( ContentPath.from( "/a/b/my-content-1" ) ) ) ).thenReturn( true );
        Mockito.when( this.contentService.contentExists( Mockito.eq( ContentPath.from( "/a/b/my-content-2" ) ) ) ).thenReturn( true );

        final ContentType contentType = ContentType.create().
            name( "test:myContentType" ).
            superType( ContentTypeName.structured() ).
            build();

        GetContentTypeParams getContentType = GetContentTypeParams.from( ContentTypeName.from( "test:myContentType" ) );
        Mockito.when( this.contentTypeService.getByName( Mockito.eq( getContentType ) ) ).thenReturn( contentType );

        runTestFunction( "/test/CreateContentHandlerTest.js", "createContentAutoGenerateNameWithExistingName" );
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

        if ( params.getExtraDatas() != null )
        {
            builder.extraDatas( ExtraDatas.from( params.getExtraDatas() ) );
        }

        return builder.build();
    }

    private String mockGenerateContentName( final String displayName )
    {
        String prettifiedName = displayName.toLowerCase().replaceAll( "\\s+", "-" );
        for ( char toBeReplaced : REPLACE_WITH_HYPHEN_CHARS )
        {
            prettifiedName = prettifiedName.replace( toBeReplaced, '-' );
        }
        return prettifiedName;
    }
}
