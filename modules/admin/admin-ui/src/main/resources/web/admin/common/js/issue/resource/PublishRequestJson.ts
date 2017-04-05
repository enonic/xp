module api.issue.resource {

    export interface PublishRequestJson {

        excludeIds: string[];

        items: PublishRequestItemJson[];
    }
}
