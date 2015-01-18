module LiveEdit.ui {

    /**
     * Base for all Live Edit UI elements
     */
    export class Base {

        static LIVE_EDIT_UI_COMPONENT:string = 'live-edit-ui-cmp';

        private rootEl:JQuery;

        constructor() {
        }

        public createHtmlFromString(html:string):JQuery {
            this.rootEl = wemjq(html);
            this.rootEl.addClass(Base.LIVE_EDIT_UI_COMPONENT);

            return this.rootEl;
        }

        public appendTo(parent:JQuery):void {
            if (parent.length > 0) {
                parent.append(this.rootEl);
            }
        }

        public getEl():JQuery {
            return this.rootEl;
        }

    }
}