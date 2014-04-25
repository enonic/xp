module api.data {

    export class ValueType implements api.Equitable {

        private name: string;

        constructor(name: string) {
            this.name = name;
        }

        toString() {
            return this.name;
        }

        valueToString(value: Value): string {
            return <string>value.asObject();
        }

        equals(o: api.Equitable): boolean {

            if (!(o instanceof ValueType)) {
                return false;
            }

            var other = <ValueType>o;

            if (!api.EquitableHelper.stringEquals(this.name, other.name)) {
                return false;
            }

            return true;
        }
    }
}