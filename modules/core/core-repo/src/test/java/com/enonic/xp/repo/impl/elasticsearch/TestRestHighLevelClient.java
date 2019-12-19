package com.enonic.xp.repo.impl.elasticsearch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.xcontent.ContextParser;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.composite.ParsedComposite;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringRareTerms;

public class TestRestHighLevelClient
    extends RestHighLevelClient
{
    static final List<NamedXContentRegistry.Entry> REGISTRY;

    static
    {
        final Map<String, ContextParser<Object, ? extends Aggregation>> map = new HashMap<>();
        map.put( StringRareTerms.NAME, ( p, c ) -> ParsedStringTerms.fromXContent( p, (String) c ) );
        map.put( ParsedComposite.class.getSimpleName(), ( p, c ) -> ParsedComposite.fromXContent( p, (String) c ) );

        REGISTRY = map.entrySet().stream().map(
            entry -> new NamedXContentRegistry.Entry( Aggregation.class, new ParseField( entry.getKey() ), entry.getValue() ) ).collect(
            Collectors.toList() );
    }

    public TestRestHighLevelClient( final RestClientBuilder restClientBuilder )
    {
        super( restClientBuilder, REGISTRY );
    }
}
