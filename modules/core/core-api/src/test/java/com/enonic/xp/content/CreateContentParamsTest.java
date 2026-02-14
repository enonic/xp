package com.enonic.xp.content;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;

import static com.enonic.xp.security.acl.Permission.READ;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class CreateContentParamsTest
{

    @Test
    void allParameters()
    {
        final AccessControlList permissions =
            AccessControlList.of( AccessControlEntry.create().principal( PrincipalKey.ofAnonymous() ).allow( READ ).build() );

        final CreateContentParams createContentParams = CreateContentParams.create().
            type( ContentTypeName.site() ).
            parent( ContentPath.from( "/myPath/myContent" ) ).
            contentData( new PropertyTree() ).
            childOrder( ChildOrder.manualOrder() ).
            createAttachments( CreateAttachments.empty() ).
            displayName( "displayName" ).mixins( Mixins.empty() ).
            permissions( permissions ).
            language( Locale.forLanguageTag( "en" ) ).
            name( "name" ).
            owner( PrincipalKey.ofAnonymous() ).
            requireValid( true ).
            build();

        assertEquals( ContentTypeName.site(), createContentParams.getType() );
        assertEquals( ContentPath.from( "/myPath/myContent" ), createContentParams.getParent() );
        assertEquals( new PropertyTree(), createContentParams.getData() );
        assertEquals( ChildOrder.manualOrder(), createContentParams.getChildOrder() );
        assertEquals( CreateAttachments.empty(), createContentParams.getCreateAttachments() );
        assertEquals( "displayName", createContentParams.getDisplayName() );
        assertEquals( Mixins.empty(), createContentParams.getMixins() );
        assertEquals( permissions, createContentParams.getPermissions() );
        assertEquals( Locale.forLanguageTag( "en" ), createContentParams.getLanguage() );
        assertEquals( "name", createContentParams.getName().toString() );
        assertEquals( PrincipalKey.ofAnonymous(), createContentParams.getOwner() );
        assertEquals( true, createContentParams.isRequireValid() );
    }

    @Test
    void copyConstructor()
    {
        final AccessControlList permissions =
            AccessControlList.of( AccessControlEntry.create().principal( PrincipalKey.ofAnonymous() ).allow( READ ).build() );

        final CreateContentParams source = CreateContentParams.create().
            type( ContentTypeName.site() ).
            parent( ContentPath.from( "/myPath/myContent" ) ).
            contentData( new PropertyTree() ).
            childOrder( ChildOrder.manualOrder() ).
            createAttachments( CreateAttachments.empty() ).
            displayName( "displayName" ).mixins( Mixins.empty() ).
            permissions( permissions ).
            language( Locale.forLanguageTag( "en" ) ).
            name( ContentName.from( "name" ) ).
            owner( PrincipalKey.ofAnonymous() ).
            requireValid( true ).
            build();

        final CreateContentParams createContentParams = CreateContentParams.create( source ).build();

        assertEquals( ContentTypeName.site(), createContentParams.getType() );
        assertEquals( ContentPath.from( "/myPath/myContent" ), createContentParams.getParent() );
        assertEquals( new PropertyTree(), createContentParams.getData() );
        assertEquals( ChildOrder.manualOrder(), createContentParams.getChildOrder() );
        assertEquals( CreateAttachments.empty(), createContentParams.getCreateAttachments() );
        assertEquals( "displayName", createContentParams.getDisplayName() );
        assertEquals( Mixins.empty(), createContentParams.getMixins() );
        assertEquals( permissions, createContentParams.getPermissions() );
        assertEquals( Locale.forLanguageTag( "en" ), createContentParams.getLanguage() );
        assertEquals( "name", createContentParams.getName().toString() );
        assertEquals( PrincipalKey.ofAnonymous(), createContentParams.getOwner() );
        assertEquals( true, createContentParams.isRequireValid() );
    }

    @Test
    void missingContentParamDisplayName()
    {
        CreateContentParams.create().
            type( ContentTypeName.site() ).
            parent( ContentPath.from( "/myPath/myContent" ) ).
            contentData( new PropertyTree() ).
            build();
    }

    @Test
    void missingContentParamType()
    {
        try
        {
            CreateContentParams.create().
                parent( ContentPath.from( "/myPath/myContent" ) ).
                contentData( new PropertyTree() ).
                displayName( "displayName" ).
                build();
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertEquals( "type is required", e.getMessage() );
        }
    }

    @Test
    void missingContentParamParentPath()
    {
        try
        {
            CreateContentParams.create().
                type( ContentTypeName.site() ).
                contentData( new PropertyTree() ).
                displayName( "displayName" ).
                build();
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertEquals( "parentPath is required", e.getMessage() );
        }
    }

    @Test
    void missingContentParamData()
    {
        try
        {
            CreateContentParams.create().
                parent( ContentPath.from( "/myPath/myContent" ) ).
                type( ContentTypeName.site() ).
                displayName( "displayName" ).
                build();
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertEquals( "data is required", e.getMessage() );
        }
    }

}
