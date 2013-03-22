package com.enonic.wem.core.content;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.Client;
import com.enonic.wem.core.initializer.InitializerTask;
import com.enonic.wem.core.support.BaseInitializer;

@Component
@Order(15)
public class ContentInitializer
    extends BaseInitializer
    implements InitializerTask
{

    private Client client;

    protected ContentInitializer()
    {
        super( "content" );
    }

    @Override
    public void initialize()
        throws Exception
    {

    }

    private void doCreateContent( final String parentPath, final String contentTypeName, final String displayName )
    {
        /*
        final ContentPath parentContentPath = ContentPath.from( parentPath );

        final QualifiedContentTypeName qualifiedContentTypeName = QualifiedContentTypeName.from( contentTypeName );

        final CreateContent createContent = content().create();
        createContent.parentContentPath( parentContentPath );
        createContent.contentType( qualifiedContentTypeName );
       // createContent.rootDataSet( rootDataSet );
        createContent.displayName( displayName );
        createContent.owner( AccountKey.anonymous() );
        final CreateContentResult createContentResult = client.execute( createContent );
          */
    }

}
