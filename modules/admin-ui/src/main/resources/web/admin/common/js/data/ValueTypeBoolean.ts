module api.data {

    export class ValueTypeBoolean extends ValueType {

        constructor() {
            super("Boolean");
        }

        isValid(value: any): boolean {
            return typeof value === 'boolean';
        }

        isConvertible(value: string): boolean {
            if (api.util.StringHelper.isBlank(value)) {
                return false;
            }
            if (!(value == "true" || value == "false" )) {
                return false;
            }
            var convertedValue = Boolean(value);
            return this.isValid(convertedValue);
        }

        newValue(value: string): Value {
            if (!this.isConvertible(value)) {
                return new Value(null, this);
            }
            return new Value(this.convertFromString(value), this);
        }

        fromJsonValue(jsonValue: boolean): Value {
            return new Value(jsonValue, this);
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

        valueEquals(a: boolean, b: boolean): boolean {
            return api.ObjectHelper.booleanEquals(a, b);
        }

        newFalse(): Value {
            return new Value(false, this);
        }

        newTrue(): Value {
            return new Value(true, this);
        }
    }
}