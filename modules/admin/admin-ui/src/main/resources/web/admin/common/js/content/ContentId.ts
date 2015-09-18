module api.content {

    export class ContentId implements api.Equitable {

        private value: string;

        constructor(value: string) {
            if (!ContentId.isValidContentId(value)) {
                throw new Error("Invalid content id: " + value)
            }
            this.value = value;
        }

        toString(): string {
            return this.value;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ContentId)) {
                return false;
            }

            var other = <ContentId>o;

            if (!api.ObjectHelper.stringEquals(this.value, other.value)) {
                return false;
            }

            return true;
        }

        static isValidContentId(id: string): boolean {
            return !api.util.StringHelper.isEmpty(id) && !api.util.StringHelper.isBlank(id);
        }

        static fromReference(reference: api.util.Reference) {
            return new ContentId(reference.getNodeId());
        }
    }
}
