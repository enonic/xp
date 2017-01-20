module api.application {

    export class MarketApplicationResponse {

        private applications: MarketApplication[];

        private metadata: MarketApplicationMetadata;

        constructor(contents: MarketApplication[], metadata: MarketApplicationMetadata) {
            this.applications = contents;
            this.metadata = metadata;
        }

        getApplications(): MarketApplication[] {
            return this.applications;
        }

        getMetadata(): MarketApplicationMetadata {
            return this.metadata;
        }

        setApplications(contents: MarketApplication[]): MarketApplicationResponse {
            this.applications = contents;
            return this;
        }

        setMetadata(metadata: MarketApplicationMetadata): MarketApplicationResponse {
            this.metadata = metadata;
            return this;
        }
    }
}
