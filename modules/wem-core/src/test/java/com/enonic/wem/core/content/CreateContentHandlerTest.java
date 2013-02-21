package com.enonic.wem.core.content;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.relationship.CreateRelationship;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.data.type.DataTypes;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.content.dao.ContentIdFactory;
import com.enonic.wem.core.search.IndexService;

import static junit.framework.Assert.assertNotNull;

public class CreateContentHandlerTest
    extends AbstractCommandHandlerTest
{
    private CreateContentHandler handler;

    private ContentDao contentDao;

    private IndexService indexService;


    @Before
    public void setUp()
        throws Exception
    {
        super.client = Mockito.mock( Client.class );

        super.initialize();

        contentDao = Mockito.mock( ContentDao.class );
        indexService = Mockito.mock( IndexService.class );

        handler = new CreateContentHandler();
        handler.setContentDao( contentDao );
        handler.setIndexService( indexService );
    }

    @Test
    public void createContent()
        throws Exception
    {
        // setup
        Mockito.when( contentDao.create( Mockito.isA( Content.class ), Mockito.any( Session.class ) ) ).thenReturn(
            ContentIdFactory.from( "100" ) );

        CreateContent command = Commands.content().create();
        command.displayName( "My Content" );
        command.contentPath( ContentPath.from( "myContent" ) );
        command.owner( UserKey.from( "myStore:myUser" ) );
        command.contentType( new QualifiedContentTypeName( ModuleName.SYSTEM, "MyContentType" ) );
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setData( "myText", "abc" );
        rootDataSet.setData( "myReference", DataTypes.CONTENT_REFERENCE, "123" );
        rootDataSet.setData( "mySet.myRelatedContent", DataTypes.CONTENT_REFERENCE, "124" );
        command.rootDataSet( rootDataSet );

        // exercise
        this.handler.handle( this.context, command );

        // verify
        Mockito.verify( contentDao, Mockito.atLeastOnce() ).create( Mockito.isA( Content.class ), Mockito.any( Session.class ) );
        Mockito.verify( indexService, Mockito.atLeastOnce() ).indexContent( Mockito.isA( Content.class ) );
        Mockito.verify( super.client, Mockito.times( 2 ) ).execute( Mockito.isA( CreateRelationship.class ) );

        ContentId contentId = command.getResult();
        assertNotNull( contentId );
    }

}
