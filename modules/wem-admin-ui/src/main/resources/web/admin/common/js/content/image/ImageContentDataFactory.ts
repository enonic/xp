module api.content.image {

    import ValueTypes = api.data.ValueTypes;
    import Value = api.data.Value;
    import PropertyTree = api.data.PropertyTree;

    export class ImageContentDataFactory {

        private image: api.content.attachment.AttachmentName;

        private mimeType: string;

        public setImage(value: api.content.attachment.AttachmentName): ImageContentDataFactory {
            this.image = value;
            return this;
        }

        public setMimeType(value: string): ImageContentDataFactory {
            this.mimeType = value;
            return this;
        }

        public create(): PropertyTree {
            var data = new PropertyTree();
            data.addString("image", this.image.toString());
            data.addString("mimeType", this.mimeType);
            return data;
        }

    }
}