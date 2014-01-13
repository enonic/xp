module api.content {

    export class ContentName extends api.Name{

        public static UNNAMED_PREFIX: string = "__unnamed__";

        constructor(name: string) {
            super(name);
        }

        isUnnamed(): boolean {
            return false;
        }

        toUnnamed():ContentUnnamed {
            api.util.assert( this instanceof ContentUnnamed, "this is not a ContentUnnamed" );
            return <ContentUnnamed>this;
        }

        public static fromString(str: string) {

            api.util.assert( str != null, "name cannot be null" );

            if (str.indexOf(ContentName.UNNAMED_PREFIX) == 0) {
                return new ContentUnnamed(str);
            }
            else {
                return new ContentName(str);
            }
        }

        public static ensureValidName(possibleInvalidName:string) {
            api.util.assert( possibleInvalidName != null, "name cannot be null" );

            return ContentName.fromString(possibleInvalidName.toLowerCase());
        }
    }
}
