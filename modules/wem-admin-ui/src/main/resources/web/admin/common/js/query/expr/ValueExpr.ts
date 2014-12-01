module api.query.expr {

    export class ValueExpr implements Expression {

        private value: api.data.Value;

        constructor(value: api.data.Value) {
            this.value = value;
        }

        getValue(): api.data.Value {
            return this.value;
        }

        static stringValue(value: string): ValueExpr {
            return new ValueExpr(new api.data.Value(value, api.data.ValueTypes.STRING));
        }

        toString() {
            var type: api.data.ValueType = this.value.getType();

            if (type == api.data.ValueTypes.DOUBLE) {
                return this.value.getString();
            }

            if (type == api.data.ValueTypes.DATE_TIME) {
                return this.typecastFunction("dateTime", this.value.getString());
            }

            if (type == api.data.ValueTypes.GEO_POINT) {
                return this.typecastFunction("geoPoint", this.value.getString());
            }

            return this.quoteString(this.value.getString());
        }

        private typecastFunction(name: string, argument: string): string {
            return name + "(" + this.quoteString(argument) + ")";
        }

        private quoteString(value: string): string {
            if (value.indexOf("'") > -1) {
                return "\"" + value + "\"";
            }
            else {
                return "'" + value + "'";
            }
        }

        public static string(value: string): ValueExpr {
            return new ValueExpr(new api.data.Value(value, api.data.ValueTypes.STRING));
        }

        public static number(value: Number): ValueExpr {
            return new ValueExpr(new api.data.Value(value, api.data.ValueTypes.DOUBLE));
        }

        public static dateTime(value: Date): ValueExpr {
            return new ValueExpr(new api.data.Value(value, api.data.ValueTypes.DATE_TIME));
        }

        public static geoPoint(value: string): ValueExpr {
            return new ValueExpr(new api.data.Value(value, api.data.ValueTypes.GEO_POINT));
        }
    }
}