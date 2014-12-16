module api.content {

    export class Thumbnail implements api.Equitable {

        private blobKey: api.util.BinaryReference;

        private mimeType: string;

        private size: number;

        constructor(builder: ThumbnailBuilder) {
            this.blobKey = builder.binaryReference;
            this.mimeType = builder.mimeType;
            this.size = builder.size;
        }

        getBlobKey(): api.util.BinaryReference {
            return this.blobKey;
        }

        getMimeType(): string {
            return this.mimeType;
        }

        getSize(): number {
            return this.size;
        }

        toJson(): api.content.ThumbnailJson {

            return {
                "binaryReference": this.getBlobKey().toString(),
                "mimeType": this.getMimeType(),
                "size": this.getSize()
            };
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Thumbnail)) {
                return false;
            }

            var other = <Thumbnail>o;

            if (!api.ObjectHelper.equals(this.blobKey, other.blobKey)){
                return false;
            }
            if (!api.ObjectHelper.stringEquals(this.mimeType, other.mimeType)) {
                return false;
            }
            if (!api.ObjectHelper.numberEquals(this.size, other.size)) {
                return false;
            }
            return true;
        }

    }

    export class ThumbnailBuilder {

        binaryReference: api.util.BinaryReference;

        mimeType: string;

        size: number;

        public fromJson(json: ThumbnailJson): ThumbnailBuilder {
            this.binaryReference = new api.util.BinaryReference(json.binaryReference);
            this.mimeType = json.mimeType;
            this.size = json.size;
            return this;
        }

        public setBinaryReference(value: api.util.BinaryReference): ThumbnailBuilder {
            this.binaryReference = value;
            return this;
        }

        public setMimeType(value: string): ThumbnailBuilder {
            this.mimeType = value;
            return this;
        }

        public setSize(value: number): ThumbnailBuilder {
            this.size = value;
            return this;
        }

        public build(): Thumbnail {
            return new Thumbnail(this);
        }
    }
}
