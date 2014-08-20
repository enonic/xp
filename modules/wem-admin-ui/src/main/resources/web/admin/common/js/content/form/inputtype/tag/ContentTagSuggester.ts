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
    import ValueTypes = api.data.ValueTypes;

    export class ContentTagSuggesterBuilder {

        contentType: api.schema.content.ContentTypeName;

        dataPath: DataPath;

        setContentType(value: api.schema.content.ContentTypeName): ContentTagSuggesterBuilder {
            this.contentType = value;
            return this;
        }

        setDataPath(value: DataPath): ContentTagSuggesterBuilder {
            this.dataPath = value;
            return this;
        }

        build(): ContentTagSuggester {
            return new ContentTagSuggester(this);
        }
    }

    export class ContentTagSuggester implements api.ui.tags.TagSuggester {

        private contentType: api.schema.content.ContentTypeName;

        private dataPath: DataPath;

        constructor(builder: ContentTagSuggesterBuilder) {
            this.contentType = builder.contentType;
            this.dataPath = builder.dataPath;
        }

        suggest(value: string): Q.Promise<string[]> {

            var fieldName = 'contentdata' + this.dataPath.getParentPath().toString() + this.dataPath.getLastElement().getName();

            var queryExpr = new QueryExpr(new CompareExpr(new FieldExpr(fieldName), CompareOperator.LIKE,
                [new ValueExpr(new Value(value, ValueTypes.STRING))]));

            var query = new ContentQuery();
            query.setContentTypeNames(this.contentType ? [this.contentType] : []);
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
