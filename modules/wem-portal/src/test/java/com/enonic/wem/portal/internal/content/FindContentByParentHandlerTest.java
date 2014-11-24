package com.enonic.wem.portal.internal.content;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.FindContentByParentResult;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.script.AbstractScriptTest;

import static com.enonic.wem.api.content.Content.newContent;
import static org.mockito.Matchers.anyObject;

public class FindContentByParentHandlerTest
    extends AbstractScriptTest
{
    private ContentService contentService;

    @Before
    public void setUp()
    {
        this.contentService = Mockito.mock( ContentService.class );
        addHandler( new FindContentByParentHandler( this.contentService ) );
    }

    @Test
    public void getContentByIdTest()
    {
        final Content content = newContent().
            id( ContentId.from( "123" ) ).
            path( ContentPath.from( "/some/path" ) ).
            createdTime( Instant.now() ).
            owner( PrincipalKey.from( "myStore:user:me" ) ).
            displayName( "My Content" ).
            modifiedTime( Instant.now() ).
            modifier( PrincipalKey.from( "system:user:admin" ) ).
            type( ContentTypeName.from( "contenttype" ) ).
            build();
        final Contents contents = Contents.from( content );
        final FindContentByParentResult findContentsResult = FindContentByParentResult.create().
            contents( contents ).
            build();
        Mockito.when( this.contentService.findByParent( anyObject() ) ).thenReturn( findContentsResult );

        runTestScript( "findContentByParent-test.js" );
    }
}
