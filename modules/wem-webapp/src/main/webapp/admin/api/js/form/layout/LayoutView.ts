module api_form_layout {

    export class LayoutView extends api_form.FormItemView {

        private layout:api_form.Layout;

        constructor(layout:api_form.Layout, idPrefix:string, className:string) {
            super(idPrefix, className, layout);

            this.layout = layout;
        }

        getData():api_data.Data[] {
            return null;
        }

    }
}