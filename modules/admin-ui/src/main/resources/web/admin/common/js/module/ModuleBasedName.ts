module api.module {


    export class ModuleBasedName implements api.Equitable {
        static SEPARATOR = ":";

        private refString: string;

        private moduleKey: ModuleKey;

        private localName: string;

        constructor(moduleKey: ModuleKey, localName: string) {
            this.moduleKey = moduleKey;
            this.localName = localName;
            this.refString = moduleKey.toString() ? moduleKey.toString() + ModuleBasedName.SEPARATOR + localName : localName;
        }

        getLocalName(): string {
            return this.localName;
        }

        getModuleKey(): ModuleKey {
            return this.moduleKey;
        }

        toString(): string {
            return this.refString;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, api.ClassHelper.getClass(this))) {
                return false;
            }

            var other = <ModuleBasedName>o;

            if (!api.ObjectHelper.stringEquals(this.refString, other.refString)) {
                return false;
            }

            return true;
        }

    }

}
