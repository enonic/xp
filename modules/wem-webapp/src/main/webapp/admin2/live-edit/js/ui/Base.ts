module LiveEdit.ui {
    var $ = $liveedit;

    export class Base {

        static constructedCount:number = 0;

        private ID_PREFIX:string = 'live-edit-ui-cmp-';

        private id:number;

        private element:JQuery;

        constructor() {
            this.id = Base.constructedCount++;
        }

        public createElement(htmlString:string):JQuery {
            this.element = $(htmlString);
            this.element.attr('id', (this.ID_PREFIX + this.id.toString()));

            return this.element;
        }

        public appendTo(parent:JQuery):void {
            if (parent.length > 0 && this.element.length > 0) {
                parent.append(this.element);
            }
        }

        public getRootEl():JQuery {
            return this.element;
        }

    }
}