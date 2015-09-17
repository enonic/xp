module api.data {

    export class ValueTypeBinaryReference extends ValueType {

        constructor() {
            super("BinaryReference");
        }

        isValid(value: any): boolean {
            if (!(typeof value === 'object')) {
                return false;
            }
            if (!api.ObjectHelper.iFrameSafeInstanceOf(value, api.util.BinaryReference)) {
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
                return new Value(new api.util.BinaryReference(value), this);
            }
            else {
                return this.newNullValue();
            }
        }

        valueToString(value: Value): string {
            if (value.isNotNull()) {
                return value.getBinaryReference().toString();
            }
            else {
                return null;
            }
        }

        toJsonValue(value: Value): any {
            return value.isNull() ? null : value.getBinaryReference().toString();
        }

        valueEquals(a: api.util.BinaryReference, b: api.util.BinaryReference): boolean {
            return api.ObjectHelper.equals(a, b);
        }
    }
}