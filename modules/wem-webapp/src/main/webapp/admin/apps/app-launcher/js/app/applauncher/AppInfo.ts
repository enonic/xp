module app.launcher {

    export class AppInfo extends api.dom.DivEl {
        private appInfoName:api.dom.H3El;
        private appInfoDescription:api.dom.DivEl;

        constructor() {
            super(null, 'app-info');

            this.appInfoName = new api.dom.H3El(null, 'app-info-name');
            this.appInfoDescription = new api.dom.DivEl(null, 'app-info-description');

            this.appendChild(this.appInfoName);
            this.appendChild(this.appInfoDescription);
        }

        showAppInfo(application:Application) {
            this.appInfoName.setText(application.getName());
            this.appInfoDescription.getEl().setInnerHtml(application.getDescription());
        }

        hideAppInfo() {
            this.appInfoName.setText('');
            this.appInfoDescription.getEl().setInnerHtml('');
        }
    }

}
