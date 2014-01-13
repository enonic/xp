module api.content {

    export class ContentName extends api.Name{

        public static UNNAMED_PREFIX: string = "__unnamed__";

        public static FORBIDDEN_CHARS: RegExp = /[^a-z0-9\-]+/ig;

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
            var validStr = ContentName.ensureValidName(str);
            if (validStr.indexOf(ContentName.UNNAMED_PREFIX) == 0) {
                return new ContentUnnamed(validStr);
            }
            else {
                return new ContentName(validStr);
            }
        }

        public static ensureValidName(possibleInvalidName:string):string {
            if (!possibleInvalidName) {
                return "";
            }

            var generated = possibleInvalidName.replace(/[\s+\.\/]/ig, '-').replace(/-{2,}/g, '-').replace(/^-|-$/g, '').toLowerCase();
            return (generated || '').replace(ContentName.FORBIDDEN_CHARS, '');

        }
    }
}
