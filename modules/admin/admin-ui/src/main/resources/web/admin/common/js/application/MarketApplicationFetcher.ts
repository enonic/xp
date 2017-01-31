module api.application {

    export class MarketApplicationsFetcher {

        static fetchChildren(version: string, installedApplications: Application[], from: number = 0,
                             size: number = -1): wemQ.Promise<MarketApplicationResponse> {
            return new api.application.ListMarketApplicationsRequest()
                .setStart(from)
                .setCount(size)
                .setVersion(version)
                .sendAndParse()
                .then((response: MarketApplicationResponse)=> {
                    let applications = response.getApplications();
                    applications.forEach((marketApp) => {
                        for (let i = 0; i < installedApplications.length; i++) {
                            if (marketApp.getAppKey().equals(installedApplications[i].getApplicationKey())) {
                                if (this.installedAppCanBeUpdated(marketApp, installedApplications[i])) {
                                    marketApp.setStatus(MarketAppStatus.OLDER_VERSION_INSTALLED);
                                } else {
                                    marketApp.setStatus(MarketAppStatus.INSTALLED);
                                }
                                break;
                            }
                        }
                    });

                    return response;
                });
        }

        static installedAppCanBeUpdated(marketApp: MarketApplication, installedApp: Application): boolean {
            return this.compareVersionNumbers(marketApp.getLatestVersion(), installedApp.getVersion()) > 0;
        }

        private static compareVersionNumbers(v1: string, v2: string): number {
            let v1parts = v1.split('.');
            let v2parts = v2.split('.');

            for (let i = 0; i < v1parts.length; ++i) {
                if (v2parts.length === i) {
                    return 1;
                }

                if (v1parts[i] === v2parts[i]) {
                    continue;
                }
                if (v1parts[i] > v2parts[i]) {
                    return 1;
                }
                return -1;
            }

            if (v1parts.length !== v2parts.length) {
                return -1;
            }

            return 0;
        }
    }
}
