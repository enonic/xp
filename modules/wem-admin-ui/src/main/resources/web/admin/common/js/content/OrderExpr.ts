module api.content {

    import OrderExprJson =  api.content.json.OrderExprJson;
    import OrderExprWrapperJson = api.content.json.OrderExprWrapperJson;

    export class OrderExpr implements api.Equitable {

        private direction: string;

        constructor(builder: OrderExprBuilder) {
            this.direction = builder.direction;
        }

        getDirection(): string {
            return this.direction;
        }

        toJson(): OrderExprJson {
            throw new Error("Must be implemented by inheritors");
        }

        toString(): string {
            throw new Error("Must be implemented by inheritors");
        }

        static toArrayJson(expressions: OrderExpr[]): OrderExprWrapperJson[] {
            var wrappers: OrderExprWrapperJson[] = [];
            expressions.forEach((expr: OrderExpr) => {
                if (api.ObjectHelper.iFrameSafeInstanceOf(expr, FieldOrderExpr)) {
                    wrappers.push(<OrderExprWrapperJson>{"FieldOrderExpr": expr.toJson()});
                } else if (api.ObjectHelper.iFrameSafeInstanceOf(expr, DynamicOrderExpr)) {
                    wrappers.push(<OrderExprWrapperJson>{"DynamicOrderExpr": expr.toJson()});
                }
            });
            return wrappers;
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, OrderExpr)) {
                return false;
            }
            var other = <OrderExpr>o;
            if (this.direction.toLowerCase() != other.getDirection().toLowerCase()) {
                return false;
            }
            return true;
        }

    }
    export class OrderExprBuilder {

        direction: string;

        constructor(json?: json.OrderExprJson) {
            if (json) {
                this.direction = json.direction;
            }
        }

        public setDirection(value: string): OrderExprBuilder {
            this.direction = value;
            return this;
        }

        public build(): OrderExpr {
            return new OrderExpr(this);
        }
    }
}