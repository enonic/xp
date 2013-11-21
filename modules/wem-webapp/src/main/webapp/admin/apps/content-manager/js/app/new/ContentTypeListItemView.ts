module app_new {

    export class ContentTypeListItemView extends api_dom.LiEl {

        constructor(item:ContentTypeListItem) {
            super("ContentTypeListItem", "content-type-list-item");

            var img = new api_dom.ImgEl(item.getIconUrl());

            var h6 = new api_dom.H6El();
            h6.getEl().setInnerHtml(item.getDisplayName());

            var p = new api_dom.PEl();
            p.getEl().setInnerHtml(item.getName());

            this.appendChild(img);
            this.appendChild(h6);
            this.appendChild(p);

            if(item.isSiteRoot()) {
                this.addClass('site');

                var span = new api_dom.SpanEl();
                span.setClass('overlay');
                this.appendChild(span);
            }
        }
    }
}