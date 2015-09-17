module api.content.json {

    export interface ResolvedPublishContentJson {

        id: string;

        path: string;

        compareStatus: string;

        displayName: string;

        name: string;

        iconUrl: string;

        type: string;

        valid: boolean;

    }
}