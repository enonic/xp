module app_view {

    export class HomeMainContainerPanel extends api_dom.DivEl {

        private brandingPanel:HomeBrandingPanel;
        private centerPanel:HomeCenterPanel;
        private backgroundImgUrl:string;

        constructor(backgroundImgUrl:string) {
            super(null, 'main-container');

            var style = this.getHTMLElement().style;
            style.left = '0px';
            style.top = '0px';
            this.setBackgroundImgUrl(backgroundImgUrl);

            this.brandingPanel = new HomeBrandingPanel();
            this.centerPanel = new HomeCenterPanel();
            this.appendChild(this.brandingPanel);
            this.appendChild(this.centerPanel);
        }

        getBrandingPanel():HomeBrandingPanel {
            return this.brandingPanel;
        }

        getCenterPanel():HomeCenterPanel {
            return this.centerPanel;
        }

        getBackgroundImgUrl():string {
            return this.backgroundImgUrl;
        }

        setBackgroundImgUrl(backgroundImgUrl:string) {
            this.backgroundImgUrl = backgroundImgUrl;
            this.getHTMLElement().style.backgroundImage = "url('" + backgroundImgUrl + "')";
        }
    }

}
