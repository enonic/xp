module api.content {

    export class ContentName extends api.Name implements api.Equitable {

        public static UNNAMED_PREFIX: string = "__unnamed__";


        constructor(name: string) {
            super(name);
        }

        isUnnamed(): boolean {
            return false;
        }

        toUnnamed(): ContentUnnamed {
            api.util.assert(api.ObjectHelper.iFrameSafeInstanceOf(this, ContentUnnamed), "this is not a ContentUnnamed");
            return <ContentUnnamed>this;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ContentName)) {
                return false;
            }

            if (!super.equals(o)) {
                return false;
            }

            return true;
        }

        public static fromString(str: string): ContentName {

            api.util.assert(str != null, "name cannot be null");
            if (str.indexOf(ContentName.UNNAMED_PREFIX) == 0) {
                return new ContentUnnamed(str);
            }
            else {
                return new ContentName(str);
            }
        }

    }
}
