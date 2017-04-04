module api.content.resource.result {

    import ContentPublishItemJson = api.content.json.ContentPublishItemJson;
    import ResolvePublishContentResultJson = api.content.json.ResolvePublishContentResultJson;

    export class ResolvePublishDependenciesResult {

        dependentContents: ContentId[];
        requestedContents: ContentId[];
        requiredContents: ContentId[];
        containsInvalid: boolean;

        constructor(builder: Builder) {
            this.dependentContents = builder.dependentContents;
            this.requestedContents = builder.requestedContents;
            this.requiredContents = builder.requiredContents;
            this.containsInvalid = builder.containsInvalid;
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

        static fromJson(json: ResolvePublishContentResultJson): ResolvePublishDependenciesResult {

            let dependants: ContentId[] = json.dependentContents
                ? json.dependentContents.map(dependant => new ContentId(dependant.id))
                : [];
            let requested: ContentId[] = json.requestedContents ? json.requestedContents.map(dependant => new ContentId(dependant.id)) : [];
            let required: ContentId[] = json.requiredContents ? json.requiredContents.map(dependant => new ContentId(dependant.id)) : [];
            let containsInvalid: boolean = json.containsInvalid;

            return ResolvePublishDependenciesResult.create().setDependentContents(dependants).setRequestedContents(
                requested)
                .setRequiredContents(required)
                .setContainsInvalid(containsInvalid)
                .build();
        }

        static create(): Builder {
            return new Builder();
        }
    }
    export class Builder {
        dependentContents: ContentId[];
        requestedContents: ContentId[];
        requiredContents: ContentId[];
        containsInvalid: boolean;
        allPublishable: boolean;

        setDependentContents(value: ContentId[]): Builder {
            this.dependentContents = value;
            return this;
        }

        setRequestedContents(value: ContentId[]): Builder {
            this.requestedContents = value;
            return this;
        }

        setRequiredContents(value: ContentId[]): Builder {
            this.requiredContents = value;
            return this;
        }

        setContainsInvalid(value: boolean): Builder {
            this.containsInvalid = value;
            return this;
        }

        setAllPublishable(value: boolean): Builder {
            this.allPublishable = value;
            return this;
        }

        build(): ResolvePublishDependenciesResult {
            return new ResolvePublishDependenciesResult(this);
        }
    }
}
