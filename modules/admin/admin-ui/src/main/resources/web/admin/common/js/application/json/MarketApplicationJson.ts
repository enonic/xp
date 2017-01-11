module api.application.json {

    export interface MarketApplicationJson {

        displayName: string;
        name: string;
        description: string;
        iconUrl: string;
        url: string;
        latestVersion: string;
        versions: Object;
    }
}
