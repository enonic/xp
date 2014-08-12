module api.query.expr {

    export class FunctionExpr implements Expression {

        private name: string;
        private arguments: ValueExpr[] = [];

        constructor(name: string, arguments: ValueExpr[]) {
            this.name = name;
            this.arguments = arguments;
        }

        getName(): string {
            return this.name;
        }

        getArguments(): ValueExpr[] {
            return this.arguments;
        }

        toString() {
            var result: string = this.name;
            result = result.concat("(");

            var sub = [];
            this.arguments.forEach((expr: ValueExpr) => {
                sub.push(expr.toString());
            });
            result = result.concat(sub.join(", "));

            result = result.concat(")");

            return result;
        }
    }
}
