module api.content.form.inputtype.tag {

    import ContentQuery = api.content.query.ContentQuery;
    import ContentQueryRequest = api.content.ContentQueryRequest;
    import QueryExpr = api.query.expr.QueryExpr;
    import CompareExpr = api.query.expr.CompareExpr;
    import FieldExpr = api.query.expr.FieldExpr;
    import CompareOperator = api.query.expr.CompareOperator;
    import ValueExpr = api.query.expr.ValueExpr;
    import DataPath = api.data.DataPath;
    import Value = api.data.Value;
    import ValueTypes = api.data.type.ValueTypes;

    export class ContentTagSuggesterBuilder {

        dataPath: DataPath;

        setDataPath(value: DataPath): ContentTagSuggesterBuilder {
            this.dataPath = value;
            return this;
        }

        build(): ContentTagSuggester {
            return new ContentTagSuggester(this);
        }
    }

    export class ContentTagSuggester implements api.ui.tags.TagSuggester {

        private dataPath: DataPath;

        constructor(builder: ContentTagSuggesterBuilder) {
            this.dataPath = builder.dataPath;
        }

        suggest(value: string): wemQ.Promise<string[]> {

            var fieldName = 'contentdata' + this.dataPath.getParentPath().toString() + this.dataPath.getLastElement().getName();

            var field: api.query.expr.ValueExpr = new api.query.expr.ValueExpr(new api.data.Value(fieldName,
                api.data.type.ValueTypes.STRING));

            var arguments: api.query.expr.ValueExpr[] = [];
            arguments.push(field);
            arguments.push(new api.query.expr.ValueExpr(new api.data.Value(value, api.data.type.ValueTypes.STRING)));
            arguments.push(new api.query.expr.ValueExpr(new api.data.Value("AND", api.data.type.ValueTypes.STRING)));

            var nGramExpr: api.query.expr.FunctionExpr = new api.query.expr.FunctionExpr("ngram", arguments);
            var nGramDynamicExpr: api.query.expr.DynamicConstraintExpr = new api.query.expr.DynamicConstraintExpr(nGramExpr);

            var queryExpr: QueryExpr = new QueryExpr(nGramDynamicExpr);

            var query = new ContentQuery();
            query.setSize(10);
            query.setQueryExpr(queryExpr);

            var queryRequest = new ContentQueryRequest(query);
            queryRequest.setExpand(api.rest.Expand.FULL);
            return queryRequest.sendAndParse().
                then((contentQueryResult: ContentQueryResult<api.content.Content,api.content.json.ContentJson>) => {
                    var values: string[] = [];
                    contentQueryResult.getContents().forEach((content: api.content.Content) => {
                        var dataSet = this.dataPath.getParentPath().isRoot() ?
                                      content.getContentData() :
                                      content.getContentData().getDataSetFromDataPath(this.dataPath);
                        var properties = dataSet.getPropertiesByName(this.dataPath.getLastElement().getName());
                        properties.forEach((property: api.data.Property) => {
                            var tag = property.getValue().asString();
                            if (tag.indexOf(value) >= 0 && values.indexOf(tag) < 0) {
                                values.push(tag);
                            }
                        });
                    });
                    return values;
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                    return [];
                });
        }
    }
}
