module api.form.layout {

    export interface LayoutViewConfig {

        context: api.form.FormContext;

        layout:api.form.Layout;

        parent: api.form.formitemset.FormItemSetOccurrenceView;

        className:string
    }

    export class LayoutView extends api.form.FormItemView {

        private layout: api.form.Layout;

        constructor(config: LayoutViewConfig) {
            super(<FormItemViewConfig>{
                className: config.className,
                context: config.context,
                formItem: config.layout,
                parent: config.parent
            });

            this.layout = config.layout;
        }

        getData(): api.data.Data[] {
            return null;
        }

    }
}