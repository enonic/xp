module LiveEdit.ui {

    export class BaseComponent extends api.dom.Element {

        constructor(elemntBuilder: api.dom.ElementBuilder) {
            super(elemntBuilder);
            this.addClass('live-edit-ui-cmp');
        }

    }
}