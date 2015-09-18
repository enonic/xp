module api.data {

    export class ValueTypeLocalTime extends ValueType {

        constructor() {
            super("LocalTime");
        }

        isValid(value: any): boolean {

            if (!(typeof value === 'object')) {
                return false;
            }
            if (!api.ObjectHelper.iFrameSafeInstanceOf(value, api.util.LocalTime)) {
                return false;
            }
            return true;
        }

        isConvertible(value: string): boolean {

            var asString = <string>value;
            if (api.util.StringHelper.isBlank(value)) {
                return false;
            }
            return api.util.LocalTime.isValidString(value);
        }


        newValue(value: string): Value {
            if (!value) {
                return this.newNullValue();
            }

            if (!this.isConvertible(value)) {
                return this.newNullValue();
            }
            return new Value(api.util.LocalTime.fromString(value), this);
        }

        valueToString(value: Value): string {
            if (value.isNotNull()) {
                return value.getLocalTime().toString();
            }
            else {
                return null;
            }
        }

        valueEquals(a: api.util.LocalTime, b: api.util.LocalTime): boolean {
            return api.ObjectHelper.equals(a, b);
        }


        toJsonValue(value: Value): string {
            return value.isNull() ? null : value.getLocalTime().toString();
        }
    }
}