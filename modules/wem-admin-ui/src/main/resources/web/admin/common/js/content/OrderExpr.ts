module api.content {

    import OrderExprJson =  api.content.json.OrderExprJson;
    import OrderExprWrapperJson = api.content.json.OrderExprWrapperJson;

    export class OrderExpr {

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

    }
    export class OrderExprBuilder {

        direction: string;

        constructor(json: json.OrderExprJson) {
            this.direction = json.direction;
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