module app_view {

    export class AppTile extends api_dom.DivEl {
        private app:app_model.Application;

        constructor(application:app_model.Application) {
            super(null, 'app-tile');
            this.app = application;

            var imgContainer = new api_dom.DivEl(null, 'img-container');

            var img = new api_dom.ImgEl(application.getIconUrl());
            imgContainer.appendChild(img);

            var nameContainer = new api_dom.DivEl(null, 'name-container');
            nameContainer.getEl().setInnerHtml(application.getName());

            var countContainer = new api_dom.DivEl(null, 'tab-count-container');
            countContainer.getEl().setInnerHtml(''+application.getOpenTabs());
            countContainer.hide();

            this.appendChild(imgContainer);
            this.appendChild(nameContainer);
            this.appendChild(countContainer);
        }

    }

}
