package com.enonic.xp.core.content;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.UpdateMetadataParams;
import com.enonic.xp.content.UpdateMetadataResult;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContentServiceImplTest_updateMetadata
    extends AbstractContentServiceTest
{

    @Test
    void update_metadata_language()
    {
        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "This is my content" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .build();

        final Content content = this.contentService.create( createContentParams );

        assertNull( content.getLanguage() );

        final UpdateMetadataParams updateMetadataParams = UpdateMetadataParams.create()
            .contentId( content.getId() )
            .editor( edit -> {
                edit.language = Locale.forLanguageTag( "en" );
            } )
            .build();

        final UpdateMetadataResult result = this.contentService.updateMetadata( updateMetadataParams );

        final Content updatedContent = this.contentService.getById( content.getId() );

        assertEquals( Locale.forLanguageTag( "en" ), updatedContent.getLanguage() );
        assertEquals( updatedContent.getCreatedTime(), content.getCreatedTime() );
        assertEquals( updatedContent.getModifier(), content.getModifier() );
        assertEquals( content.getId(), result.getContentId() );
    }

    @Test
    void update_metadata_owner()
    {
        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "This is my content" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .build();

        final Content content = this.contentService.create( createContentParams );

        final PrincipalKey originalOwner = content.getOwner();

        final PrincipalKey newOwner = PrincipalKey.from( "user:system:new-owner" );

        final UpdateMetadataParams updateMetadataParams = UpdateMetadataParams.create()
            .contentId( content.getId() )
            .editor( edit -> {
                edit.owner = newOwner;
            } )
            .build();

        final UpdateMetadataResult result = this.contentService.updateMetadata( updateMetadataParams );

        final Content updatedContent = this.contentService.getById( content.getId() );

        assertEquals( newOwner, updatedContent.getOwner() );
        assertEquals( updatedContent.getCreatedTime(), content.getCreatedTime() );
        assertEquals( updatedContent.getModifier(), content.getModifier() );
        assertEquals( content.getId(), result.getContentId() );
    }

    @Test
    void update_metadata_language_and_owner()
    {
        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "This is my content" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .build();

        final Content content = this.contentService.create( createContentParams );

        final PrincipalKey newOwner = PrincipalKey.from( "user:system:new-owner" );

        final UpdateMetadataParams updateMetadataParams = UpdateMetadataParams.create()
            .contentId( content.getId() )
            .editor( edit -> {
                edit.language = Locale.forLanguageTag( "no" );
                edit.owner = newOwner;
            } )
            .build();

        final UpdateMetadataResult result = this.contentService.updateMetadata( updateMetadataParams );

        final Content updatedContent = this.contentService.getById( content.getId() );

        assertEquals( Locale.forLanguageTag( "no" ), updatedContent.getLanguage() );
        assertEquals( newOwner, updatedContent.getOwner() );
        assertEquals( updatedContent.getCreatedTime(), content.getCreatedTime() );
        assertEquals( updatedContent.getModifier(), content.getModifier() );
        assertEquals( content.getId(), result.getContentId() );
    }

    @Test
    void update_metadata_without_admin_permissions()
    {
        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "This is my content" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .build();

        final Content content = this.contentService.create( createContentParams );

        final UpdateMetadataParams updateMetadataParams = UpdateMetadataParams.create()
            .contentId( content.getId() )
            .editor( edit -> {
                edit.language = Locale.forLanguageTag( "en" );
            } )
            .build();

        assertThrows( ForbiddenAccessException.class, () -> ContextBuilder.from( ContextAccessor.current() )
            .authInfo( AuthenticationInfo.create().user( ContextAccessor.current().getAuthInfo().getUser() ).build() )
            .build()
            .runWith( () -> {
                this.contentService.updateMetadata( updateMetadataParams );
            } ) );
    }
}
