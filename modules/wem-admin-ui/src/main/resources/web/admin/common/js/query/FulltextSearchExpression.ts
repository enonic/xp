module api.query {

    import ValueExpr = api.query.expr.ValueExpr;
    import FunctionExpr = api.query.expr.FunctionExpr;
    import DynamicConstraintExpr = api.query.expr.DynamicConstraintExpr;
    import LogicalExpr = api.query.expr.LogicalExpr;
    import LogicalOperator = api.query.expr.LogicalOperator;

    export class FulltextSearchExpression {

        static create(searchString: string, queryFields: QueryFields): api.query.expr.Expression {

            if (searchString == null) {
                return null;
            }
            var arguments: api.query.expr.ValueExpr[] = [];

            arguments.push(ValueExpr.stringValue(queryFields.toString()));
            arguments.push(ValueExpr.stringValue(searchString));
            arguments.push(ValueExpr.stringValue("AND"));

            var fulltextExp: FunctionExpr = new FunctionExpr("fulltext", arguments);
            var fulltextDynamicExpr: DynamicConstraintExpr = new DynamicConstraintExpr(fulltextExp);

            var nGramExpr: FunctionExpr = new FunctionExpr("ngram", arguments);
            var nGramDynamicExpr: DynamicConstraintExpr = new DynamicConstraintExpr(nGramExpr);

            var booleanExpr: LogicalExpr = new LogicalExpr(fulltextDynamicExpr, LogicalOperator.OR, nGramDynamicExpr);
            return booleanExpr;
        }
    }

    export class FulltextSearchExpressionBuilder {

        queryFields: QueryFields = new QueryFields();

        searchString: string;

        addField(queryField: QueryField): FulltextSearchExpressionBuilder {
            this.queryFields.add(queryField);
            return this;
        }

        setSearchString(searchString: string): FulltextSearchExpressionBuilder {
            this.searchString = searchString;
            return this;
        }

        build(): api.query.expr.Expression {
            return FulltextSearchExpression.create(this.searchString, this.queryFields);
        }

    }

}

