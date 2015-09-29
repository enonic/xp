module api.content.json {

    export interface NewContentPublishItem {

        id: string;

        path: string;

        iconUrl: string;

        displayName: string;

        compareStatus: string;

        name: string;

        type: string;

        valid: boolean;
    }
}