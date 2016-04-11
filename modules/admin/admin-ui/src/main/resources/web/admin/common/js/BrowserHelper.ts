module api {

    enum BrowserName {
        CHROME, FIREFOX, OPERA, SAFARI, MSIE, TRIDENT
    }

    export class BrowserHelper {

        private static AVAILABLE_VERSIONS = {};

        private static BROWSER_NAME: BrowserName;

        private static BROWSER_VERSION: string;

        private static IS_IE: boolean = false;

        static isAvailableBrowser(): boolean {
            if (!BrowserHelper.BROWSER_NAME) {
                this.init();
            }
            switch (BrowserHelper.BROWSER_NAME) {
            case BrowserName.CHROME:
                if (BrowserHelper.BROWSER_VERSION >= BrowserHelper.AVAILABLE_VERSIONS[BrowserHelper.BROWSER_NAME]) {
                    return true;
                }
                break;
            case BrowserName.FIREFOX:
                if (BrowserHelper.BROWSER_VERSION >= BrowserHelper.AVAILABLE_VERSIONS[BrowserHelper.BROWSER_NAME]) {
                    return true;
                }
                break;
            }
            return false;
        }

        static isOldBrowser(): boolean {
            if (!BrowserHelper.BROWSER_NAME) {
                this.init();
            }
            if (BrowserHelper.AVAILABLE_VERSIONS[BrowserHelper.BROWSER_NAME] > BrowserHelper.BROWSER_VERSION) {
                return true;
            }
            return false;
        }

        static isIE(): boolean {
            if (!BrowserHelper.BROWSER_NAME) {
                this.init();
            }

            return BrowserHelper.IS_IE;
        }

        static isIOS(): boolean {
            return /iPad|iPhone|iPod/.test(navigator.userAgent) && !window['MSStream'];
        }

        static isFirefox(): boolean {
            return navigator.userAgent.toLowerCase().indexOf('firefox') > -1;
        }

        private static init() {
            var M = navigator.userAgent.match(/(opera|chrome|safari|firefox|msie|trident(?=\/))\/?\s*(\d+)/i) || [];
            BrowserHelper.BROWSER_NAME = (<any>BrowserName)[M[1].toLocaleUpperCase()];
            BrowserHelper.BROWSER_VERSION = M[2];

            BrowserHelper.AVAILABLE_VERSIONS[BrowserName.CHROME] = "39";
            BrowserHelper.AVAILABLE_VERSIONS[BrowserName.FIREFOX] = "27";

            BrowserHelper.IS_IE = BrowserHelper.BROWSER_NAME === BrowserName.TRIDENT ||
                                  BrowserHelper.BROWSER_NAME === BrowserName.MSIE ||
                                  navigator.userAgent.indexOf('Edge/') > 0;

        }
    }
}
