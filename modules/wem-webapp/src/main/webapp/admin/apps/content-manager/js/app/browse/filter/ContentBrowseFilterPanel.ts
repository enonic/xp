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
    import DynamicConstraintExpr = api.query.expr.DynamicConstraintExpr;
    import Value = api.data.Value;
    import ValueTypes = api.data.ValueTypes;

    export class ContentBrowseFilterPanel extends api.app.browse.filter.BrowseFilterPanel {

        constructor() {

            var contentTypeAggregation = new AggregationGroupView("contentTypes");
            var lastModifiedAggregation = new AggregationGroupView("lastModified");
            //var spaceFacets = new api.facet.FacetGroupView("space", "Space");
            //var lastModifiedFacets = new api.facet.FacetGroupView("lastModified", "Last modified", null, this.lastModifiedGroupFacetFilter);

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
            var fulltextExpression: api.query.expr.Expression = this.createFulltextSearchExpression(fulltext);
            var query: QueryExpr = new QueryExpr(fulltextExpression);
            contentQuery.setQueryExpr(query);
        }

        private createFulltextSearchExpression(fulltext: string): api.query.expr.Expression {

            if (fulltext == null) {
                return null;
            }

            var arguments: ValueExpr[] = [];

            arguments.push(new ValueExpr(new Value("_all_text", ValueTypes.STRING)));
            arguments.push(new ValueExpr(new Value(fulltext, ValueTypes.STRING)));
            arguments.push(new ValueExpr(new Value("AND", ValueTypes.STRING)));

            var fulltextExp: FunctionExpr = new FunctionExpr("fulltext", arguments);
            var fulltextDynamicExpr: DynamicConstraintExpr = new DynamicConstraintExpr(fulltextExp);

            var nGramExpr: FunctionExpr = new FunctionExpr("ngram", arguments);
            var nGramDynamicExpr: DynamicConstraintExpr = new DynamicConstraintExpr(nGramExpr);

            var booleanExpr: api.query.expr.LogicalExpr =
                new api.query.expr.LogicalExpr(fulltextDynamicExpr, LogicalOperator.OR, nGramDynamicExpr);
            return booleanExpr;
        }

        private appendContentTypeFilter(searchInputValues: SearchInputValues, contentQuery: ContentQuery): void {

            var contentTypesSelections = searchInputValues.getSelectedValuesForAggregationName("contentTypes");

            var contentTypeNames: ContentTypeName[] = this.parseContentTypeNames(contentTypesSelections);

            contentQuery.setContentTypeNames(contentTypeNames);
        }

        private appendLastModifiedQuery(searchInputValues: SearchInputValues): void {

            var value = searchInputValues.getSelectedValuesForAggregationName("lastModified");


        }

        private parseContentTypeNames(names: string[]): ContentTypeName[] {
            var contentTypeNames: ContentTypeName[] = [];

            if (names) {
                for (var i = 0; i < names.length; i++) {
                    var name = names[i];
                    contentTypeNames.push(new ContentTypeName(name));
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
