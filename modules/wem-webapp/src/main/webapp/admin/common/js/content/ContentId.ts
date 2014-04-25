module api.content {

    export class ContentId implements api.Equitable {

        private value: string;

        constructor(value: string) {
            if (!ContentId.isValidContentId(value)) {
                throw new Error("Invalid content id")
            }
            this.value = value;
        }

        toString(): string {
            return this.value;
        }

        equals(o: api.Equitable): boolean {

            if (!(o instanceof ContentId)) {
                return false;
            }

            var other = <ContentId>o;

            if (!api.EquitableHelper.stringEquals(this.value, other.value)) {
                return false;
            }

            return true;
        }

        static isValidContentId(id: string): boolean {
            return !api.util.isStringEmpty(id) && !api.util.isStringBlank(id);
        }
    }
}
