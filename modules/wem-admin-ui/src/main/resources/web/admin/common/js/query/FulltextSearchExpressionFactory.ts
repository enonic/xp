module api.query {

    export class FulltextSearchExpressionFactory {

        public static create(searchString: string): api.query.expr.Expression {

            if (searchString == null) {
                return null;
            }
            var arguments: api.query.expr.ValueExpr[] = [];

            var fields: api.query.expr.ValueExpr = new api.query.expr.ValueExpr(new api.data.Value("displayName^5,name^3,_all_text",
                api.data.type.ValueTypes.STRING));

            arguments.push(fields);
            arguments.push(new api.query.expr.ValueExpr(new api.data.Value(searchString, api.data.type.ValueTypes.STRING)));
            arguments.push(new api.query.expr.ValueExpr(new api.data.Value("AND", api.data.type.ValueTypes.STRING)));

            var fulltextExp: api.query.expr.FunctionExpr = new api.query.expr.FunctionExpr("fulltext", arguments);
            var fulltextDynamicExpr: api.query.expr.DynamicConstraintExpr = new api.query.expr.DynamicConstraintExpr(fulltextExp);

            var nGramExpr: api.query.expr.FunctionExpr = new api.query.expr.FunctionExpr("ngram", arguments);
            var nGramDynamicExpr: api.query.expr.DynamicConstraintExpr = new api.query.expr.DynamicConstraintExpr(nGramExpr);

            var booleanExpr: api.query.expr.LogicalExpr = new api.query.expr.LogicalExpr(fulltextDynamicExpr,
                api.query.expr.LogicalOperator.OR, nGramDynamicExpr);
            return booleanExpr;
        }
    }
}

