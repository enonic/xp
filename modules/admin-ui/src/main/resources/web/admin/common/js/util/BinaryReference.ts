module api.util {

    export class BinaryReference implements api.Equitable {

        private value: string;

        constructor(value: string) {
            this.value = value;
        }

        getValue(): string {
            return this.value;
        }

        equals(o: Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, BinaryReference)) {
                return false;
            }

            var other = <BinaryReference>o;

            if (!api.ObjectHelper.stringEquals(this.value, other.value)) {
                return false;
            }

            return true;
        }

        toString(): string {
            return this.value;
        }
    }
}