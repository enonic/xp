module app.browse.filter {

    import ContentQueryRequest = api.content.ContentQueryRequest;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import ContentSummaryJson = api.content.json.ContentSummaryJson;
    import ContentQueryResultJson = api.content.json.ContentQueryResultJson;
    import ContentQueryResult = api.content.ContentQueryResult;
    import ContentSummary = api.content.ContentSummary;
    import AggregationTypeWrapperJson = api.aggregation.AggregationTypeWrapperJson;
    import AggregationGroupView = api.aggregation.AggregationGroupView;
    import Aggregation = api.aggregation.Aggregation;
    import AggregationFactory = api.aggregation.AggregationFactory;
    import SearchInputValues = api.query.SearchInputValues;
    import ContentQuery = api.content.query.ContentQuery;
    import TermsAggregationQuery = api.query.aggregation.TermsAggregationQuery;
    import DateRangeAggregationQuery = api.query.aggregation.DateRangeAggregationQuery;
    import DateRange = api.query.aggregation.DateRange;
    import QueryExpr = api.query.expr.QueryExpr;
    import ValueExpr = api.query.expr.ValueExpr;
    import FunctionExpr = api.query.expr.FunctionExpr;
    import LogicalOperator = api.query.expr.LogicalOperator;
    import CompareExpr = api.query.expr.CompareExpr;
    import LogicalExp = api.query.expr.LogicalExpr;
    import DynamicConstraintExpr = api.query.expr.DynamicConstraintExpr;
    import Value = api.data.Value;
    import ValueTypes = api.data.ValueTypes;


    export class ContentBrowseFilterPanel extends api.app.browse.filter.BrowseFilterPanel {

        static CONTENT_TYPE_AGGREGATION_DISPLAY_NAME: string = "Content Types"
        static LAST_MODIFIED_AGGREGATION_DISPLAY_NAME: string = "Last Modified"

        constructor() {

            var contentTypeAggregation = new AggregationGroupView("contentTypes",
                ContentBrowseFilterPanel.CONTENT_TYPE_AGGREGATION_DISPLAY_NAME);
            var lastModifiedAggregation = new AggregationGroupView("lastModified",
                ContentBrowseFilterPanel.LAST_MODIFIED_AGGREGATION_DISPLAY_NAME);

            super(null, [contentTypeAggregation, lastModifiedAggregation]);

            this.resetFacets(true);

            this.addListener(
                {
                    onReset: ()=> {
                        this.resetFacets();
                    },

                    onSearch: (searchInputValues: SearchInputValues)=> {

                        var isClean = !this.hasFilterSet();
                        if (isClean) {
                            this.reset();
                            return;
                        }

                        var contentQuery: ContentQuery = new ContentQuery();
                        this.appendFulltextSearch(searchInputValues, contentQuery);
                        this.appendContentTypeFilter(searchInputValues, contentQuery);

                        var lastModifiedFilter: api.query.filter.Filter = this.appendLastModifiedQuery(searchInputValues);
                        if (lastModifiedFilter != null) {
                            contentQuery.addQueryFilter(lastModifiedFilter);
                        }

                        this.appendContentTypesAggregation(contentQuery);
                        this.appendLastModifiedAggregation(contentQuery);

                        new ContentQueryRequest<ContentSummaryJson,ContentSummary>(contentQuery).
                            setExpand(api.rest.Expand.SUMMARY).
                            send().done((jsonResponse: api.rest.JsonResponse<ContentQueryResultJson<ContentSummaryJson>>) => {

                                var result: ContentQueryResultJson<ContentSummaryJson> = jsonResponse.getResult();

                                this.updateAggregations(Aggregation.fromJsonArray(result.aggregations));

                                new ContentBrowseSearchEvent(result.contents).fire();
                            });
                    }
                }
            );
        }

        private resetFacets(supressEvent?: boolean) {
            var queryExpr: QueryExpr = new QueryExpr(null);

            var contentQuery: ContentQuery = new ContentQuery();
            contentQuery.setQueryExpr(queryExpr);
            contentQuery.setSize(0);
            this.appendContentTypesAggregation(contentQuery);
            this.appendLastModifiedAggregation(contentQuery);

            new ContentQueryRequest<ContentSummaryJson,ContentSummary>(contentQuery).
                send().done((jsonResponse: api.rest.JsonResponse<ContentQueryResultJson<ContentSummaryJson>>) => {

                    var result: ContentQueryResultJson<ContentSummaryJson> = jsonResponse.getResult();
                    new ContentBrowseSearchEvent(result.contents).fire();

                    this.updateAggregations(Aggregation.fromJsonArray(result.aggregations));


                    if (!supressEvent) {
                        new ContentBrowseResetEvent().fire();
                    }
                }
            );
        }

        private appendFulltextSearch(searchInputValues: SearchInputValues, contentQuery: ContentQuery) {

            var fulltext: string = searchInputValues.getTextSearchFieldValue();
            var fulltextExpression: api.query.expr.Expression = api.query.FulltextFunctionFactory.create(fulltext);
            var query: QueryExpr = new QueryExpr(fulltextExpression);
            contentQuery.setQueryExpr(query);
        }

        private appendContentTypeFilter(searchInputValues: SearchInputValues, contentQuery: ContentQuery): void {

            var selectedBuckets: api.aggregation.Bucket[] = searchInputValues.getSelectedValuesForAggregationName("contentTypes");

            var contentTypeNames: ContentTypeName[] = this.parseContentTypeNames(selectedBuckets);

            contentQuery.setContentTypeNames(contentTypeNames);
        }

        private appendLastModifiedQuery(searchInputValues: api.query.SearchInputValues): api.query.filter.Filter {

            var lastModifiedSelectedBuckets: api.aggregation.Bucket[] = searchInputValues.getSelectedValuesForAggregationName("lastModified");

            if (lastModifiedSelectedBuckets == null || lastModifiedSelectedBuckets.length == 0) {
                return null;
            }


            var dateRangeBucket: api.aggregation.DateRangeBucket = <api.aggregation.DateRangeBucket> lastModifiedSelectedBuckets.pop();

            // FIX FIX FIX

            var rangeFilter: api.query.filter.RangeFilter = new api.query.filter.RangeFilter("modifiedtime",
                ValueExpr.dateTime(dateRangeBucket.getFrom()).getValue(), null);

            return rangeFilter;

        }

        private parseContentTypeNames(buckets: api.aggregation.Bucket[]): ContentTypeName[] {
            var contentTypeNames: ContentTypeName[] = [];

            if (buckets) {
                for (var i = 0; i < buckets.length; i++) {
                    var bucket: api.aggregation.Bucket = buckets[i];
                    contentTypeNames.push(new ContentTypeName(bucket.getKey()));
                }
            }

            return contentTypeNames;
        }

        private appendContentTypesAggregation(contentQuery) {
            contentQuery.addAggregationQuery(this.createTermsAggregation("contentTypes", "contenttype", 10));
        }

        private createTermsAggregation(name: string, fieldName: string, size: number): TermsAggregationQuery {
            var termsAggregation = new TermsAggregationQuery(name);
            termsAggregation.setFieldName(fieldName);
            termsAggregation.setSize(size);
            return termsAggregation;
        }

        private appendLastModifiedAggregation(contentQuery: ContentQuery) {

            var dateRangeAgg = new DateRangeAggregationQuery("lastModified");
            dateRangeAgg.setFieldName("modifiedTime");
            dateRangeAgg.addRange(new DateRange("now-1h", null, "< 1 hour"));
            dateRangeAgg.addRange(new DateRange("now-1d", null, "< 1 day"));
            dateRangeAgg.addRange(new DateRange("now-1w", null, "< 1 week"));

            contentQuery.addAggregationQuery(dateRangeAgg);
        }
    }


}
