module app.launcher {

    export class AppTile extends api.dom.DivEl {
        private app:api.app.Application;

        constructor(application:api.app.Application) {
            super('app-tile');
            this.app = application;
            if (this.app.useFullSizeIcon()) {
                this.addClass("fullsize");
            }

            var link = new api.dom.AEl();
            link.setUrl('#/' + application.getId());

            var imgContainer = new api.dom.DivEl('img-container');

            var img = new api.dom.ImgEl(application.getIconUrl());
            imgContainer.appendChild(img);

            var nameContainer = new api.dom.DivEl('name-container');
            nameContainer.getEl().setInnerHtml(application.getName());

            var countContainer = new api.dom.DivEl('tab-count-container');
            countContainer.getEl().setInnerHtml('' + application.getOpenTabs());
            countContainer.hide();

            link.appendChild(imgContainer);
            link.appendChild(nameContainer);
            link.appendChild(countContainer);
            this.appendChild(link);
        }

    }

}
