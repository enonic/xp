module api.query.expr {

    export class CompareExpr implements ConstraintExpr {

        private field: FieldExpr;

        private operator: CompareOperator;

        private values: ValueExpr[] = [];

        constructor(field: FieldExpr, operator: CompareOperator, values: ValueExpr[]) {
            this.field = field;
            this.operator = operator;
            this.values = values;
        }

        getField(): FieldExpr {
            return this.field;
        }

        getOperator(): CompareOperator {
            return this.operator;
        }

        getFirstValue(): ValueExpr {
            return this.values.length > 0 ? this.values[0] : null;
        }

        public getValues(): ValueExpr[] {
            return this.values;
        }

        toString() {
            var result: string = this.field.toString();
            result = result.concat(" ");
            result = result.concat(this.operatorAsString());
            result = result.concat(" ");

            if (this.allowMultipleValues()) {
                result = result.concat("(");

                var sub = [];
                this.values.forEach((expr: ValueExpr) => {
                    sub.push(expr.toString());
                });
                result = result.concat(sub.join(", "));

                result = result.concat(")");

            } else {
                result = result.concat(this.getFirstValue().toString());
            }

            return result;
        }

        public static eq(field: FieldExpr, value: ValueExpr): CompareExpr {
            return CompareExpr.create(field, CompareOperator.EQ, [value]);
        }

        public static neq(field: FieldExpr, value: ValueExpr): CompareExpr {
            return CompareExpr.create(field, CompareOperator.NEQ, [value]);
        }

        public static gt(field: FieldExpr, value: ValueExpr): CompareExpr {
            return CompareExpr.create(field, CompareOperator.GT, [value]);
        }

        public static gte(field: FieldExpr, value: ValueExpr): CompareExpr {
            return CompareExpr.create(field, CompareOperator.GTE, [value]);
        }

        public static lt(field: FieldExpr, value: ValueExpr): CompareExpr {
            return CompareExpr.create(field, CompareOperator.LT, [value]);
        }

        public static lte(field: FieldExpr, value: ValueExpr): CompareExpr {
            return CompareExpr.create(field, CompareOperator.LTE, [value]);
        }

        public static like(field: FieldExpr, value: ValueExpr): CompareExpr {
            return CompareExpr.create(field, CompareOperator.LIKE, [value]);
        }

        public static notLike(field: FieldExpr, value: ValueExpr): CompareExpr {
            return CompareExpr.create(field, CompareOperator.NOT_LIKE, [value]);
        }

        public static In(field: FieldExpr, values: ValueExpr[]): CompareExpr {
            return CompareExpr.create(field, CompareOperator.IN, values);
        }

        public static notIn(field: FieldExpr, values: ValueExpr[]): CompareExpr {
            return CompareExpr.create(field, CompareOperator.NOT_IN, values);
        }

        public static create(field: FieldExpr, operator: CompareOperator, values: ValueExpr[]): CompareExpr {
            return new CompareExpr(field, operator, values);
        }

        private operatorAsString(): string {
            switch (this.operator) {
            case CompareOperator.EQ:
                return "=";
            case CompareOperator.NEQ:
                return "!=";
            case CompareOperator.GT:
                return ">";
            case CompareOperator.GTE:
                return ">=";
            case CompareOperator.LT:
                return "<";
            case CompareOperator.LTE:
                return "<=";
            case CompareOperator.LIKE:
                return "LIKE";
            case CompareOperator.NOT_LIKE:
                return "NOT LIKE";
            case CompareOperator.IN:
                return "IN";
            case CompareOperator.NOT_IN:
                return "NOT IN";
            default:
                return "";
            }
        }

        private allowMultipleValues(): boolean {
            return this.operator == CompareOperator.IN || this.operator == CompareOperator.NOT_IN;
        }
    }
}