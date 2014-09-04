module api.content.image {

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

        public create(): api.content.ContentData {
            var contentData = new api.content.ContentData();

            var imageValue = new api.data.Value(this.image.toString(), api.data.type.ValueTypes.STRING);
            var mimeTypeValue = new api.data.Value(this.mimeType, api.data.type.ValueTypes.STRING);

            contentData.addData(api.data.Property.fromNameValue("image", imageValue));
            contentData.addData(api.data.Property.fromNameValue("mimeType", mimeTypeValue));
            return contentData;
        }

    }
}