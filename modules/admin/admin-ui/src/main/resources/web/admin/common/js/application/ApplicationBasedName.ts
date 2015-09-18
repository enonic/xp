module api.application {


    export class ApplicationBasedName implements api.Equitable {
        static SEPARATOR = ":";

        private refString: string;

        private applicationKey: ApplicationKey;

        private localName: string;

        constructor(applicationKey: ApplicationKey, localName: string) {
            this.applicationKey = applicationKey;
            this.localName = localName;
            this.refString = applicationKey.toString() ? applicationKey.toString() + ApplicationBasedName.SEPARATOR + localName : localName;
        }

        getLocalName(): string {
            return this.localName;
        }

        getApplicationKey(): ApplicationKey {
            return this.applicationKey;
        }

        toString(): string {
            return this.refString;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, api.ClassHelper.getClass(this))) {
                return false;
            }

            var other = <ApplicationBasedName>o;

            if (!api.ObjectHelper.stringEquals(this.refString, other.refString)) {
                return false;
            }

            return true;
        }

    }

}
