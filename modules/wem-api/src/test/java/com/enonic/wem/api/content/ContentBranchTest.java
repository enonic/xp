package com.enonic.wem.api.content;


import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.wem.api.account.AccountKey;

import static com.enonic.wem.api.content.ContentBranch.newContentBranch;
import static junit.framework.Assert.assertEquals;

public class ContentBranchTest
{
    @Test
    public void size()
    {
        ContentBranch.Builder builder = newContentBranch().parent( createContent( "a" ) );
        builder.addChild( createContent( "a/a-a" ) );
        builder.addChild( createContent( "a/a-b" ) );
        ContentBranch contentBranch = builder.build();
        assertEquals( 2, contentBranch.size() );
    }

    @Test
    public void deepSize()
    {
        ContentBranch.Builder builder = newContentBranch().parent( createContent( "a" ) );
        builder.addChild( createContent( "a/a-a" ) );
        ContentBranch branchAAB = newContentBranch().parent( createContent( "a/a-b" ) ).addChild( createContent( "a/a-b/a" ) ).build();
        builder.addChild( branchAAB );
        ContentBranch contentBranch = builder.build();
        assertEquals( 3, contentBranch.deepSize() );
    }

    private Content createContent( String path )
    {
        return Content.newContent().path( ContentPath.from( path ) ).createdTime( DateTime.now() ).owner(
            AccountKey.user( "myStore:me" ) ).build();
    }
}
