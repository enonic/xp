module app_home {

    export class HomeMainContainer extends api_dom.DivEl {

        private brandingPanel:Branding;
        private centerPanel:CenterPanel;
        private backgroundImgUrl:string;

        constructor(backgroundImgUrl:string) {
            super(null, 'home-main-container');

            var style = this.getHTMLElement().style;
            style.left = '0px';
            style.top = '0px';
            this.setBackgroundImgUrl(backgroundImgUrl);

            this.brandingPanel = new Branding();
            this.centerPanel = new CenterPanel();
            this.appendChild(this.brandingPanel);
            this.appendChild(this.centerPanel);
        }

        getBrandingPanel():Branding {
            return this.brandingPanel;
        }

        getCenterPanel():CenterPanel {
            return this.centerPanel;
        }
    }

}
