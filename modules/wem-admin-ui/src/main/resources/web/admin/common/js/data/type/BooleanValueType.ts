module api.data.type {

    export class BooleanValueType extends ValueType {

        constructor() {
            super("Boolean");
        }

        isValid(value: any): boolean {
            return typeof value === 'boolean';
        }

        isConvertible(value: any): boolean {
            if (api.util.isStringBlank(value)) {
                return false;
            }
            var valueAsString = value.toString();
            if (!(valueAsString == "true" || valueAsString == "false" )) {
                return false;
            }
            var convertedValue = Boolean(value);
            return this.isValid(convertedValue);
        }

        newValue(value: string): Value {
            if (!this.isConvertible(value)) {
                return null;
            }
            return new Value(this.convertFromString(value), this);
        }

        private convertFromString(value: string): boolean {
            if (value == "true") {
                return true;
            }
            else if (value == "false") {
                return false;
            }
            else {
                throw new Error("given string cannot be converted to a Boolean Value: " + value);
            }
        }

        valueToString(value: Value): string {
            return (<Boolean>value.getObject()).toString();
        }

        newFalse(): Value {
            return new Value(false, this);
        }

        newTrue(): Value {
            return new Value(true, this);
        }
    }
}