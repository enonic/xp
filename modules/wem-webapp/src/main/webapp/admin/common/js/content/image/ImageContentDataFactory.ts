module api_content_image{

    export class ImageContentDataFactory {

        private image:api_content.AttachmentName;

        private mimeType:string;

        public setImage(value:api_content.AttachmentName):ImageContentDataFactory {
            this.image = value;
            return this;
        }

        public setMimeType(value:string):ImageContentDataFactory {
            this.mimeType = value;
            return this;
        }

        public create():api_content.ContentData {
            var contentData = new api_content.ContentData();

            var imageValue = new api_data.Value( this.image.toString(), api_data.ValueTypes.STRING );
            var mimeTypeValue = new api_data.Value( this.mimeType, api_data.ValueTypes.STRING );

            contentData.addData(api_data.Property.fromNameValue("image", imageValue ));
            contentData.addData(api_data.Property.fromNameValue("mimeType", mimeTypeValue));
            return contentData;
        }

    }
}