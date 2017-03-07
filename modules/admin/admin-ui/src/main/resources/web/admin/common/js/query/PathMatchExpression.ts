module api.query {

    import ValueExpr = api.query.expr.ValueExpr;
    import FunctionExpr = api.query.expr.FunctionExpr;
    import DynamicConstraintExpr = api.query.expr.DynamicConstraintExpr;
    import LogicalExpr = api.query.expr.LogicalExpr;
    import LogicalOperator = api.query.expr.LogicalOperator;
    import CompareExpr = api.query.expr.CompareExpr;
    import FieldExpr = api.query.expr.FieldExpr;

    export class PathMatchExpression extends FulltextSearchExpression {

        static createWithPath(searchString: string, queryFields: QueryFields, path: string): api.query.expr.Expression {

            let nameExpr = FulltextSearchExpression.create(searchString, queryFields);

            let pathExpr = CompareExpr.like(new FieldExpr('_path'),
                ValueExpr.string('/content/*' + searchString + '*'));

            let nameOrPathExpr: LogicalExpr = new LogicalExpr(nameExpr, LogicalOperator.OR, pathExpr);

            let args = [];
            args.push(ValueExpr.stringValue('_path'));
            args.push(ValueExpr.stringValue('/content' + path));

            let matchedExpr: FunctionExpr = new FunctionExpr('pathMatch', args);
            let matchedDynamicExpr: DynamicConstraintExpr = new DynamicConstraintExpr(matchedExpr);

            let booleanExpr: LogicalExpr = new LogicalExpr(nameOrPathExpr, LogicalOperator.AND, matchedDynamicExpr);
            return booleanExpr;
        }
    }

    export class PathMatchExpressionBuilder extends FulltextSearchExpressionBuilder {

        path: string;

        addField(queryField: QueryField): PathMatchExpressionBuilder {
            super.addField(queryField);
            return this;
        }

        setSearchString(searchString: string): PathMatchExpressionBuilder {
            super.setSearchString(searchString);
            return this;
        }

        setPath(path: string): PathMatchExpressionBuilder {
            this.path = path;
            return this;
        }

        build(): api.query.expr.Expression {
            return PathMatchExpression.createWithPath(this.searchString, this.queryFields, this.path);
        }
    }

}
