module app.view {
    export class UserItemHeader extends api.dom.DivEl {
        private displayName: api.dom.H2El;
        private subTitle: api.dom.H3El;

        constructor() {
            super("principal-item-header");

            this.displayName = new api.dom.H2El();
            this.subTitle = new api.dom.H3El();

            this.appendChild(this.displayName);
            this.appendChild(this.subTitle);

        }

        setTitle(value: string) {
            this.displayName.getEl().setInnerHtml(value);
        }

        setSubTitle(value: string) {
            this.subTitle.getEl().setInnerHtml(value);
        }
    }
}