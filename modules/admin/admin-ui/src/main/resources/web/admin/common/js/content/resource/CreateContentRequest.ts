module api.content.resource {

    export class CreateContentRequest extends ContentResourceRequest<api.content.json.ContentJson, Content> {

        private valid: boolean;

        private requireValid: boolean;

        private name: ContentName;

        private parent: ContentPath;

        private contentType: api.schema.content.ContentTypeName;

        private data: api.data.PropertyTree;

        private meta: ExtraData[] = [];

        private displayName: string;

        constructor() {
            super();
            this.valid = false;
            this.requireValid = false;
            super.setMethod("POST");
        }

        setValid(value: boolean): CreateContentRequest {
            this.valid = value;
            return this;
        }

        setRequireValid(value: boolean): CreateContentRequest {
            this.requireValid = value;
            return this;
        }

        setName(value: ContentName): CreateContentRequest {
            this.name = value;
            return this;
        }

        setParent(value: ContentPath): CreateContentRequest {
            this.parent = value;
            return this;
        }

        setContentType(value: api.schema.content.ContentTypeName): CreateContentRequest {
            this.contentType = value;
            return this;
        }

        setData(data: api.data.PropertyTree): CreateContentRequest {
            this.data = data;
            return this;
        }

        setExtraData(extraData: ExtraData[]): CreateContentRequest {
            this.meta = extraData;
            return this;
        }

        setDisplayName(displayName: string): CreateContentRequest {
            this.displayName = displayName;
            return this;
        }

        getParams(): Object {
            return {
                valid: this.valid,
                requireValid: this.requireValid,
                name: this.name.isUnnamed() ? this.name.toUnnamed().toStringIncludingHidden() : this.name.toString(),
                parent: this.parent.toString(),
                contentType: this.contentType.toString(),
                data: this.data.toJson(),
                meta: this.extraDataToJson(),
                displayName: this.displayName
            };
        }

        private extraDataToJson(): api.content.json.ExtraDataJson[] {
            return this.meta ? this.meta.map((extraData: ExtraData) => extraData.toJson()) : null;
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "create");
        }

        sendAndParse(): wemQ.Promise<Content> {

            return this.send().then((response: api.rest.JsonResponse<api.content.json.ContentJson>) => {

                return this.fromJsonToContent(response.getResult());

            });
        }

    }
}