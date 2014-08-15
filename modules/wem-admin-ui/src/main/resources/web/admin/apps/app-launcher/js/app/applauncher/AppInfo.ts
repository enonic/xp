module app.launcher {

    export class AppInfo extends api.dom.DivEl {
        private appInfoName: api.dom.H3El;
        private appInfoDescription: api.dom.DivEl;

        constructor() {
            super('app-info');

            this.appInfoName = new api.dom.H3El('app-info-name');
            this.appInfoDescription = new api.dom.DivEl('app-info-description');

            this.appendChild(this.appInfoName);
            this.appendChild(this.appInfoDescription);
        }

        showAppInfo(application: api.app.Application) {
            this.appInfoName.setHtml(application.getName());
            this.appInfoDescription.getEl().setInnerHtml(application.getDescription());
        }

        hideAppInfo() {
            this.appInfoName.setHtml('');
            this.appInfoDescription.getEl().setInnerHtml('');
        }
    }

}
