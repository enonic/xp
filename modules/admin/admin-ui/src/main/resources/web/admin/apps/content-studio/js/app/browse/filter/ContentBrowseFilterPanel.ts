module app.browse.filter {

    import ContentQueryRequest = api.content.ContentQueryRequest;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import ContentSummaryJson = api.content.json.ContentSummaryJson;
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

        contentTypeAggregation: ContentTypeAggregationGroupView;
        lastModifiedAggregation: AggregationGroupView;

        constructor() {

            this.contentTypeAggregation = new ContentTypeAggregationGroupView(
                ContentBrowseFilterPanel.CONTENT_TYPE_AGGREGATION_NAME,
                ContentBrowseFilterPanel.CONTENT_TYPE_AGGREGATION_DISPLAY_NAME);

            this.lastModifiedAggregation = new AggregationGroupView(
                ContentBrowseFilterPanel.LAST_MODIFIED_AGGREGATION_NAME,
                ContentBrowseFilterPanel.LAST_MODIFIED_AGGREGATION_DISPLAY_NAME);

            super(null, [this.contentTypeAggregation, this.lastModifiedAggregation]);


            this.initAggregationGroupView([this.contentTypeAggregation, this.lastModifiedAggregation]);

            this.onReset(()=> {
                this.resetFacets();
            });

            this.onRefresh(this.refreshFacets);

            this.onSearch(this.searchFacets);
        }

        private searchFacets(event: api.app.browse.filter.SearchEvent) {
            if (!this.hasFilterSet()) {
                this.handleEmptyFilterInput();
                return;
            }

            this.searchDataAndHandleResponse(this.createContentQuery(event));
        }

        private refreshFacets(event: api.app.browse.filter.SearchEvent) {
            if (!this.hasFilterSet()) {
                this.handleEmptyFilterInput(true);
                return;
            }

            this.refreshDataAndHandleResponse(this.createContentQuery(event));
        }

        private handleEmptyFilterInput(isRefreshEvent?: boolean) {
            if (isRefreshEvent) {

                this.resetFacets(true, true).then(() => {
                    new ContentBrowseRefreshEvent().fire();
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).done();

            } else { // it's SearchEvent, usual reset with grid reload
                this.reset();
            }
        }

        private createContentQuery(event: api.app.browse.filter.SearchEvent): ContentQuery {
            var contentQuery: ContentQuery = new ContentQuery();
            this.appendFulltextSearch(event.getSearchInputValues(), contentQuery);
            this.appendContentTypeFilter(event.getSearchInputValues(), contentQuery);

            var lastModifiedFilter: api.query.filter.Filter = this.appendLastModifiedQuery(event.getSearchInputValues());
            if (lastModifiedFilter != null) {
                contentQuery.addQueryFilter(lastModifiedFilter);
            }

            contentQuery.setSize(ContentQuery.POSTLOAD_SIZE);

            this.appendContentTypesAggregationQuery(contentQuery);
            this.appendLastModifiedAggregationQuery(contentQuery);

            return contentQuery;
        }

        private searchDataAndHandleResponse(contentQuery: ContentQuery) {
            new ContentQueryRequest<ContentSummaryJson,ContentSummary>(contentQuery).
                setExpand(api.rest.Expand.SUMMARY).
                sendAndParse().then((contentQueryResult: ContentQueryResult<ContentSummary,ContentSummaryJson>) => {
                    this.handleDataSearchResult(contentQuery, contentQueryResult);
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).done();
        }

        private refreshDataAndHandleResponse(contentQuery: ContentQuery) {
            new ContentQueryRequest<ContentSummaryJson,ContentSummary>(contentQuery).
            setExpand(api.rest.Expand.SUMMARY).
            sendAndParse().then((contentQueryResult: ContentQueryResult<ContentSummary,ContentSummaryJson>) => {
                if(contentQueryResult.getMetadata().getTotalHits() > 0) {
                    this.handleDataSearchResult(contentQuery, contentQueryResult);
                }
                else {
                    this.handleNoSearchResultOnRefresh(contentQuery);
                }

            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();
        }

        private handleDataSearchResult(contentQuery: ContentQuery,
                                       contentQueryResult: ContentQueryResult<ContentSummary,ContentSummaryJson>) {
            this.getAggregations(contentQuery, contentQueryResult).then((aggregations: api.aggregation.Aggregation[]) => {
                this.updateAggregations(aggregations, true);
                this.updateHitsCounter(contentQueryResult.getMetadata().getTotalHits());
                this.toggleAggregationsVisibility(contentQueryResult.getAggregations());
                new ContentBrowseSearchEvent(contentQueryResult, contentQuery).fire();
            });
        }

        private handleNoSearchResultOnRefresh(contentQuery: ContentQuery) {
            if(contentQuery.getContentTypes().length > 0 ) { //remove content type facet from search
                this.refreshDataAndHandleResponse(this.cloneContentQueryNoContentTypes(contentQuery));
            }
            else if(this.hasSearchStringSet()) { // if still no result and search text is set remove last modified facet
                this.deselectAll();
                this.searchDataAndHandleResponse(this.cloneContentQueryNoAggregations(contentQuery));
            }
            else {
                this.reset();
            }
        }

        private cloneContentQueryNoContentTypes(contentQuery: ContentQuery): ContentQuery {
            var newContentQuery: ContentQuery = new ContentQuery().
                setContentTypeNames([]).
                setFrom(contentQuery.getFrom()).
                setQueryExpr(contentQuery.getQueryExpr()).
                setSize(contentQuery.getSize()).
                setAggregationQueries(contentQuery.getAggregationQueries()).
                setQueryFilters(contentQuery.getQueryFilters());

            return newContentQuery;
        }

        private cloneContentQueryNoAggregations(contentQuery: ContentQuery): ContentQuery {
            return this.cloneContentQueryNoContentTypes(contentQuery).setQueryFilters([]);
        }

        private getAggregations(contentQuery: ContentQuery,
                                contentQueryResult: ContentQueryResult<ContentSummary,ContentSummaryJson>): wemQ.Promise<api.aggregation.Aggregation[]> {
            return new ContentQueryRequest<ContentSummaryJson,ContentSummary>(this.cloneContentQueryNoContentTypes(contentQuery)).
                setExpand(api.rest.Expand.SUMMARY).
                sendAndParse().then((contentQueryResultNoContentTypesSelected: ContentQueryResult<ContentSummary,ContentSummaryJson>) => {
                    return this.combineAggregations(contentQueryResult, contentQueryResultNoContentTypesSelected);
                });
        }

        private combineAggregations(contentQueryResult, contentQueryResultNoContentTypesSelected): api.aggregation.Aggregation[] {
            var contentTypesAggr = contentQueryResultNoContentTypesSelected.getAggregations().filter((aggregation) => {
                return aggregation.getName() === ContentBrowseFilterPanel.CONTENT_TYPE_AGGREGATION_NAME;
            });
            var dateModifiedAggr = contentQueryResult.getAggregations().filter((aggregation) => {
                return aggregation.getName() !== ContentBrowseFilterPanel.CONTENT_TYPE_AGGREGATION_NAME;
            });

            var aggregations = [contentTypesAggr[0], dateModifiedAggr[0]];

            return aggregations;
        }

        private initAggregationGroupView(aggregationGroupView: AggregationGroupView[]) {

            var contentQuery: ContentQuery = this.buildAggregationsQuery(new QueryExpr(null));

            new ContentQueryRequest<ContentSummaryJson,ContentSummary>(contentQuery).
                sendAndParse().then((contentQueryResult: ContentQueryResult<ContentSummary,ContentSummaryJson>) => {

                    this.updateAggregations(contentQueryResult.getAggregations(), false);
                    this.updateHitsCounter(contentQueryResult.getMetadata().getTotalHits(), true);
                    this.toggleAggregationsVisibility(contentQueryResult.getAggregations());

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
                    this.updateHitsCounter(contentQueryResult.getMetadata().getTotalHits(), true);
                    this.toggleAggregationsVisibility(contentQueryResult.getAggregations());

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
                QueryField.CONTENT_TYPE, 30));
        }

        private createTermsAggregation(name: string, fieldName: string, size: number): TermsAggregationQuery {
            var termsAggregation = new TermsAggregationQuery(name);
            termsAggregation.setFieldName(fieldName);
            termsAggregation.setSize(size);
            termsAggregation.setOrderByType(api.query.aggregation.TermsAggregationOrderType.DOC_COUNT);
            termsAggregation.setOrderByDirection(api.query.aggregation.TermsAggregationOrderDirection.DESC);
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

        private toggleAggregationsVisibility(aggregations: api.aggregation.Aggregation[]) {
            aggregations.forEach((aggregation: api.aggregation.BucketAggregation) => {
                var aggregationIsEmpty = !aggregation.getBuckets().some((bucket: api.aggregation.Bucket) => {
                    if (bucket.docCount > 0) {
                        return true;
                    }
                })

                var aggregationGroupView = aggregation.getName() == ContentBrowseFilterPanel.CONTENT_TYPE_AGGREGATION_NAME
                    ? this.contentTypeAggregation
                    : this.lastModifiedAggregation;

                if (aggregationIsEmpty) {
                    aggregationGroupView.hide();
                }
                else {
                    aggregationGroupView.show();
                }
            })
        }
    }
}
