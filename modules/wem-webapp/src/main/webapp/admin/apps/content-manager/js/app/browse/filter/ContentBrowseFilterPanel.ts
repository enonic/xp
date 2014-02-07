module app.browse.filter {

    export class ContentBrowseFilterPanel extends api.app.browse.filter.BrowseFilterPanel {

        constructor() {

            var contentTypeAggregation = new api.aggregation.AggregationGroupView("contentTypes");
            //var spaceFacets = new api.facet.FacetGroupView("space", "Space");
            //var lastModifiedFacets = new api.facet.FacetGroupView("lastModified", "Last modified", null, this.lastModifiedGroupFacetFilter);

            super(null, [contentTypeAggregation]);

            this.resetFacets(true);

            this.addListener(
                {
                    onReset: ()=> {
                        this.resetFacets();
                    },

                    onSearch: (values: {[s:string] : string[];
                    })=> {

                        var isClean = !this.hasFilterSet();
                        if (isClean) {
                            this.reset();
                            return;
                        }

                        var contentQuery: api.content.query.ContentQuery = new api.content.query.ContentQuery();

                        var fulltext: string = values['query'] ? values['query'][0] : undefined;

                        var fulltextExpression: api.query.expr.Expression = this.createFulltextSearchExpression(fulltext);
                        var query: api.query.expr.QueryExpr = new api.query.expr.QueryExpr(fulltextExpression);

                        var contentTypeNames: api.schema.content.ContentTypeName[] = this.parseContentTypeNames(values['contentTypes']);
                        contentQuery.setQueryExpr(query);
                        contentQuery.setContentTypeNames(contentTypeNames);

                        var contentTypesAgg: api.query.aggregation.TermsAggregationQuery = this.createTermsAggregation("contentTypes",
                            "contenttype", 10);
                        contentQuery.addAggregationQuery(contentTypesAgg);

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

        private refreshAggregations(aggregationWrapperJsons: api.aggregation.AggregationTypeWrapperJson[]) {
            var aggregations: api.aggregation.Aggregation[] = [];

            aggregationWrapperJsons.forEach((aggregationJson: api.aggregation.AggregationTypeWrapperJson) => {
                aggregations.push(api.aggregation.AggregationFactory.createFromJson(aggregationJson));
                console.log("******* adding facet from result in reset-facets");
            })

            this.updateAggregations(aggregations);
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

        private resetFacets(supressEvent?: boolean) {
            var queryExpr: api.query.expr.QueryExpr = new api.query.expr.QueryExpr(null);

            var contentQuery: api.content.query.ContentQuery = new api.content.query.ContentQuery();
            contentQuery.setQueryExpr(queryExpr);
            contentQuery.setSize(0);

            var contentTypesAgg: api.query.aggregation.TermsAggregationQuery = this.createTermsAggregation("contentTypes", "contenttype",
                10);
            contentQuery.addAggregationQuery(contentTypesAgg);

            new api.content.ContentQueryRequest<api.content.ContentQueryResult<api.content.json.ContentSummaryJson>>(contentQuery).
                send().done((jsonResponse: api.rest.JsonResponse<api.content.ContentQueryResult<api.content.json.ContentSummaryJson>>) => {

                    var result = jsonResponse.getResult();
                    new ContentBrowseSearchEvent(result.contents).fire();

                    this.refreshAggregations(result.aggregations);

                    if (!supressEvent) {
                        new ContentBrowseResetEvent().fire();
                    }
                }
            );
        }

        private createTermsAggregation(name: string, fieldName: string, size: number): api.query.aggregation.TermsAggregationQuery {
            var termsAggregation: api.query.aggregation.TermsAggregationQuery = new api.query.aggregation.TermsAggregationQuery(name);
            termsAggregation.setFieldName(fieldName);
            termsAggregation.setSize(size);
            return termsAggregation;
        }


        private extractRangesFromFilterValues(values: { [s : string ] : string[];
        }): {lower:Date; upper:Date
        }[] {
            var ranges = [];

            if (values) {
                var now = new Date();
                var oneDayAgo = new Date();
                var oneWeekAgo = new Date();
                var oneHourAgo = new Date();
                oneDayAgo.setDate(now.getDate() - 1);
                oneWeekAgo.setDate(now.getDate() - 7);
                Admin.lib.DateHelper.addHours(oneHourAgo, -1);

                for (var prop in values) {
                    if (values.hasOwnProperty(prop) && values[prop].length > 0) {
                        var lower = null;

                        switch (values[prop][0]) {
                        case '< 1 day':
                            lower = oneDayAgo;
                            break;
                        case '< 1 hour':
                            lower = oneHourAgo;
                            break;
                        case '1 < week':
                            lower = oneWeekAgo;
                            break;
                        }

                        if (lower) {
                            ranges.push({
                                lower: lower,
                                upper: null
                            })
                        }
                    }
                }
            }
            return ranges;
        }

        private lastModifiedGroupFacetFilter(facet: api.facet.Facet) {
            return facet instanceof api.facet.QueryFacet;
        }
    }

}
