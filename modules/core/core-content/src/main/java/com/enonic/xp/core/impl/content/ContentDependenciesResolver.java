package com.enonic.xp.core.impl.content;

import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.content.*;
import com.enonic.xp.query.aggregation.TermsAggregationQuery;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.schema.content.ContentTypeName;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class ContentDependenciesResolver {

    private final ContentService contentService;

    public ContentDependenciesResolver(final ContentService contentService) {
        this.contentService = contentService;
    }

    public ContentDependencies resolve(final ContentId contentId) {
        final Collection<ContentDependenciesAggregation> inbound = this.resolveInboundDependenciesAggregation(contentId);

        final Collection<ContentDependenciesAggregation> outbound = this.resolveOutboundDependenciesAggregation(contentId);

        return ContentDependencies.create().inboundDependencies(inbound).outboundDependencies(outbound).build();
    }

    private Collection<ContentDependenciesAggregation> resolveInboundDependenciesAggregation(final ContentId contentId) {

        final FindContentIdsByQueryResult result = this.contentService.find(ContentQuery.create().
                queryExpr(QueryParser.parse("_references = '" + contentId.toString() + "' AND _id != '" + contentId.toString() + "'")).
                aggregationQuery(TermsAggregationQuery.create("type").
                        fieldName("type").
                        orderDirection(TermsAggregationQuery.Direction.DESC).
                        build()).
                build());

        final BucketAggregation bucketAggregation = (BucketAggregation) result.getAggregations().get("type");

        return bucketAggregation.getBuckets().getSet().stream().map(bucket -> new ContentDependenciesAggregation(bucket)).collect(
                Collectors.toList());
    }

    private Collection<ContentDependenciesAggregation> resolveOutboundDependenciesAggregation(final ContentId contentId) {

        final Map<ContentTypeName, Long> aggregationJsonMap = Maps.newHashMap();

        final Contents contents = this.contentService.getByIds(
                new GetContentByIdsParams(ContentIds.from(this.contentService.getOutboundDependenciesIds(contentId))));

        contents.forEach(existingContent -> {
            final ContentTypeName contentTypeName = existingContent.getType();
            final Long count = aggregationJsonMap.containsKey(contentTypeName) ? aggregationJsonMap.get(contentTypeName) + 1 : 1;
            aggregationJsonMap.put(contentTypeName, count);
        });

        return aggregationJsonMap.entrySet().
                stream().
                map(entry -> new ContentDependenciesAggregation(entry.getKey(), entry.getValue())).
                collect(Collectors.toList());
    }

}
