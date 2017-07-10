module api.content.resource.result {

    import ContentPublishItemJson = api.content.json.ContentPublishItemJson;
    import ResolvePublishContentResultJson = api.content.json.ResolvePublishContentResultJson;

    export class ResolvePublishDependenciesResult {

        dependentContents: ContentId[];
        requestedContents: ContentId[];
        requiredContents: ContentId[];
        containsInvalid: boolean;
        allPublishable: boolean;
        anyPublishable: boolean;

        constructor(builder: Builder) {
            this.dependentContents = builder.dependentContents;
            this.requestedContents = builder.requestedContents;
            this.requiredContents = builder.requiredContents;
            this.containsInvalid = builder.containsInvalid;
            this.allPublishable = builder.allPublishable;
            this.anyPublishable = builder.anyPublishable;
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

        isAnyPublishable(): boolean {
            return this.anyPublishable;
        }

        static fromJson(json: ResolvePublishContentResultJson): ResolvePublishDependenciesResult {

            let dependants: ContentId[] = json.dependentContents
                ? json.dependentContents.map(dependant => new ContentId(dependant.id))
                : [];
            let requested: ContentId[] = json.requestedContents ? json.requestedContents.map(dependant => new ContentId(dependant.id)) : [];
            let required: ContentId[] = json.requiredContents ? json.requiredContents.map(dependant => new ContentId(dependant.id)) : [];
            let containsInvalid: boolean = json.containsInvalid;
            let allPublishable: boolean = json.allPublishable;
            let anyPublishable: boolean = json.anyPublishable;

            return ResolvePublishDependenciesResult.create().setDependentContents(dependants).setRequestedContents(
                requested)
                .setRequiredContents(required)
                .setContainsInvalid(containsInvalid)
                .setAllPublishable(allPublishable)
                .setAnyPublishable(anyPublishable)
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
        anyPublishable: boolean;

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

        setAnyPublishable(value: boolean): Builder {
            this.anyPublishable = value;
            return this;
        }

        build(): ResolvePublishDependenciesResult {
            return new ResolvePublishDependenciesResult(this);
        }
    }
}
