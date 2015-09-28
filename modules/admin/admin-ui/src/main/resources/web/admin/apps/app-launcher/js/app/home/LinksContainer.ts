module app.home {

    export class LinksContainer extends api.dom.DivEl {
        private links: {text:string; url:string;}[];

        constructor() {
            super('links-container');
            this.links = [];
        }

        addLink(linkText: string, linkUrl: string): LinksContainer {
            if (this.links.length > 0) {
                this.addSeparator();
            }

            var linkEl = new api.dom.AEl();
            linkEl.setHtml(linkText);
            linkEl.setUrl(linkUrl);
            this.appendChild(linkEl);

            this.links.push({text: linkText, url: linkUrl});

            return this;
        }

        addText(linkText: string): LinksContainer {
            if (this.links.length > 0) {
                this.addSeparator();
            }

            var spanEl = new api.dom.SpanEl();
            spanEl.setHtml(linkText);
            this.appendChild(spanEl);
            this.links.push({text: linkText, url: ''});

            return this;
        }

        private addSeparator() {
            var d = document.createElement('div');
            d.innerHTML = '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;';
            this.getEl().appendChild(d.firstChild);
        }

    }

}
