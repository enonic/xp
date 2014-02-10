module app.browse.filter {

    export class ContentBrowseFilterPanel extends api.app.browse.filter.BrowseFilterPanel {

        constructor() {

            var contentTypeAggregation = new api.aggregation.AggregationGroupView("contentTypes");
            var lastModifiedAggregation = new api.aggregation.AggregationGroupView("lastModified");
            //var spaceFacets = new api.facet.FacetGroupView("space", "Space");
            //var lastModifiedFacets = new api.facet.FacetGroupView("lastModified", "Last modified", null, this.lastModifiedGroupFacetFilter);

            super(null, [contentTypeAggregation, lastModifiedAggregation]);

            this.resetFacets(true);

            this.addListener(
                {
                    onReset: ()=> {
                        this.resetFacets();
                    },

                    onSearch: (searchInputValues: api.query.SearchInputValues)=> {

                        var isClean = !this.hasFilterSet();
                        if (isClean) {
                            this.reset();
                            return;
                        }

                        var contentQuery: api.content.query.ContentQuery = new api.content.query.ContentQuery();
                        this.appendFulltextSearch(searchInputValues, contentQuery);
                        this.appendContentTypeFilter(searchInputValues, contentQuery);
                        this.appendContentTypesAggregation(contentQuery);
                        this.appendLastModifiedAggregation(contentQuery);

                        new api.content.ContentQueryRequest<api.content.ContentQueryResult<api.content.json.ContentSummaryJson>>(contentQuery).
                            setExpand(api.rest.Expand.SUMMARY).
                            send().done((jsonResponse: api.rest.JsonResponse<api.content.ContentQueryResult<api.content.json.ContentSummaryJson>>) => {
                                var result = jsonResponse.getResult();

                                this.refreshAggregations(result.aggregations);

                                new ContentBrowseSearchEvent(result.contents).fire();
                            })
                        ;
                    }
                }
            );
        }

        private resetFacets(supressEvent?: boolean) {
            var queryExpr: api.query.expr.QueryExpr = new api.query.expr.QueryExpr(null);

            var contentQuery: api.content.query.ContentQuery = new api.content.query.ContentQuery();
            contentQuery.setQueryExpr(queryExpr);
            contentQuery.setSize(0);
            this.appendContentTypesAggregation(contentQuery);
            this.appendLastModifiedAggregation(contentQuery);

            new api.content.ContentQueryRequest<api.content.ContentQueryResult<api.content.json.ContentSummaryJson>>(contentQuery).
                send().done((jsonResponse: api.rest.JsonResponse<api.content.ContentQueryResult<api.content.json.ContentSummaryJson>>) => {

                    var result: api.content.ContentQueryResult<api.content.json.ContentSummaryJson> = jsonResponse.getResult();
                    new ContentBrowseSearchEvent(result.contents).fire();

                    this.refreshAggregations(result.aggregations);

                    if (!supressEvent) {
                        new ContentBrowseResetEvent().fire();
                    }
                }
            );
        }

        private refreshAggregations(aggregationWrapperJsons: api.aggregation.AggregationTypeWrapperJson[]) {
            var aggregations: api.aggregation.Aggregation[] = [];

            aggregationWrapperJsons.forEach((aggregationJson: api.aggregation.AggregationTypeWrapperJson) => {
                aggregations.push(api.aggregation.AggregationFactory.createFromJson(aggregationJson));
            })

            this.updateAggregations(aggregations);
        }

        private appendFulltextSearch(searchInputValues: api.query.SearchInputValues, contentQuery: api.content.query.ContentQuery) {

            var fulltext: string = searchInputValues.getTextSearchFieldValue();
            var fulltextExpression: api.query.expr.Expression = this.createFulltextSearchExpression(fulltext);
            var query: api.query.expr.QueryExpr = new api.query.expr.QueryExpr(fulltextExpression);
            contentQuery.setQueryExpr(query);
        }

        private createFulltextSearchExpression(fulltext: string): api.query.expr.Expression {

            if (fulltext == null) {
                return null;
            }

            var arguments: api.query.expr.ValueExpr[] = [];

            arguments.push(new api.query.expr.ValueExpr(new api.data.Value("_all_text", api.data.ValueTypes.STRING)));
            arguments.push(new api.query.expr.ValueExpr(new api.data.Value(fulltext, api.data.ValueTypes.STRING)));
            arguments.push(new api.query.expr.ValueExpr(new api.data.Value("AND", api.data.ValueTypes.STRING)));

            var fulltextExp: api.query.expr.FunctionExpr = new api.query.expr.FunctionExpr("fulltext", arguments);
            var fulltextDynamicExpr: api.query.expr.DynamicConstraintExpr = new api.query.expr.DynamicConstraintExpr(fulltextExp);

            var nGramExpr: api.query.expr.FunctionExpr = new api.query.expr.FunctionExpr("ngram", arguments);
            var nGramDynamicExpr: api.query.expr.DynamicConstraintExpr = new api.query.expr.DynamicConstraintExpr(nGramExpr);

            var booleanExpr: api.query.expr.LogicalExpr =
                new api.query.expr.LogicalExpr(fulltextDynamicExpr, api.query.expr.LogicalOperator.OR, nGramDynamicExpr);
            return booleanExpr;
        }

        private appendContentTypeFilter(searchInputValues: api.query.SearchInputValues,
                                        contentQuery: api.content.query.ContentQuery): void {

            var contentTypesSelections = searchInputValues.getSelectedValuesForAggregationName("contentTypes");

            var contentTypeNames: api.schema.content.ContentTypeName[] = this.parseContentTypeNames(contentTypesSelections);

            contentQuery.setContentTypeNames(contentTypeNames);
        }

        private appendLastModifiedQuery(searchInputValues: api.query.SearchInputValues): void {

            var value = searchInputValues.getSelectedValuesForAggregationName("lastModified");


        }

        private parseContentTypeNames(names: string[]): api.schema.content.ContentTypeName[] {
            var contentTypeNames: api.schema.content.ContentTypeName[] = [];

            if (names) {
                for (var i = 0; i < names.length; i++) {
                    var name = names[i];
                    contentTypeNames.push(new api.schema.content.ContentTypeName(name));
                }
            }

            return contentTypeNames;
        }

        private appendContentTypesAggregation(contentQuery) {
            contentQuery.addAggregationQuery(this.createTermsAggregation("contentTypes", "contenttype", 10));
        }

        private createTermsAggregation(name: string, fieldName: string, size: number): api.query.aggregation.TermsAggregationQuery {
            var termsAggregation: api.query.aggregation.TermsAggregationQuery = new api.query.aggregation.TermsAggregationQuery(name);
            termsAggregation.setFieldName(fieldName);
            termsAggregation.setSize(size);
            return termsAggregation;
        }

        private appendLastModifiedAggregation(contentQuery: api.content.query.ContentQuery) {

            var dateRangeAgg: api.query.aggregation.DateRangeAggregationQuery = new api.query.aggregation.DateRangeAggregationQuery("lastModified");
            dateRangeAgg.setFieldName("modifiedTime");
            dateRangeAgg.addRange(new api.query.aggregation.DateRange("now-1h", null, "< 1 hour"));
            dateRangeAgg.addRange(new api.query.aggregation.DateRange("now-1d", null, "< 1 day"));
            dateRangeAgg.addRange(new api.query.aggregation.DateRange("now-1w", null, "< 1 week"));

            contentQuery.addAggregationQuery(dateRangeAgg);
        }

    }

}
