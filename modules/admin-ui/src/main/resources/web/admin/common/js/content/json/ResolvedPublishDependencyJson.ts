module api.content.json {

    export interface ResolvedPublishDependencyJson {

        id: string;

        reasonId: string;

        path: string;

        compareStatus: string;

        displayName: string;

        name: string;

        iconUrl: string;

        type: string;

        isValid: boolean;

    }
}