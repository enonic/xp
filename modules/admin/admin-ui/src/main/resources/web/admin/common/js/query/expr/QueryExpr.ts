module api.query.expr {

    export class QueryExpr implements Expression {

        private constraint: ConstraintExpr;
        private orderList: OrderExpr[] = [];

        constructor(constraint: ConstraintExpr, orderList?: OrderExpr[]) {
            this.constraint = constraint;
            if (orderList) {
                this.orderList = orderList;
            }
        }

        getConstraint(): ConstraintExpr {
            return this.constraint;
        }

        getOrderList(): OrderExpr[] {
            return this.orderList;
        }

        toString() {
            var result: string = "";

            if (this.constraint != null) {
                result = result.concat(this.constraint.toString());
            }

            if (this.orderList.length > 0) {
                result = result.concat(" ORDER BY ");

                var sub = [];
                this.orderList.forEach((expr: OrderExpr) => {
                    sub.push(expr.toString());
                });
                result = result.concat(sub.join(", "));
            }

            return result;
        }
    }
}
