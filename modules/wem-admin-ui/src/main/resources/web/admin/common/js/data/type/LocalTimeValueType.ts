module api.data.type {

    export class LocalTimeValueType extends ValueType {

        constructor() {
            super("LocalTime");
        }

        isValid(value: any): boolean {
            if (!(typeof value === 'string')) {
                return false;
            }
            var valueAsString = <string>value;
            var re = /^[0-2]\d:[0-5]\d$/;
            return re.test(valueAsString);
        }

        isConvertible(value: string): boolean {

            var asString = <string>value;
            if (api.util.isStringBlank(value)) {
                return false;
            }
            if (asString.indexOf(':') == -1) {
                return false;
            }
            if (asString.length != 5) {
                return false;
            }

            return this.isValid(value);
        }

        newValue(value: string): Value {
            if (!this.isConvertible(value)) {
                return null;
            }
            return new Value(value, this);
        }

        toJsonValue(value: api.data.Value): string {
            var time: string = value.getObject().toString();
            return time;
        }
    }
}