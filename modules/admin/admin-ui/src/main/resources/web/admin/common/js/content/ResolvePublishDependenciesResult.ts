module api.content {

    import ContentPublishItemJson = api.content.json.ContentPublishItemJson;
    import ResolvePublishContentResultJson = api.content.json.ResolvePublishContentResultJson;

    export class ResolvePublishDependenciesResult {

        dependentContents: ContentPublishItem[];
        requestedContents: ContentPublishItem[];

        constructor(dependants: ContentPublishItem[], requested: ContentPublishItem[]) {
            this.dependentContents = dependants;
            this.requestedContents = requested;
        }

        getDependants(): ContentPublishItem[] {
            return this.dependentContents;
        }

        getRequested(): ContentPublishItem[] {
            return this.requestedContents;
        }

        static fromJson(json: ResolvePublishContentResultJson): ResolvePublishDependenciesResult {

            let dependats: ContentPublishItem[] = json.dependentContents.map(dependant => ContentPublishItem.fromJson(dependant));
            let requested: ContentPublishItem[] = json.requestedContents.map(requested => ContentPublishItem.fromJson(requested));

            return new ResolvePublishDependenciesResult(dependats, requested);
        }
    }
}