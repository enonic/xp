module api.content {

    export class UpdateContentRequest extends ContentResourceRequest<any> {

        private id: string;

        private name: ContentName;

        private contentType: api.schema.content.ContentTypeName;

        private form: api.form.Form;

        private contentData: ContentData;

        private displayName: string;

        private draft: boolean;

        private updateAttachments: api.content.UpdateAttachments;

        private thumbnail: api.content.Thumbnail;

        constructor(id: string) {
            super();
            this.id = id;
            this.draft = false;
            this.setMethod("POST");
        }

        setUpdateAttachments(updateAttachments: api.content.UpdateAttachments) {
            this.updateAttachments = updateAttachments;
        }

        setThumbnail(thumbnail: api.content.Thumbnail) {
            this.thumbnail = thumbnail;
        }

        setId(id: string): UpdateContentRequest {
            this.id = id;
            return this;
        }

        setContentName(value: ContentName): UpdateContentRequest {
            this.name = value;
            return this;
        }

        setContentType(value: api.schema.content.ContentTypeName): UpdateContentRequest {
            this.contentType = value;
            return this;
        }

        setForm(form: api.form.Form): UpdateContentRequest {
            this.form = form;
            return this;
        }

        setContentData(contentData: api.content.ContentData): UpdateContentRequest {
            this.contentData = contentData;
            return this;
        }

        setDisplayName(displayName: string): UpdateContentRequest {
            this.displayName = displayName;
            return this;
        }

        setDraft(draft: boolean): UpdateContentRequest {
            this.draft = draft;
            return this;
        }

        getParams(): Object {
            return {
                contentId: this.id,
                draft: this.draft,
                contentName: this.name.isUnnamed() ? this.name.toUnnamed().toStringIncludingHidden() : this.name.toString(),
                contentType: this.contentType.toString(),
                form: this.form.toJson(),
                contentData: this.contentData.toJson(),
                displayName: this.displayName,
                updateAttachments: this.updateAttachments.toJson(),
                thumbnail: this.thumbnail? this.thumbnail.toJson() : undefined
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "update");
        }

        sendAndParse(): Q.Promise<Content> {

            return this.send().then((response: api.rest.JsonResponse<api.content.json.ContentJson>) => {
                return this.fromJsonToContent(response.getResult());
            });
        }

    }

}