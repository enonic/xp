module api_content {

    export class UpdateContentRequest extends ContentResourceRequest<any> {

        private id:string;

        private name:ContentName;

        private contentType:api_schema_content.ContentTypeName;

        private form:api_form.Form;

        private contentData:ContentData;

        private displayName:string;

        private attachments:api_content_attachment.Attachment[] = [];

        constructor(id:string) {
            super();
            this.id = id;
            this.setMethod("POST");
        }

        setId(id:string):UpdateContentRequest {
            this.id = id;
            return this;
        }

        setContentName(value:ContentName):UpdateContentRequest {
            this.name = value;
            return this;
        }

        setContentType(value:api_schema_content.ContentTypeName):UpdateContentRequest {
            this.contentType = value;
            return this;
        }

        setForm(form:api_form.Form):UpdateContentRequest {
            this.form = form;
            return this;
        }

        setContentData(contentData:api_content.ContentData):UpdateContentRequest {
            this.contentData = contentData;
            return this;
        }

        setDisplayName(displayName:string):UpdateContentRequest {
            this.displayName = displayName;
            return this;
        }


        addAttachment(attachment:api_content_attachment.Attachment):UpdateContentRequest {
            this.attachments.push( attachment );
            return this;
        }

        addAttachments(attachments:api_content_attachment.Attachment[]):UpdateContentRequest {
            this.attachments = this.attachments.concat(attachments);
            return this;
        }

        getParams():Object {
            return {
                contentId: this.id,
                contentName: this.name.isUnnamed() ? this.name.toUnnamed().toStringIncludingHidden() : this.name.toString(),
                contentType: this.contentType.toString(),
                form: this.form.toJson(),
                contentData: this.contentData.toJson(),
                displayName: this.displayName,
                attachments: this.attachmentsToJson()
            };
        }

        private attachmentsToJson(): api_content_attachment.AttachmentJson[] {
            var array: api_content_attachment.AttachmentJson[] = [];
            this.attachments.forEach((attachment: api_content_attachment.Attachment)=> {
                var attachmentJsonbj:api_content_attachment.AttachmentJson = {
                    "blobKey": attachment.getBlobKey().toString(),
                    "attachmentName": attachment.getAttachmentName().toString(),
                    "mimeType": attachment.getMimeType(),
                    "size": attachment.getSize()
                };
                array.push(attachmentJsonbj);
            });
            return array;
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "update");
        }

        sendAndParse(): JQueryPromise<Content> {

            var deferred = jQuery.Deferred<Content>();

            this.send().done((response: api_rest.JsonResponse<api_content_json.ContentJson>) => {
                deferred.resolve(this.fromJsonToContent(response.getResult()));
            }).fail((response: api_rest.RequestError) => {
                    deferred.reject(null);
                });

            return deferred;
        }
    }
}