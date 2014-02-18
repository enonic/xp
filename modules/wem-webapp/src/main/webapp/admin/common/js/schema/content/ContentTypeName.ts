module api.schema.content {

    export class ContentTypeName {

        private value: string;

        constructor(name: string) {
            this.value = name
        }

        toString(): string {
            return this.value;
        }

        equals(contentTypeName: ContentTypeName): boolean {
            return this.toString() == contentTypeName.toString();
        }
    }
}