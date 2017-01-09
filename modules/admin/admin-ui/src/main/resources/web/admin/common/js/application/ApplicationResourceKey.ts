module api.application {

    export class ApplicationResourceKey {

        private static SEPARATOR: string = ":";

        private applicationKey:ApplicationKey;

        private path:ResourcePath;

        private refString:string;

        public static fromString(str: string): ApplicationResourceKey {
            let sepIndex: number = str.indexOf(ApplicationResourceKey.SEPARATOR);
            if( sepIndex == -1 ) {
                throw new Error("ApplicationResourceKey must contain separator '" + ApplicationResourceKey.SEPARATOR + "':" + str);
            }

            let applicationKey = str.substring(0, sepIndex);
            let path = str.substring(sepIndex+1, str.length);

            return new ApplicationResourceKey(ApplicationKey.fromString(applicationKey), ResourcePath.fromString(path));
        }

        constructor(applicationKey:ApplicationKey, path:ResourcePath) {
            this.applicationKey = applicationKey;
            this.path = path;
            this.refString = applicationKey.toString() + ApplicationResourceKey.SEPARATOR + path.toString();
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