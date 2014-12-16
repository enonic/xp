module api.thumb {

    export class Thumbnail implements api.Equitable {

        private binaryReference: api.util.BinaryReference;

        private mimeType: string;

        private size: number;

        constructor(builder: ThumbnailBuilder) {
            this.binaryReference = builder.binaryReference;
            this.mimeType = builder.mimeType;
            this.size = builder.size;
        }

        getBinaryReference(): api.util.BinaryReference {
            return this.binaryReference;
        }

        getMimeType(): string {
            return this.mimeType;
        }

        getSize(): number {
            return this.size;
        }

        toJson(): ThumbnailJson {

            return {
                "binaryReference": this.getBinaryReference().toString(),
                "mimeType": this.getMimeType(),
                "size": this.getSize()
            };
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Thumbnail)) {
                return false;
            }

            var other = <Thumbnail>o;

            if (!api.ObjectHelper.equals(this.binaryReference, other.binaryReference)) {
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

        public static create(): ThumbnailBuilder {
            return new ThumbnailBuilder();
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
