module api_form_layout {

    export class LayoutView extends api_form.FormItemView {

        private layout:api_form.Layout;

        constructor(context: api_form.FormContext, layout:api_form.Layout, idPrefix:string, className:string) {
            super(idPrefix, className, context, layout);

            this.layout = layout;
        }

        getData():api_data.Data[] {
            return null;
        }

    }
}