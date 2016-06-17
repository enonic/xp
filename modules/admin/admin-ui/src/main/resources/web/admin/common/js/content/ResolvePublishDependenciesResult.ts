module api.content {

    import ContentPublishItemJson = api.content.json.ContentPublishItemJson;
    import ResolvePublishContentResultJson = api.content.json.ResolvePublishContentResultJson;

    export class ResolvePublishDependenciesResult {

        dependentContents: ContentIds;
        requestedContents: ContentIds;
        containsRemovable: boolean;


        constructor(dependants: ContentIds, requested: ContentIds, containsRemovable: boolean) {
            this.dependentContents = dependants;
            this.requestedContents = requested;
            this.containsRemovable = containsRemovable;
        }

        getDependants(): ContentIds {
            return this.dependentContents;
        }

        getRequested(): ContentIds {
            return this.requestedContents;
        }

        isContainsRemovable(): boolean {
            return this.containsRemovable;
        }

        static fromJson(json: ResolvePublishContentResultJson): ResolvePublishDependenciesResult {

            let dependants: ContentIds = ContentIds.from(json.dependentContents.map(dependant => new ContentId(dependant.id)));
            let requested: ContentIds = ContentIds.from(json.requestedContents.map(dependant => new ContentId(dependant.id)));
            let containsRemovable: boolean = json.containsRemovable;

            return new ResolvePublishDependenciesResult(dependants, requested, containsRemovable);
        }
    }
}