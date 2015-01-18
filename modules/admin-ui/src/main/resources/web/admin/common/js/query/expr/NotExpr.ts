module api.query.expr {

    export class NotExpr implements ConstraintExpr {
        private expr: Expression;

        constructor(expr: ConstraintExpr) {
            this.expr = expr;
        }

        getExpression(): Expression {
            return this.expr;
        }

        toString() {
            return "NOT (" + this.expr.toString() + ")";
        }
    }
}
