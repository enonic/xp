module app_launcher {

    export class AppTile extends api_dom.DivEl {
        private app:Application;

        constructor(application:Application) {
            super(null, 'app-tile');
            this.app = application;
            if (this.app.useFullSizeIcon()) {
                this.addClass("fullsize");
            }

            var link = new api_dom.AEl();
            link.setUrl('#/' + application.getName());

            var imgContainer = new api_dom.DivEl(null, 'img-container');

            var img = new api_dom.ImgEl(application.getIconUrl());
            imgContainer.appendChild(img);

            var nameContainer = new api_dom.DivEl(null, 'name-container');
            nameContainer.getEl().setInnerHtml(application.getName());

            var countContainer = new api_dom.DivEl(null, 'tab-count-container');
            countContainer.getEl().setInnerHtml('' + application.getOpenTabs());
            countContainer.hide();

            link.appendChild(imgContainer);
            link.appendChild(nameContainer);
            link.appendChild(countContainer);
            this.appendChild(link);
        }

    }

}
