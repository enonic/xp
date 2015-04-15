module api.form.inputtype.text {

    declare var CONFIG;

    import support = api.form.inputtype.support;
    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;

    export class TinyMCE extends support.BaseInputTypeNotManagingAdd<any,string> {

        private editor: TinyMceEditor;

        constructor(config: api.form.inputtype.InputTypeViewContext<any>) {
            super(config);
        }

        getValueType(): ValueType {
            return ValueTypes.HTML_PART;
        }

        newInitialValue(): Value {
            return ValueTypes.HTML_PART.newValue("");
        }

        createInputOccurrenceElement(index: number, property: Property): api.dom.Element {

            var textAreaEl = new api.ui.text.TextArea(this.getInput().getName() + "-" + index);

            var clazz = textAreaEl.getId().replace(/\./g, '_');
            textAreaEl.addClass(clazz);
            var baseUrl = CONFIG.assetsUri;

            textAreaEl.onRendered(() => {
                tinymce.init({
                    selector: 'textarea.' + clazz,
                    document_base_url: baseUrl + '/common/lib/tinymce/',
                    skin_url: baseUrl + '/common/lib/tinymce/skins/lightgray',
                    theme_url: 'modern',
                    toolbar: [
                        "undo redo | bold italic underline strikethrough | alignleft aligncenter alignright alignjustify | styleselect",
                        "cut copy paste | bullist numlist outdent indent | fontselect fontsizeselect"
                    ],
                    menubar: false,
                    statusbar: false,
                    plugins: 'autoresize',
                    autoresize_min_height: 100,
                    autoresize_max_height: 400,
                    autoresize_bottom_margin: 0,
                    height: 100,

                    setup: (editor) => {
                        editor.on('change', (e) => {
                            var value = this.newValue(this.editor.getContent());
                            property.setValue(value);
                        });
                    },
                    init_instance_callback: (editor) => {
                        editor.execCommand('mceAutoResize');
                    }
                });

                this.editor = tinymce.get(textAreaEl.getId());
                if (property.hasNonNullValue()) {
                    this.editor.setContent(property.getString());
                }
            });

            var textAreaWrapper = new api.dom.DivEl();
            textAreaWrapper.appendChild(textAreaEl);
            return textAreaWrapper;
        }

        private newValue(s: string): Value {
            return new Value(s, ValueTypes.HTML_PART);
        }

        valueBreaksRequiredContract(value: Value): boolean {
            return value.isNull() || !value.getType().equals(ValueTypes.HTML_PART) ||
                api.util.StringHelper.isBlank(value.getString());
        }

        hasInputElementValidUserInput(inputElement: api.dom.Element) {

            // TODO
            return true;
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class("TinyMCE", TinyMCE));
}