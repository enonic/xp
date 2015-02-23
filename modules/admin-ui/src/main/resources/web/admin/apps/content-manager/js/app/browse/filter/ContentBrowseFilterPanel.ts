module app.browse.filter {

    import ContentQueryRequest = api.content.ContentQueryRequest;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import ContentSummaryJson = api.content.json.ContentSummaryJson;
    import ContentQueryResultJson = api.content.json.ContentQueryResultJson;
    import ContentQueryResult = api.content.ContentQueryResult;
    import ContentSummary = api.content.ContentSummary;
    import AggregationTypeWrapperJson = api.aggregation.AggregationTypeWrapperJson;
    import AggregationGroupView = api.aggregation.AggregationGroupView;
    import ContentTypeAggregationGroupView = api.aggregation.ContentTypeAggregationGroupView;
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
    import RefreshEvent = api.app.browse.filter.RefreshEvent;
    import SearchEvent = api.app.browse.filter.SearchEvent;
    import QueryField = api.query.QueryField;


    export class ContentBrowseFilterPanel extends api.app.browse.filter.BrowseFilterPanel {

        static CONTENT_TYPE_AGGREGATION_NAME: string = "contentTypes";
        static LAST_MODIFIED_AGGREGATION_NAME: string = "lastModified";
        static CONTENT_TYPE_AGGREGATION_DISPLAY_NAME: string = "Content Types";
        static LAST_MODIFIED_AGGREGATION_DISPLAY_NAME: string = "Last Modified";

        constructor() {

            var contentTypeAggregation: ContentTypeAggregationGroupView = new ContentTypeAggregationGroupView(
                ContentBrowseFilterPanel.CONTENT_TYPE_AGGREGATION_NAME,
                ContentBrowseFilterPanel.CONTENT_TYPE_AGGREGATION_DISPLAY_NAME);

            var lastModifiedAggregation: AggregationGroupView = new AggregationGroupView(
                ContentBrowseFilterPanel.LAST_MODIFIED_AGGREGATION_NAME,
                ContentBrowseFilterPanel.LAST_MODIFIED_AGGREGATION_DISPLAY_NAME);

            super(null, [contentTypeAggregation, lastModifiedAggregation]);


            this.initAggregationGroupView([contentTypeAggregation, lastModifiedAggregation]);

            this.onReset(()=> {
                this.resetFacets();
            });

            this.onRefresh(this.searchFacets);

            this.onSearch(this.searchFacets);
        }

        private searchFacets(event: api.app.browse.filter.SearchEvent) {

            var isClean = !this.hasFilterSet();
            if (isClean) {
                if (event instanceof RefreshEvent) {
                    this.resetFacets(true, true).then(() => {
                        new ContentBrowseRefreshEvent().fire();
                    }).catch((reason: any) => {
                        api.DefaultErrorHandler.handle(reason);
                    }).done();
                } else { // it's SearchEvent, usual reset with grid reload
                    this.reset();
                }
                return;
            }
            var contentQuery: ContentQuery = new ContentQuery();
            this.appendFulltextSearch(event.getSearchInputValues(), contentQuery);
            this.appendContentTypeFilter(event.getSearchInputValues(), contentQuery);

            var lastModifiedFilter: api.query.filter.Filter = this.appendLastModifiedQuery(event.getSearchInputValues());
            if (lastModifiedFilter != null) {
                contentQuery.addQueryFilter(lastModifiedFilter);
            }

            this.appendContentTypesAggregationQuery(contentQuery);
            this.appendLastModifiedAggregationQuery(contentQuery);

            new ContentQueryRequest<ContentSummaryJson,ContentSummary>(contentQuery).
                setExpand(api.rest.Expand.SUMMARY).
                sendAndParse().then((contentQueryResult: ContentQueryResult<ContentSummary,ContentSummaryJson>) => {
                    var searchStrValue: string = event.getSearchInputValues().getTextSearchFieldValue();
                    if (searchStrValue != null && searchStrValue.length > 0) {
                        this.updateAggregations(contentQueryResult.getAggregations(), true);
                        new ContentBrowseSearchEvent(contentQueryResult, contentQuery).fire();

                    } else {
                        if (event instanceof RefreshEvent) {// refresh without grid reload
                            this.resetFacets(true, true);
                            new ContentBrowseRefreshEvent().fire();
                        } else {// in other cases - reset with grid reload
                            this.updateAggregations(contentQueryResult.getAggregations(), false);
                            new ContentBrowseSearchEvent(contentQueryResult, contentQuery).fire();
                        }
                    }
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).done();
        }

        private initAggregationGroupView(aggregationGroupView: AggregationGroupView[]) {

            var contentQuery: ContentQuery = this.buildAggregationsQuery(new QueryExpr(null));

            new ContentQueryRequest<ContentSummaryJson,ContentSummary>(contentQuery).
                sendAndParse().then((contentQueryResult: ContentQueryResult<ContentSummary,ContentSummaryJson>) => {

                    this.updateAggregations(contentQueryResult.getAggregations(), false);

                    aggregationGroupView.forEach((aggregationGroupView: AggregationGroupView) => {
                        aggregationGroupView.initialize();
                    });
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).done();
        }

        private resetFacets(suppressEvent?: boolean, doResetAll?: boolean) {

            var contentQuery: ContentQuery = this.buildAggregationsQuery(new QueryExpr(null));

            return new ContentQueryRequest<ContentSummaryJson,ContentSummary>(contentQuery).
                sendAndParse().then((contentQueryResult: ContentQueryResult<ContentSummary,ContentSummaryJson>) => {

                    this.updateAggregations(contentQueryResult.getAggregations(), doResetAll);

                    if (!suppressEvent) { // then fire usual reset event with content grid reloading
                        new ContentBrowseResetEvent().fire();
                    }
                }
            ).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                });
        }

        private buildAggregationsQuery(queryExpr: QueryExpr): ContentQuery {
            var contentQuery: ContentQuery = new ContentQuery();
            contentQuery.setQueryExpr(queryExpr);
            contentQuery.setSize(0);
            this.appendContentTypesAggregationQuery(contentQuery);
            this.appendLastModifiedAggregationQuery(contentQuery);

            return contentQuery;
        }

        private appendFulltextSearch(searchInputValues: SearchInputValues, contentQuery: ContentQuery) {

            var searchString: string = searchInputValues.getTextSearchFieldValue();

            var fulltextSearchExpression: api.query.expr.Expression = new api.query.FulltextSearchExpressionBuilder().
                setSearchString(searchString).
                addField(new QueryField(QueryField.DISPLAY_NAME, 5)).
                addField(new QueryField(QueryField.NAME, 3)).
                addField(new QueryField(QueryField.ALL)).
                build();

            var query: QueryExpr = new QueryExpr(fulltextSearchExpression);
            contentQuery.setQueryExpr(query);
        }

        private appendContentTypeFilter(searchInputValues: SearchInputValues, contentQuery: ContentQuery): void {
            var selectedBuckets: api.aggregation.Bucket[] = searchInputValues.getSelectedValuesForAggregationName(ContentBrowseFilterPanel.CONTENT_TYPE_AGGREGATION_NAME);

            var contentTypeNames: ContentTypeName[] = this.parseContentTypeNames(selectedBuckets);

            contentQuery.setContentTypeNames(contentTypeNames);
        }

        private appendLastModifiedQuery(searchInputValues: api.query.SearchInputValues): api.query.filter.Filter {

            var lastModifiedSelectedBuckets: api.aggregation.Bucket[] = searchInputValues.getSelectedValuesForAggregationName(ContentBrowseFilterPanel.LAST_MODIFIED_AGGREGATION_NAME);

            if (lastModifiedSelectedBuckets == null || lastModifiedSelectedBuckets.length == 0) {
                return null;
            }

            if (lastModifiedSelectedBuckets.length == 1) {
                var dateRangeBucket: api.aggregation.DateRangeBucket = <api.aggregation.DateRangeBucket> lastModifiedSelectedBuckets.pop();
                return new api.query.filter.RangeFilter(QueryField.MODIFIED_TIME, ValueExpr.dateTime(dateRangeBucket.getFrom()).getValue(),
                    null);
            }

            var booleanFilter: api.query.filter.BooleanFilter = new api.query.filter.BooleanFilter();

            lastModifiedSelectedBuckets.forEach((selectedBucket: api.aggregation.DateRangeBucket) => {
                var rangeFilter: api.query.filter.RangeFilter =
                    new api.query.filter.RangeFilter(QueryField.MODIFIED_TIME, ValueExpr.dateTime(selectedBucket.getFrom()).getValue(),
                        null);

                booleanFilter.addShould(<api.query.filter.Filter>rangeFilter);
            });

            return booleanFilter;
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

        private appendContentTypesAggregationQuery(contentQuery) {
            contentQuery.addAggregationQuery(this.createTermsAggregation((ContentBrowseFilterPanel.CONTENT_TYPE_AGGREGATION_NAME),
                QueryField.CONTENT_TYPE, 15));
        }

        private createTermsAggregation(name: string, fieldName: string, size: number): TermsAggregationQuery {
            var termsAggregation = new TermsAggregationQuery(name);
            termsAggregation.setFieldName(fieldName);
            termsAggregation.setSize(size);
            return termsAggregation;
        }

        private appendLastModifiedAggregationQuery(contentQuery: ContentQuery) {

            var dateRangeAgg = new DateRangeAggregationQuery((ContentBrowseFilterPanel.LAST_MODIFIED_AGGREGATION_NAME));
            dateRangeAgg.setFieldName(QueryField.MODIFIED_TIME);
            dateRangeAgg.addRange(new DateRange("now-1h", null, "< 1 hour"));
            dateRangeAgg.addRange(new DateRange("now-1d", null, "< 1 day"));
            dateRangeAgg.addRange(new DateRange("now-1w", null, "< 1 week"));

            contentQuery.addAggregationQuery(dateRangeAgg);
        }
    }
}
