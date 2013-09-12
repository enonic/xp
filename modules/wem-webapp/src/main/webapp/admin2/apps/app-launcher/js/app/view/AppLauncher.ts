module app_view {

    export class AppLauncher {

        private mainContainer:HomeMainContainer;
        private adminApplicationFrames:api_dom.DivEl;
        private appIframes:{[name: string]: api_dom.IFrameEl;};
        private lostConnectionDetector:app_launcher.LostConnectionDetector;

        constructor(mainContainer:HomeMainContainer) {
            this.mainContainer = mainContainer;
            this.appIframes = {};


            this.adminApplicationFrames = new api_dom.DivEl();
            this.adminApplicationFrames.getEl().setHeight('100%').setWidth('100%');

            var appBridge = new api_app.AppBridge();
            appBridge.addListener({
                onShowLauncher: ()=> {
                    this.showLauncherScreen();
                },
                onConnectionLost: ()=> {
                    new api_notify.showError("Lost connection to server - Please wait until connection is restored");
                }
            });
            this.lostConnectionDetector = new app_launcher.LostConnectionDetector();
            this.lostConnectionDetector.startPolling();
            api_dom.Body.get().appendChild(this.adminApplicationFrames);
        }

        loadApplication(app:app_model.Application) {
            if (!app.getAppUrl()) {
                console.warn('Missing URL for app "' + app.getName() + '". Cannot be opened.');
                return;
            }
            var appName = app.getName();
            var appIframe:api_dom.IFrameEl = this.appIframes[ appName];
            var iFrameExist:boolean = !!appIframe;

            this.mainContainer.hide();
            for (var name in this.appIframes) {
                var iframe:api_dom.IFrameEl = this.appIframes[name];
                if (iframe.getEl().getAttribute('data-wem-app') !== appName) {
                    iframe.hide();
                }
            }

            if (iFrameExist) {
                appIframe.show();
            } else {
                appIframe = this.createIframe(app.getAppUrl(), appName);
                this.adminApplicationFrames.appendChild(appIframe);
                this.showLoadMask();
                this.appIframes[appName] = appIframe;
            }
        }

        private showLauncherScreen() {
            for (var name in this.appIframes) {
                var iframe:api_dom.IFrameEl = this.appIframes[name];
                iframe.hide();
            }
            this.mainContainer.show();
        }

        private showLoadMask() {
            // TODO implement loadMask
        }

        private createIframe(url:string, name:string):api_dom.IFrameEl {
            var iframe = new api_dom.IFrameEl();
            iframe.getEl().setHeight('100%').setWidth('100%').getHTMLElement().style.border = '0';
            iframe.setSrc(url);
            iframe.getEl().setAttribute('data-wem-app', name);
            return iframe;
        }
    }
}
