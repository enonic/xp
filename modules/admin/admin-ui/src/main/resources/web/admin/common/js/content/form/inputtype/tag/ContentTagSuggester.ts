module api.content.form.inputtype.tag {

    import Content = api.content.Content;
    import ContentJson = api.content.json.ContentJson;
    import ContentQuery = api.content.query.ContentQuery;
    import ContentQueryRequest = api.content.resource.ContentQueryRequest;
    import QueryExpr = api.query.expr.QueryExpr;
    import FieldExpr = api.query.expr.FieldExpr;
    import CompareOperator = api.query.expr.CompareOperator;
    import FunctionExpr = api.query.expr.FunctionExpr;
    import DynamicConstraintExpr = api.query.expr.DynamicConstraintExpr;
    import ValueExpr = api.query.expr.ValueExpr;
    import PropertyPath = api.data.PropertyPath;
    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;

    export class ContentTagSuggesterBuilder {

        dataPath: PropertyPath;

        setDataPath(value: PropertyPath): ContentTagSuggesterBuilder {
            this.dataPath = value;
            return this;
        }

        build(): ContentTagSuggester {
            return new ContentTagSuggester(this);
        }
    }

    export class ContentTagSuggester implements api.ui.tags.TagSuggester {

        private propertyPath: PropertyPath;

        constructor(builder: ContentTagSuggesterBuilder) {
            this.propertyPath = builder.dataPath;
        }

        suggest(value: string): wemQ.Promise<string[]> {

            let fieldName = 'data' + this.propertyPath.getParentPath().toString() + this.propertyPath.getLastElement().getName();

            let fulltextExpression: api.query.expr.Expression = new api.query.FulltextSearchExpressionBuilder().
                setSearchString(value).
                addField(new api.query.QueryField(fieldName)).
                build();

            let queryExpr: QueryExpr = new QueryExpr(fulltextExpression);

            let query = new ContentQuery();
            query.setSize(10);
            query.setQueryExpr(queryExpr);

            let queryRequest = new ContentQueryRequest(query);
            queryRequest.setExpand(api.rest.Expand.FULL);

            return queryRequest.sendAndParse().then(
                (contentQueryResult: api.content.resource.result.ContentQueryResult<Content,ContentJson>) => {

                    let suggestedTags: string[] = [];
                    contentQueryResult.getContents().forEach((content: Content) => {
                        let propertySet = this.propertyPath.getParentPath().isRoot() ?
                                          content.getContentData().getRoot() :
                                          content.getContentData().getPropertySet(this.propertyPath);
                        propertySet.forEachProperty(this.propertyPath.getLastElement().getName(), (property: Property) => {
                            if (property.hasNonNullValue()) {
                                let suggestedTag = property.getString();
                                if (suggestedTag.search(new RegExp(value, 'i')) === 0 && suggestedTags.indexOf(suggestedTag) < 0) {
                                    suggestedTags.push(suggestedTag);
                                }
                            }
                        });
                    });
                    return suggestedTags;
                });
        }
    }
}
