module api.content.resource.result {

    import ContentPublishItemJson = api.content.json.ContentPublishItemJson;
    import ResolvePublishContentResultJson = api.content.json.ResolvePublishContentResultJson;

    export class ResolvePublishDependenciesResult {

        dependentContents: ContentId[];
        requestedContents: ContentId[];
        requiredContents: ContentId[];
        containsInvalid: boolean;
        allPublishable: boolean;

        constructor(dependants: ContentId[], requested: ContentId[], required: ContentId[], containsInvalid: boolean,
                    allPublishable: boolean) {
            this.dependentContents = dependants;
            this.requestedContents = requested;
            this.requiredContents = required;
            this.containsInvalid = containsInvalid;
            this.allPublishable = allPublishable;
        }

        getDependants(): ContentId[] {
            return this.dependentContents;
        }

        getRequested(): ContentId[] {
            return this.requestedContents;
        }

        getRequired(): ContentId[] {
            return this.requiredContents;
        }

        isContainsInvalid(): boolean {
            return this.containsInvalid;
        }

        isAllPublishable(): boolean {
            return this.allPublishable;
        }

        static fromJson(json: ResolvePublishContentResultJson): ResolvePublishDependenciesResult {

            let dependants: ContentId[] = json.dependentContents
                ? json.dependentContents.map(dependant => new ContentId(dependant.id))
                : [];
            let requested: ContentId[] = json.requestedContents ? json.requestedContents.map(dependant => new ContentId(dependant.id)) : [];
            let required: ContentId[] = json.requiredContents ? json.requiredContents.map(dependant => new ContentId(dependant.id)) : [];
            let containsInvalid: boolean = json.containsInvalid;
            let allPublishable: boolean = json.allPublishable;

            return new ResolvePublishDependenciesResult(dependants, requested, required, containsInvalid, allPublishable);
        }
    }
}
