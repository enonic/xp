module api.content.page {

    export class DescriptorKey {

        private static SEPARATOR = ":";

        private moduleKey: api.module.ModuleKey;

        private name: ComponentDescriptorName;

        private refString:string;

        public static fromString(str:string):DescriptorKey{
            var sepIndex:number = str.indexOf(DescriptorKey.SEPARATOR);
            if( sepIndex == -1 ) {
                throw new Error("DescriptorKey must contain separator '" + DescriptorKey.SEPARATOR + "':" + str);
            }

            var moduleKey = str.substring(0, sepIndex);
            var name = str.substring(sepIndex+1, str.length);

            return new DescriptorKey(api.module.ModuleKey.fromString(moduleKey), new ComponentDescriptorName(name));
        }

        constructor(moduleKey:api.module.ModuleKey, name: ComponentDescriptorName) {
            this.moduleKey = moduleKey;
            this.name = name;
            this.refString = moduleKey.toString() + DescriptorKey.SEPARATOR  + name.toString();
        }

        getModuleKey():api.module.ModuleKey {
            return this.moduleKey;
        }

        getName():ComponentDescriptorName {
            return this.name;
        }

        toString():string {
            return this.refString;
        }
    }
}