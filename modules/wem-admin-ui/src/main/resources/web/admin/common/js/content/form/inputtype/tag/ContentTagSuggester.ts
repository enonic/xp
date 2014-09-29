module api.content.form.inputtype.tag {

    import Content = api.content.Content;
    import ContentJson = api.content.json.ContentJson;
    import ContentQuery = api.content.query.ContentQuery;
    import ContentQueryRequest = api.content.ContentQueryRequest;
    import QueryExpr = api.query.expr.QueryExpr;
    import CompareExpr = api.query.expr.CompareExpr;
    import FieldExpr = api.query.expr.FieldExpr;
    import CompareOperator = api.query.expr.CompareOperator;
    import FunctionExpr = api.query.expr.FunctionExpr;
    import DynamicConstraintExpr = api.query.expr.DynamicConstraintExpr;
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

            var fieldName = ContentData.CONTENT_DATA_PATH +
                            this.dataPath.getParentPath().toString() +
                            this.dataPath.getLastElement().getName();

            var fulltextExpression: api.query.expr.Expression = new api.query.FulltextSearchExpressionBuilder().
                setSearchString(value).
                addField(new api.query.QueryField(fieldName)).
                build();

            var queryExpr: QueryExpr = new QueryExpr(fulltextExpression);

            var query = new ContentQuery();
            query.setSize(10);
            query.setQueryExpr(queryExpr);

            var queryRequest = new ContentQueryRequest(query);
            queryRequest.setExpand(api.rest.Expand.FULL);
            return queryRequest.sendAndParse().
                then((contentQueryResult: ContentQueryResult<Content,ContentJson>) => {
                    var values: string[] = [];
                    contentQueryResult.getContents().forEach((content: Content) => {
                        var dataSet = this.dataPath.getParentPath().isRoot() ?
                                      content.getContentData() :
                                      content.getContentData().getDataSetByPath(this.dataPath);
                        var properties = dataSet.getPropertiesByName(this.dataPath.getLastElement().getName());
                        properties.forEach((property: api.data.Property) => {
                            if (property.hasNonNullValue()) {
                                var tag = property.getString();
                                if (values.indexOf(tag) < 0) {
                                    values.push(tag);
                                }
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
