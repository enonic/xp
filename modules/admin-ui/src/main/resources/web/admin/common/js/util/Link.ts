module api.util {

    export class Link implements api.Equitable {

        private path: string;

        constructor(value: string) {
            this.path = value;
        }

        getPath(): string {
            return this.path;
        }

        equals(o: Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Link)) {
                return false;
            }

            var other = <Link>o;

            if (!api.ObjectHelper.stringEquals(this.path, other.path)) {
                return false;
            }
        }

        toString(): string {
            return this.path;
        }
    }
}