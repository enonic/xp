module app_view {

    export class AppInfoPanel extends api_dom.DivEl {
        private appInfoName: api_dom.H3El;
        private appInfoDescription: api_dom.DivEl;

        constructor() {
            super(null, 'app-info-container');

            this.appInfoName = new api_dom.H3El(null, 'app-info-name');
            this.appInfoDescription = new api_dom.DivEl(null, 'app-info-description');

            this.appendChild(this.appInfoName);
            this.appendChild(this.appInfoDescription);
        }

        showAppInfo(application: app_model.Application) {
            this.appInfoName.setText(application.getName());
            this.appInfoDescription.getEl().setInnerHtml(application.getDescription());
        }

        hideAppInfo() {
            this.appInfoName.setText('');
            this.appInfoDescription.getEl().setInnerHtml('');
        }
    }

}
