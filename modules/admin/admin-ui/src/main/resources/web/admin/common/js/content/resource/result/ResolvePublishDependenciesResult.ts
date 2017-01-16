module api.content.resource.result {

    import ContentPublishItemJson = api.content.json.ContentPublishItemJson;
    import ResolvePublishContentResultJson = api.content.json.ResolvePublishContentResultJson;

    export class ResolvePublishDependenciesResult {

        dependentContents: ContentId[];
        requestedContents: ContentId[];
        containsRemovable: boolean;
        containsInvalid: boolean;

        constructor(dependants: ContentId[], requested: ContentId[], containsRemovable: boolean, containsInvalid: boolean) {
            this.dependentContents = dependants;
            this.requestedContents = requested;
            this.containsRemovable = containsRemovable;
            this.containsInvalid = containsInvalid;
        }

        getDependants(): ContentId[] {
            return this.dependentContents;
        }

        getRequested(): ContentId[] {
            return this.requestedContents;
        }

        isContainsRemovable(): boolean {
            return this.containsRemovable;
        }

        isContainsInvalid(): boolean {
            return this.containsInvalid;
        }

        static fromJson(json: ResolvePublishContentResultJson): ResolvePublishDependenciesResult {

            let dependants: ContentId[] = json.dependentContents.map(dependant => new ContentId(dependant.id));
            let requested: ContentId[] = json.requestedContents.map(dependant => new ContentId(dependant.id));
            let containsRemovable: boolean = json.containsRemovable;
            let containsInvalid: boolean = json.containsInvalid;

            return new ResolvePublishDependenciesResult(dependants, requested, containsRemovable, containsInvalid);
        }
    }
}
