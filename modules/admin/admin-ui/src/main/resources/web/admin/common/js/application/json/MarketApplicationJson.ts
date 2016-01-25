module api.application.json {

    export interface MarketApplicationJson {

        displayName: string,
        description: string,
        iconUrl: string,
        applicationUrl: string,
        latestVersion: string,
        versions: Object
    }
}