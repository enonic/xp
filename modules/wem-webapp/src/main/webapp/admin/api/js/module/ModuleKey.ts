module api_module{

    export class ModuleKey {

        private static SEPARATOR:string = "-";

        private name:string;

        private version:string;

        public static fromString(str:string):ModuleKey{
            var sepIndex:number = str.indexOf(ModuleKey.SEPARATOR)
            if( sepIndex == -1 ) {
                throw new Error("ModuleKey must contain separator '-':" + str);
            }

            var name = str.substring(0, sepIndex);
            var version = str.substring(sepIndex+1, str.length);

            return new ModuleKey(name, version);
        }

        constructor(moduleName:string, moduleVersion:string) {
            this.name = moduleName;
            this.version = moduleVersion;
        }

        getName():string {
            return this.name;
        }

        getVersion():string {
            return this.version;
        }

        toString():string {
            return this.name + ModuleKey.SEPARATOR + this.version;
        }

    }
}