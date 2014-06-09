module LiveEdit.ui {

    export class BaseComponent extends api.dom.Element {

        constructor(properties: api.dom.ElementProperties) {
            super(properties);
            this.addClass('live-edit-ui-cmp');
        }

    }
}