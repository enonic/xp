module api.form {

    export interface LayoutViewConfig {

        context: FormContext;

        layout:Layout;

        parent: FormItemSetOccurrenceView;

        className:string
    }

    export class LayoutView extends FormItemView {

        private _layout: Layout;

        constructor(config: LayoutViewConfig) {
            super(<FormItemViewConfig>{
                className: config.className,
                context: config.context,
                formItem: config.layout,
                parent: config.parent
            });

            this._layout = config.layout;
        }

    }
}