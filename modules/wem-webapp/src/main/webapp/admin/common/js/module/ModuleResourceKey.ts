module api.module{

    export class ModuleResourceKey {

        private static SEPARATOR = ":";

        private moduleKey:ModuleKey;

        private path:ResourcePath;

        private refString:string;

        public static fromString(str:string):ModuleResourceKey{
            var sepIndex:number = str.indexOf(ModuleResourceKey.SEPARATOR);
            if( sepIndex == -1 ) {
                throw new Error("ModuleResourceKey must contain separator '" + ModuleResourceKey.SEPARATOR + "':" + str);
            }

            var moduleKey = str.substring(0, sepIndex);
            var path = str.substring(sepIndex+1, str.length);

            return new ModuleResourceKey(ModuleKey.fromString(moduleKey), ResourcePath.fromString(path));
        }

        constructor(moduleKey:ModuleKey, path:ResourcePath) {
            this.moduleKey = moduleKey;
            this.path = path;
            this.refString = moduleKey.toString() + ModuleResourceKey.SEPARATOR  + path.toString();
        }

        getModuleKey():ModuleKey {
            return this.moduleKey;
        }

        getPath():ResourcePath {
            return this.path;
        }

        toString():string {
            return this.refString;
        }

    }
}