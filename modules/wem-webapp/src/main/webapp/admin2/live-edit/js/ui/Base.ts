module LiveEdit.ui {
    var $ = $liveedit;

    export class Base {

        static constructedCount:number = 0;

        private element:JQuery;

        constructor() {
        }

        public createElement(htmlString:string):JQuery {
            var id:number = Base.constructedCount++;

            this.element = $(htmlString);
            this.element.attr('id', 'live-edit-ui-cmp-' + id.toString());

            return this.element;
        }

        public appendTo(parent:JQuery):void {
            if (parent.length > 0 && this.element.length > 0) {
                parent.append(this.element);
            }
        }

        public getEl():JQuery {
            return this.element;
        }

    }
}