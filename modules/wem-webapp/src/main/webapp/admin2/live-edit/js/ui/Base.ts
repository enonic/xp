module LiveEdit.ui {
    var $ = $liveEdit;

    export class Base {

        ID_PREFIX:string = 'live-edit-ui-cmp-';

        static constructedCount:number = 0;

        private rootEl:JQuery;
        private id:string;

        constructor() {
            this.id = this.ID_PREFIX + Base.constructedCount++;
        }

        public createElementsFromString(html:string):JQuery {
            this.rootEl = $(html);
            this.rootEl.attr('id', this.id);

            return this.rootEl;
        }

        public appendTo(parent:JQuery):void {
            if (parent.length > 0 && this.rootEl.length > 0) {
                parent.append(this.rootEl);
            }
        }

        public getRootEl():JQuery {
            return this.rootEl;
        }

    }
}