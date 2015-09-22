module api.query.expr {

    export class FieldExpr implements Expression {

        private name: string;

        constructor(name: string) {
            this.name = name;
        }

        getName(): string {
            return this.name;
        }

        toString() {
            return this.name;
        }
    }
}
