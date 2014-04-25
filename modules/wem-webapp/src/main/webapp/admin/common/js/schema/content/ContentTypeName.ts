module api.schema.content {

    export class ContentTypeName implements api.Equitable {

        private value: string;

        constructor(name: string) {
            this.value = name
        }

        toString(): string {
            return this.value;
        }

        equals(o: api.Equitable): boolean {

            if (!(o instanceof ContentTypeName)) {
                return false;
            }

            var other = <ContentTypeName>o;

            if (!api.EquitableHelper.stringEquals(this.value, other.value)) {
                return false;
            }

            return true;
        }
    }
}