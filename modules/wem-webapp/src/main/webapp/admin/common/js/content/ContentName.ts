module api_content {

    export class ContentName {

        public static UNNAMED_PREFIX: string = "__unnamed__";

        private value: string;

        constructor(name: string) {
            api_util.assert( name != null, "name cannot be null" );
            this.value = name;
        }

        isUnnamed(): boolean {
            return false;
        }

        toString(): string {
            return this.value;
        }

        toUnnamed():ContentUnnamed {
            api_util.assert( this instanceof ContentUnnamed, "this is not a ContentUnnamed" );
            return <ContentUnnamed>this;
        }

        public static fromString(str: string) {

            api_util.assert( str != null, "name cannot be null" );

            if (str.indexOf(ContentName.UNNAMED_PREFIX) == 0) {
                return new ContentUnnamed(str);
            }
            else {
                return new ContentName(str);
            }
        }
    }
}
