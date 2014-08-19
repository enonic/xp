module api.blob {

    export class BlobKey implements api.Equitable {

        private value: string;

        constructor(value: string) {
            this.value = value;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, BlobKey)) {
                return false;
            }

            var other = <BlobKey>o;

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
