module api.data.type {

    export class LongValueType extends ValueType {

        constructor() {
            super("Long");
        }

        isValid(value: any): boolean {
            return api.util.NumberHelper.isWholeNumber(value);
        }

        isConvertible(value: string): boolean {
            if (api.util.StringHelper.isBlank(value)) {
                return false;
            }
            var convertedValue = Number(value);
            return this.isValid(convertedValue);
        }

        newValue(value: string): Value {
            if (!value) {
                return this.newNullValue();
            }

            if (!this.isConvertible(value)) {
                return this.newNullValue();
            }
            return new Value(this.convertFromString(value), this);
        }

        fromJsonValue(jsonValue: number): api.data.Value {
            return new Value(jsonValue, this);
        }

        private convertFromString(value: string): number {
            return Number(value);
        }

        valueToString(value: Value): string {
            return (<Number>value.getObject()).toString();
        }
    }
}