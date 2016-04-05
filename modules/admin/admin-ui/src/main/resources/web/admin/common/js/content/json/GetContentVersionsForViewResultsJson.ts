module api.content.json {

    export interface GetContentVersionsForViewResultsJson {

        from: number;

        size: number;

        hits: number;

        totalHits: number;

        activeVersion: ActiveContentVersionJson;

        contentVersions: ContentVersionViewJson[];
    }
}

