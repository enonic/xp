module app.launcher {

    export class AppTile extends api.dom.DivEl {
        private app: api.app.Application;
        private countContainer: api.dom.DivEl;

        constructor(application: api.app.Application, index: number) {
            super('app-tile');
            this.getEl().setAttribute("tabindex", (index + 1) + "");
            this.app = application;
            if (this.app.isFullSizeIcon()) {
                this.addClass("fullsize");
            }

            var link = new api.dom.AEl();
            link.setUrl('#/' + application.getId());

            var imgContainer = new api.dom.DivEl('icon-container');

            if (this.app.isFullSizeIcon()) {
                var img = new api.dom.ImgEl(application.getIconUrl());
                imgContainer.appendChild(img);
            } else {
                var icon = new api.dom.IEl("icon-" + application.getIconUrl());
                imgContainer.appendChild(icon);
            }


            var nameContainer = new api.dom.DivEl('name-container');
            nameContainer.getEl().setInnerHtml(application.getName());

            this.countContainer = new api.dom.DivEl('tab-count-container');
            this.showCount();

            link.appendChild(imgContainer);
            link.appendChild(nameContainer);
            link.appendChild(this.countContainer);
            this.appendChild(link);
        }

        showCount() {
            var openTabs = this.app.getOpenTabs();
            this.countContainer.getEl().setInnerHtml('' + openTabs);
            this.countContainer.setVisible(openTabs > 0);
        }

        show() {
            this.showCount();
            super.show();
        }
    }

}
