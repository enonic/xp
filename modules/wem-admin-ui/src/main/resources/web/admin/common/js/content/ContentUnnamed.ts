module api.content {

    export class ContentUnnamed extends ContentName implements api.Equitable {

        public static PRETTY_UNNAMED = "unnamed";

        constructor(name: string) {
            super(name);
            api.util.assert(name.indexOf(ContentName.UNNAMED_PREFIX) == 0,
                    "An UnnamedContent must start with [" + ContentName.UNNAMED_PREFIX + "]: " + name);
        }

        isUnnamed(): boolean {
            return true;
        }

        toString(): string {
            return "";
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ContentUnnamed)) {
                return false;
            }

            if (!super.equals(o)) {
                return false;
            }

            return true;
        }

        toStringIncludingHidden() {
            return super.toString();
        }

        public static newUnnamed() {
            return new ContentUnnamed(ContentName.UNNAMED_PREFIX);
        }
    }
}
