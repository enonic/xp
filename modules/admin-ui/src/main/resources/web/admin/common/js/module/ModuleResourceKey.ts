module api.module{

    export class ModuleResourceKey {

        private static SEPARATOR = ":";

        private applicationKey:ApplicationKey;

        private path:ResourcePath;

        private refString:string;

        public static fromString(str:string):ModuleResourceKey{
            var sepIndex:number = str.indexOf(ModuleResourceKey.SEPARATOR);
            if( sepIndex == -1 ) {
                throw new Error("ModuleResourceKey must contain separator '" + ModuleResourceKey.SEPARATOR + "':" + str);
            }

            var applicationKey = str.substring(0, sepIndex);
            var path = str.substring(sepIndex+1, str.length);

            return new ModuleResourceKey(ApplicationKey.fromString(applicationKey), ResourcePath.fromString(path));
        }

        constructor(applicationKey:ApplicationKey, path:ResourcePath) {
            this.applicationKey = applicationKey;
            this.path = path;
            this.refString = applicationKey.toString() + ModuleResourceKey.SEPARATOR  + path.toString();
        }

        getApplicationKey():ApplicationKey {
            return this.applicationKey;
        }

        getPath():ResourcePath {
            return this.path;
        }

        toString():string {
            return this.refString;
        }

    }
}