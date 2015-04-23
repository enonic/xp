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
            this.addClass("tinymce-editor");
        }

        getValueType(): ValueType {
            return ValueTypes.HTML_PART;
        }

        newInitialValue(): Value {
            return ValueTypes.HTML_PART.newValue("");
        }

        createInputOccurrenceElement(index: number, property: Property): api.dom.Element {
            var focusedEditorCls = "tinymce-editor-focused";
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
                        "styleselect | cut copy pastetext | bullist numlist outdent indent | charmap link unlink | table | code"
                    ],
                    menubar: false,
                    statusbar: false,
                    paste_as_text: true,
                    plugins: ['autoresize', 'table', 'link', 'paste', 'charmap', 'code'],
                    autoresize_min_height: 100,
                    autoresize_bottom_margin: 0,
                    height: 100,

                    setup: (editor) => {
                        editor.on('change', (e) => {
                            var value = this.newValue(this.editor.getContent());
                            property.setValue(value);
                        });
                        editor.on('focus', (e) => {
                            textAreaWrapper.addClass(focusedEditorCls);
                        });
                        editor.on('blur', (e) => {
                            textAreaWrapper.removeClass(focusedEditorCls);
                        });
                    },
                    init_instance_callback: (editor) => {
                        editor.execCommand('mceAutoResize');
                    }
                });

                this.editor = this.getEditor(textAreaEl.getId(), property);
            });

            var textAreaWrapper = new api.dom.DivEl();
            textAreaWrapper.appendChild(textAreaEl);
            return textAreaWrapper;
        }

        private getEditor(editorId: string, property: Property): TinyMceEditor {
            var editor = tinymce.get(editorId);

            if (property.hasNonNullValue()) {
                editor.setContent(property.getString());
            }

            return editor;
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