module api.form.layout {

    export class LayoutView extends api.form.FormItemView {

        private layout:api.form.Layout;

        constructor(context: api.form.FormContext, layout:api.form.Layout, idPrefix:string, className:string) {
            super(idPrefix, className, context, layout);

            this.layout = layout;
        }

        getData():api.data.Data[] {
            return null;
        }

    }
}