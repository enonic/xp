module api.content.resource.result {

    import ContentPublishItemJson = api.content.json.ContentPublishItemJson;
    import ResolvePublishContentResultJson = api.content.json.ResolvePublishContentResultJson;
    import RequestedContentJson = api.content.json.RequestedContentJson;

    export class ResolvePublishDependenciesResult {

        requestedContents: RequestedContentResult[];
        dependentContents: ContentId[];
        requiredContents: ContentId[];
        containsInvalid: boolean;

        constructor(dependants: ContentId[], requested: RequestedContentResult[], required: ContentId[], containsInvalid: boolean) {
            this.dependentContents = dependants;
            this.requestedContents = requested;
            this.requiredContents = required;
            this.containsInvalid = containsInvalid;
        }

        getDependants(): ContentId[] {
            return this.dependentContents;
        }

        getRequested(): RequestedContentResult[] {
            return this.requestedContents;
        }

        getRequired(): ContentId[] {
            return this.requiredContents;
        }

        isContainsInvalid(): boolean {
            return this.containsInvalid;
        }

        static fromJson(json: ResolvePublishContentResultJson): ResolvePublishDependenciesResult {

            let dependants: ContentId[] = json.dependentContents
                ? json.dependentContents.map(dependant => new ContentId(dependant.id))
                : [];

            let requested: RequestedContentResult[] = json.requestedContents ? json.requestedContents.map(
                requestedJson => RequestedContentResult.fromJson(requestedJson)) : [];

            let required: ContentId[] = json.requiredContents ?
                                        json.requiredContents.map(requiredJson => new ContentId(requiredJson.id)) : [];

            let containsInvalid: boolean = json.containsInvalid;

            return new ResolvePublishDependenciesResult(dependants, requested, required, containsInvalid);
        }
    }

    export class RequestedContentResult {

        private id: ContentId;

        private hasChildren: boolean;

        constructor(id: string, hasChildren: boolean) {
            this.id = new ContentId(id);
            this.hasChildren = hasChildren;
        }

        public static fromJson(json: RequestedContentJson): RequestedContentResult {
            return new RequestedContentResult(json.id.id, json.hasChildren);
        }

        getId(): ContentId {
            return this.id;
        }

        getHasChildren(): boolean {
            return this.hasChildren;
        }
    }

}
