module api.issue.json {

    export interface PublishRequestJson {

        excludeIds: string[];

        items: PublishRequestItemJson[];
    }
}
