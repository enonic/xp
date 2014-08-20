module api.content {

    export class ContentResponse<T> {

        private contents: T[];

        private metadata: ContentMetadata;

        constructor(contents: T[], metadata: ContentMetadata) {
            this.contents = contents;
            this.metadata = metadata;
        }

        getContents(): T[] {
            return this.contents;
        }

        getMetadata(): ContentMetadata {
            return this.metadata;
        }

        setContents(contents: T[]): ContentResponse<T> {
            this.contents = contents;
            return this;
        }

        setMetadata(metadata: ContentMetadata): ContentResponse<T> {
            this.metadata = metadata;
            return this;
        }
    }
}