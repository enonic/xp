package com.enonic.wem.repo.internal.elasticsearch.storage;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.repo.internal.storage.StorageDao;
import com.enonic.wem.repo.internal.storage.StorageDocument;
import com.enonic.wem.repo.internal.storage.StorageSettings;

@Component
public class ElasticsearchStorageDao
    implements StorageDao
{
    private final static Logger LOG = LoggerFactory.getLogger( ElasticsearchStorageDao.class );

    private final String storeTimeout = "5s";

    private Client client;

    @Override
    public String store( final StorageDocument doc )
    {
        final StorageSettings settings = doc.getSettings();

        final IndexRequest request = Requests.indexRequest().
            index( settings.getStorageName().getName() ).
            type( settings.getStorageType().getName() ).
            source( XContentBuilderFactory.create( doc ) ).
            id( doc.getId() ).
            refresh( settings.forceRefresh() );

        if ( settings.getRouting() != null )
        {
            request.routing( settings.getRouting() );
        }

        if ( settings.getParent() != null )
        {
            request.parent( settings.getParent() );
        }

        return doStore( request );
    }

    private String doStore( final IndexRequest request )
    {

        final IndexResponse indexResponse = this.client.index( request ).
            actionGet( storeTimeout );

        return indexResponse.getId();
    }


    @Reference
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
