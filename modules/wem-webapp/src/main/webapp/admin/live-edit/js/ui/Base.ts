module LiveEdit.ui {

    // Uses
    var $ = $liveEdit;

    /**
     * Base for all Live Edit UI elements
     */
    export class Base {

        ID_PREFIX:string = 'live-edit-ui-cmp-';

        static constructedCount:number = 0;

        private rootEl:JQuery;

        private id:string;

        constructor() {
            // Create dom id attribute value
            this.id = this.ID_PREFIX + Base.constructedCount++;
        }

        public createHtmlFromString(html:string):JQuery {
            this.rootEl = $(html);
            this.rootEl.attr('id', this.id);

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