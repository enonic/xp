package com.enonic.wem.core.content;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.GetContentTree;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.dao.ContentDao;

public class GetContentTreeHandlerTest
    extends AbstractCommandHandlerTest
{
    private GetContentTreeHandler handler;

    private ContentDao contentDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.client = Mockito.mock( Client.class );

        super.initialize();

        contentDao = Mockito.mock( ContentDao.class );

        handler = new GetContentTreeHandler();
        handler.setContext( this.context );
        handler.setContentDao( contentDao );
    }


    @Test
    public void getContentTree_no_topNodes()
        throws Exception
    {
        GetContentTree command = new GetContentTree();

        Mockito.when( contentDao.getContentTree( Mockito.isA( Session.class ) ) ).thenReturn( new Tree<Content>() );

        // exercise
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        Mockito.verify( contentDao, Mockito.times( 1 ) ).getContentTree( Mockito.isA( Session.class ) );
    }

    @Test
    public void getContentTree_with_topLevelNodes()
        throws Exception
    {
        GetContentTree command = new GetContentTree();
        final ContentIds topLevelNodes = ContentIds.from( "a", "b" );
        command.selectors( topLevelNodes );

        Mockito.when( contentDao.getContentTree( Mockito.isA( Session.class ), Mockito.eq( topLevelNodes ) ) ).thenReturn(
            new Tree<Content>() );

        // exercise
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        Mockito.verify( contentDao, Mockito.times( 1 ) ).getContentTree( Mockito.isA( Session.class ), Mockito.eq( topLevelNodes ) );
    }


}
