module api_content {

    export class ContentUnnamed extends ContentName {

        constructor(name: string) {
            super(name);
            api_util.assert(name.indexOf(ContentName.UNNAMED_PREFIX) == 0,
                "An UnnamedContent must start with [" + ContentName.UNNAMED_PREFIX + "]: " + name);
        }

        isUnnamed(): boolean {
            return true;
        }

        toString(): string {
            return "";
        }

        toStringIncludingHidden() {
            return super.toString();
        }

        public static newUnnamed() {
            return new ContentUnnamed(ContentName.UNNAMED_PREFIX);
        }
    }
}
