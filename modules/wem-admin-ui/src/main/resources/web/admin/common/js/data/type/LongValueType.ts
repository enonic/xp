module api.data.type {

    export class LongValueType extends ValueType {

        constructor() {
            super("Long");
        }

        isValid(value: any): boolean {
            return typeof value === 'number' && !isNaN(value);
        }

        isConvertible(value: string): boolean {
            if (api.util.isStringBlank(value)) {
                return false;
            }
            var convertedValue = Number(value);
            return this.isValid(convertedValue);
        }

        newValue(value: string): Value {
            if (!this.isConvertible(value)) {
                return null;
            }
            return new Value(this.convertFromString(value), this);
        }

        private convertFromString(value: string): number {
            return Number(value);
        }

        valueToString(value: Value): string {
            return (<Number>value.getObject()).toString();
        }
    }
}