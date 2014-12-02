module api.content {

    export class DynamicOrderExpr extends OrderExpr {

        private function: string;

        constructor(builder: DynamicOrderExprBuilder) {
            super(builder);
            this.function = builder.function;
        }

        getFunction(): string {
            return this.function;
        }

        toString() {
            return this.function + " " + super.getDirection();
        }

        toJson(): json.OrderExprJson {
            return {
                "function": this.function,
                "direction": this.getDirection()
            };
        }
    }

    export class DynamicOrderExprBuilder extends OrderExprBuilder {

        function: string;

        constructor(json: json.OrderExprJson) {
            super(json);
            this.function = json.function;
        }

        public setFunction(value: string): DynamicOrderExprBuilder {
            this.function = value;
            return this;
        }

        public build(): DynamicOrderExpr {
            return new DynamicOrderExpr(this);
        }
    }
}