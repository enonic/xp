module api.ui.text {

    export class CodeAreaBuilder {

        name: string;

        mode: string;

        lineNumbers: boolean;

        public setName(value: string): CodeAreaBuilder {
            this.name = value;
            return this;
        }

        public setMode(value: string): CodeAreaBuilder {
            this.mode = value;
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

        private codeMirror;

        private mode: string;

        constructor(builder: CodeAreaBuilder) {
            this.textArea = new TextArea(builder.name);

            super(this.textArea);

            this.mode = builder.mode;
            this.options = {
                lineNumbers: builder.lineNumbers
            };
            CodeMirror.modeURL = api.util.UriHelper.getUri('admin/common/lib/codemirror/mode/%N.js');

            this.onAdded((event) => {
                this.codeMirror = CodeMirror.fromTextArea(<HTMLTextAreaElement>this.textArea.getHTMLElement(), this.options);
                this.codeMirror.setSize("540px", "350px");
                this.codeMirror.setOption("mode", this.mode);
                CodeMirror.autoLoadMode(this.codeMirror, this.mode);
                this.codeMirror.refresh();
            });

            this.onShown((event) => {
                this.codeMirror.refresh();
            });
        }

        setValue(value: string): CodeArea {
            this.codeMirror.setValue(value);
            return this;
        }

        getValue(): string {
            return this.codeMirror.getValue();
        }
    }
}