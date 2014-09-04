module api.data.type {

    export class ValueType implements api.Equitable {

        private name: string;

        constructor(name: string) {
            this.name = name;
        }

        toString(): string {
            return this.name;
        }

        valueToString(value: api.data.Value): string {
            return String(value.getObject());
        }

        valueToBoolean(value: api.data.Value): boolean {
            return value.asString() == "true";
        }

        valueToNumber(value: api.data.Value): number {
            return Number(value.getObject());
        }

        isValid(value: any): boolean {
            return true;
        }

        isConvertible(value: any): boolean {
            return true;
        }

        newValue(value: string): api.data.Value {
            return new Value(value, this);
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ValueType)) {
                return false;
            }

            var other = <ValueType>o;

            if (!api.ObjectHelper.stringEquals(this.name, other.name)) {
                return false;
            }

            return true;
        }
    }
}