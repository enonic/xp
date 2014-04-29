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

        static CONTENT_TYPE_AGGREGATION_NAME: string = "contentTypes";
        static LAST_MODIFIED_AGGREGATION_NAME: string = "lastModified";
        static CONTENT_TYPE_AGGREGATION_DISPLAY_NAME: string = "Content Types";
        static LAST_MODIFIED_AGGREGATION_DISPLAY_NAME: string = "Last Modified";

        constructor() {

            var contentTypeAggregation: AggregationGroupView = new AggregationGroupView(
                ContentBrowseFilterPanel.CONTENT_TYPE_AGGREGATION_NAME,
                ContentBrowseFilterPanel.CONTENT_TYPE_AGGREGATION_DISPLAY_NAME);
            this.loadAndSetContentTypeDisplayNames(contentTypeAggregation);


            var lastModifiedAggregation: AggregationGroupView = new AggregationGroupView(
                ContentBrowseFilterPanel.LAST_MODIFIED_AGGREGATION_NAME,
                ContentBrowseFilterPanel.LAST_MODIFIED_AGGREGATION_DISPLAY_NAME);

            super(null, [contentTypeAggregation, lastModifiedAggregation]);

            this.resetFacets(true);

            this.onReset(()=> {
                this.resetFacets();
            });

            this.onSearch(this.searchFacets);

            api.content.ContentDeletedEvent.on(() => {
                this.search();
            });
        }

        private loadAndSetContentTypeDisplayNames(aggregationGroupView: AggregationGroupView) {

            var displayNameMap: string[] = [];

            var request = new api.schema.content.GetAllContentTypesRequest();
            request.sendAndParse().done((contentTypes: api.schema.content.ContentTypeSummary[]) => {

                contentTypes.forEach((contentType: api.schema.content.ContentTypeSummary)=> {
                    displayNameMap[contentType.getName()] = contentType.getDisplayName();
                });

                aggregationGroupView.getAggregationViews().forEach((aggregationView: api.aggregation.AggregationView)=> {
                    if (aggregationView.getName() == ContentBrowseFilterPanel.CONTENT_TYPE_AGGREGATION_NAME) {
                        aggregationView.setDisplayNamesMap(displayNameMap);
                    }
                });

            });
        }

        private searchFacets(event: api.app.browse.filter.SearchEvent) {

            var isClean = !this.hasFilterSet();
            if (isClean) {
                this.reset();
                return;
            }

            var contentQuery: ContentQuery = new ContentQuery();
            this.appendFulltextSearch(event.getSearchInputValues(), contentQuery);
            this.appendContentTypeFilter(event.getSearchInputValues(), contentQuery);

            var lastModifiedFilter: api.query.filter.Filter = this.appendLastModifiedQuery(event.getSearchInputValues());
            if (lastModifiedFilter != null) {
                contentQuery.addQueryFilter(lastModifiedFilter);
            }

            this.appendContentTypesAggregation(contentQuery);
            this.appendLastModifiedAggregation(contentQuery);

            new ContentQueryRequest<ContentSummaryJson,ContentSummary>(contentQuery).
                setExpand(api.rest.Expand.SUMMARY).
                sendAndParse().done((contentQueryResult: ContentQueryResult<ContentSummary,ContentSummaryJson>) => {

                    var doUpdateAll = true;
                    if (event.getElementChanged() instanceof api.aggregation.BucketView) {
                        doUpdateAll = false;
                    }

                    this.updateAggregations(contentQueryResult.getAggregations(), doUpdateAll);

                    new ContentBrowseSearchEvent(contentQueryResult.getContentsAsJson()).fire();
                });
        }

        private resetFacets(supressEvent?: boolean) {
            var queryExpr: QueryExpr = new QueryExpr(null);

            var contentQuery: ContentQuery = new ContentQuery();
            contentQuery.setQueryExpr(queryExpr);
            contentQuery.setSize(0);
            this.appendContentTypesAggregation(contentQuery);
            this.appendLastModifiedAggregation(contentQuery);

            new ContentQueryRequest<ContentSummaryJson,ContentSummary>(contentQuery).
                sendAndParse().done((contentQueryResult: ContentQueryResult<ContentSummary,ContentSummaryJson>) => {

                    this.updateAggregations(contentQueryResult.getAggregations());

                    if (!supressEvent) {
                        new ContentBrowseResetEvent().fire();
                    }
                }
            );
        }

        private appendFulltextSearch(searchInputValues: SearchInputValues, contentQuery: ContentQuery) {

            var searchString: string = searchInputValues.getTextSearchFieldValue();
            var fulltextSearchExpression: api.query.expr.Expression = api.query.FulltextSearchExpressionFactory.create(searchString);
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
                return new api.query.filter.RangeFilter("modifiedtime", ValueExpr.dateTime(dateRangeBucket.getFrom()).getValue(), null);
            }

            var booleanFilter: api.query.filter.BooleanFilter = new api.query.filter.BooleanFilter();

            lastModifiedSelectedBuckets.forEach((selectedBucket: api.aggregation.DateRangeBucket) => {
                var rangeFilter: api.query.filter.RangeFilter =
                    new api.query.filter.RangeFilter("modifiedtime", ValueExpr.dateTime(selectedBucket.getFrom()).getValue(), null);

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

        private appendContentTypesAggregation(contentQuery) {
            contentQuery.addAggregationQuery(this.createTermsAggregation((ContentBrowseFilterPanel.CONTENT_TYPE_AGGREGATION_NAME),
                "contenttype", 10));
        }

        private createTermsAggregation(name: string, fieldName: string, size: number): TermsAggregationQuery {
            var termsAggregation = new TermsAggregationQuery(name);
            termsAggregation.setFieldName(fieldName);
            termsAggregation.setSize(size);
            return termsAggregation;
        }

        private appendLastModifiedAggregation(contentQuery: ContentQuery) {

            var dateRangeAgg = new DateRangeAggregationQuery((ContentBrowseFilterPanel.LAST_MODIFIED_AGGREGATION_NAME));
            dateRangeAgg.setFieldName("modifiedTime");
            dateRangeAgg.addRange(new DateRange("now-1h", null, "< 1 hour"));
            dateRangeAgg.addRange(new DateRange("now-1d", null, "< 1 day"));
            dateRangeAgg.addRange(new DateRange("now-1w", null, "< 1 week"));

            contentQuery.addAggregationQuery(dateRangeAgg);
        }
    }
}
