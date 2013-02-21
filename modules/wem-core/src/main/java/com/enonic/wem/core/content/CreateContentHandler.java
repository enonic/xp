package com.enonic.wem.core.content;

import javax.jcr.Session;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.search.IndexService;

@Component
public class CreateContentHandler
    extends CommandHandler<CreateContent>
{
    private ContentDao contentDao;

    private IndexService indexService;

    private final static Logger LOG = LoggerFactory.getLogger( CreateContentHandler.class );

    public CreateContentHandler()
    {
        super( CreateContent.class );
    }

    @Override
    public void handle( final CommandContext context, final CreateContent command )
        throws Exception
    {
        final Content.Builder builder = Content.newContent();
        builder.path( command.getContentPath() );
        builder.rootDataSet( command.getRootDataSet() );
        builder.type( command.getContentType() );
        builder.displayName( command.getDisplayName() );
        builder.createdTime( DateTime.now() );
        builder.modifiedTime( DateTime.now() );
        builder.owner( command.getOwner() );
        builder.modifier( command.getOwner() );

        final Content content = builder.build();

        final Session session = context.getJcrSession();
        final ContentId contentId = contentDao.create( content, session );
        session.save();

        /*final List<Data> references = resolveReferences( content.getRootDataSet() );
        for ( Data reference : references )
        {
            final ContentId toContent = ContentIdFactory.from( reference.asString() );
            final CreateRelationship createRelationship = Commands.relationship().create();
            createRelationship.fromContent( contentId );
            createRelationship.toContent( toContent );
            createRelationship.type( QualifiedRelationshipTypeName.from( "system:default" ) );
            createRelationship.managed( reference.getPath() );

            //context.getClient().execute( createRelationship );
        }*/

        try
        {
            // TODO: Temporary easy solution to get Id. The index logic should eventually not be here anyway
            final Content storedContent = builder.id( contentId ).build();
            indexService.indexContent( storedContent );
        }
        catch ( Exception e )
        {
            LOG.error( "Index content failed", e );
        }

        command.setResult( contentId );
    }

    /*private List<Data> resolveReferences( final RootDataSet rootDataSet )
    {
        final List<Data> references = new ArrayList<>();
        final DataVisitor dataVisitor = new DataVisitor()
        {
            @Override
            public void visit( final Data reference )
            {
                references.add( reference );
            }
        };
        dataVisitor.restrictType( DataTypes.REFERENCE );
        dataVisitor.traverse( rootDataSet );
        return references;
    }*/

    @Inject
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    @Inject
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }
}
