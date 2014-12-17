module api.data {

    export class ValueTypeLink extends ValueType {

        constructor() {
            super("Link");
        }

        isValid(value: any): boolean {
            if (!(typeof value === 'object')) {
                return false;
            }
            if (!api.ObjectHelper.iFrameSafeInstanceOf(value, api.util.Link)) {
                return false;
            }
            return true;
        }

        isConvertible(value: string): boolean {
            if (api.util.StringHelper.isBlank(value)) {
                return false;
            }
            return true;
        }

        newValue(value: string): Value {
            if (this.isConvertible(value)) {
                return new Value(new api.util.Link(value), this);
            }
            else {
                return this.newNullValue();
            }
        }

        valueToString(value: Value): string {
            if (value.isNotNull()) {
                return value.getLink().toString();
            }
            else {
                return null;
            }
        }

        toJsonValue(value: Value): any {
            return value.isNull() ? null : value.getLink().toString();
        }

        valueEquals(a: api.util.Link, b: api.util.Link): boolean {
            return api.ObjectHelper.equals(a, b);
        }
    }
}