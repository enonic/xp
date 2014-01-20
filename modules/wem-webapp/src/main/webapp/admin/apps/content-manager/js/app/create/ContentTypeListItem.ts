module app.create {

    export class ContentTypeListItem extends api.dom.LiEl {

        private siteRoot: boolean;

        private contentType: api.schema.content.ContentTypeSummary;

        private iconUrl: string;

        constructor(item: api.schema.content.ContentTypeSummary, siteRoot?: boolean, markRoot?: boolean) {
            super("content-type-list-item");

            this.contentType = item;
            this.siteRoot = siteRoot || false;
            this.iconUrl = item.getIconUrl();

            var img = new api.dom.ImgEl(item.getIconUrl());

            var h6 = new api.dom.H6El();
            h6.getEl().setInnerHtml(item.getDisplayName());
            h6.getEl().setAttribute("title", item.getDisplayName());

            var p = new api.dom.PEl();
            p.getEl().setInnerHtml(item.getName());
            p.getEl().setAttribute("title", item.getName());

            this.appendChild(img);
            this.appendChild(h6);
            this.appendChild(p);

            if (siteRoot && markRoot) {
                this.addClass('site');

                var span = new api.dom.SpanEl();
                span.setClass('overlay');
                this.appendChild(span);
            }
        }

        getName(): string {
            return this.contentType.getName();
        }

        getDisplayName(): string {
            return this.contentType.getDisplayName();
        }

        getIconUrl(): string {
            return this.iconUrl;
        }

        getContentType(): api.schema.content.ContentTypeSummary {
            return this.contentType;
        }

        isSiteRoot(): boolean {
            return this.siteRoot;
        }
    }
}