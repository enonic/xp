module app_view {

    export class HomeLinksPanel extends api_dom.DivEl {
        private links:{text:string; url:string;}[];

        constructor() {
            super(null, 'links-container');
            this.links = [];
        }

        addLink(linkText:string, linkUrl:string):HomeLinksPanel {
            if (this.links.length > 0) {
                this.addSeparator();
            }

            var linkEl = new api_dom.AEl();
            linkEl.setText(linkText);
            linkEl.setUrl(linkUrl);
            this.appendChild(linkEl);

            this.links.push({text: linkText, url: linkUrl});

            return this;
        }

        private addSeparator() {
            var d = document.createElement('div');
            d.innerHTML = '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;';
            this.getEl().appendChild(d.firstChild);
        }

    }

}
