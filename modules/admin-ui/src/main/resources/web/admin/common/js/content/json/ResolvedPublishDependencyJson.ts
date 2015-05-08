module api.content.json {

    export interface ResolvedPublishDependencyJson {

        id: string;

        initialReasonId: string;

        path: string;

        compareStatus: string;

        displayName: string;

        name: string;

        iconUrl: string;

        type: string;

        isValid: boolean;

    }
}