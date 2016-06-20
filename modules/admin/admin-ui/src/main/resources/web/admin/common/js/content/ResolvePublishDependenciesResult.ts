module api.content {

    import ContentPublishItemJson = api.content.json.ContentPublishItemJson;
    import ResolvePublishContentResultJson = api.content.json.ResolvePublishContentResultJson;

    export class ResolvePublishDependenciesResult {

        dependentContents: ContentPublishItem[];
        requestedContents: ContentPublishItem[];
        metadata: ContentMetadata;
        containsRemovable: boolean;


        constructor(dependants: ContentPublishItem[], requested: ContentPublishItem[], metadata: ContentMetadata, containsRemovable: boolean) {
            this.dependentContents = dependants;
            this.requestedContents = requested;
            this.metadata = metadata;
            this.containsRemovable = containsRemovable;
        }

        getDependants(): ContentPublishItem[] {
            return this.dependentContents;
        }

        getRequested(): ContentPublishItem[] {
            return this.requestedContents;
        }

        getMetadata(): ContentMetadata {
            return this.metadata;
        }

        isContainsRemovable(): boolean {
            return this.containsRemovable;
        }

        static fromJson(json: ResolvePublishContentResultJson): ResolvePublishDependenciesResult {

            let dependants: ContentPublishItem[] = json.dependentContents.map(dependant => ContentPublishItem.fromJson(dependant));
            let requested: ContentPublishItem[] = json.requestedContents.map(requested => ContentPublishItem.fromJson(requested));
            let metadata: ContentMetadata = new ContentMetadata(json.metadata["hits"], json.metadata["totalHits"]);
            let containsRemovable: boolean = json.containsRemovable;

            return new ResolvePublishDependenciesResult(dependants, requested, metadata, containsRemovable);
        }
    }
}