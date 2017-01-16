module api.ui.text {

    export class CodeAreaBuilder {

        name: string;

        mode: string;

        value: string;

        lineNumbers: boolean;

        public setName(value: string): CodeAreaBuilder {
            this.name = value;
            return this;
        }

        public setMode(value: string): CodeAreaBuilder {
            this.mode = value;
            return this;
        }

        public setValue(value: string): CodeAreaBuilder {
            this.value = value;
            return this;
        }

        public setLineNumbers(value: boolean): CodeAreaBuilder {
            this.lineNumbers = value;
            return this;
        }

        public build(): CodeArea {
            return new CodeArea(this);
        }
    }

    export class CodeArea extends api.dom.CompositeFormInputEl {

        private textArea: api.ui.text.TextArea;

        private options: CodeMirrorOptions;

        private codeMirror: CodeMirrorEditor;

        private mode: string;

        constructor(builder: CodeAreaBuilder) {
            super();

            this.textArea = new TextArea(builder.name, builder.value);

            this.setWrappedInput(this.textArea);

            this.mode = builder.mode;
            this.options = {
                lineNumbers: builder.lineNumbers
            };
            CodeMirror.modeURL = api.util.UriHelper.getAdminUri('common/lib/codemirror/mode/%N.js');

            this.onAdded(() => {
                this.codeMirror = CodeMirror.fromTextArea(<HTMLTextAreaElement>this.textArea.getHTMLElement(), this.options);
                this.codeMirror.setSize(540, 350);
                this.codeMirror.setOption("mode", this.mode);
                CodeMirror.autoLoadMode(this.codeMirror, this.mode);
                this.codeMirror.refresh();
            });

            this.onShown(() => {
                this.codeMirror.refresh();
            });
        }
    }
}
