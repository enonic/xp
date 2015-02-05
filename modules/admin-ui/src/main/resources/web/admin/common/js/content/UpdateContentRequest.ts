module api.content {

    import Thumbnail = api.thumb.Thumbnail;

    export class UpdateContentRequest extends ContentResourceRequest<api.content.json.ContentJson, Content> {

        private id: string;

        private name: ContentName;

        private data: api.data.PropertyTree;

        private meta: Metadata[];

        private displayName: string;

        private valid: boolean;

        private requireValid: boolean;

        private thumbnail: Thumbnail;

        private language: string;

        private owner: api.security.PrincipalKey;

        constructor(id: string) {
            super();
            this.id = id;
            this.valid = false;
            this.requireValid = false;
            this.setMethod("POST");
        }

        setThumbnail(thumbnail: Thumbnail) {
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

        setData(contentData: api.data.PropertyTree): UpdateContentRequest {
            this.data = contentData;
            return this;
        }

        setMetadata(metadata: Metadata[]): UpdateContentRequest {
            this.meta = metadata;
            return this;
        }

        setDisplayName(displayName: string): UpdateContentRequest {
            this.displayName = displayName;
            return this;
        }

        setValid(valid: boolean): UpdateContentRequest {
            this.valid = valid;
            return this;
        }

        setRequireValid(requireValid: boolean): UpdateContentRequest {
            this.requireValid = requireValid;
            return this;
        }

        setLanguage(language: string): UpdateContentRequest {
            this.language = language;
            return this;
        }

        setOwner(owner: api.security.PrincipalKey): UpdateContentRequest {
            this.owner = owner;
            return this;
        }

        getParams(): Object {
            return {
                contentId: this.id,
                valid: this.valid,
                requireValid: this.requireValid,
                contentName: this.name.isUnnamed() ? this.name.toUnnamed().toStringIncludingHidden() : this.name.toString(),
                data: this.data.toJson(),
                meta: (this.meta || []).map((metadata: Metadata) => metadata.toJson()),
                displayName: this.displayName,
                thumbnail: this.thumbnail ? this.thumbnail.toJson() : undefined,
                language: this.language,
                owner: this.owner ? this.owner.toString() : undefined
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "update");
        }

        sendAndParse(): wemQ.Promise<Content> {

            return this.send().then((response: api.rest.JsonResponse<api.content.json.ContentJson>) => {
                return this.fromJsonToContent(response.getResult());
            });
        }

    }

}