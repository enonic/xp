module api.data {

    export class PropertyId implements api.Equitable {

        private value: string;

        constructor(value: string) {
            this.value = value;
        }

        public equals(o: Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, PropertyId)) {
                return false;
            }

            var other = <PropertyId>o;

            if (!api.ObjectHelper.stringEquals(this.value, other.value)) {
                return false;
            }

            return true;
        }

        public toString(): string {
            return this.value;
        }
    }
}