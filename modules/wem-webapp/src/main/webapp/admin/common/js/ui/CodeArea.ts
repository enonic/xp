module api.ui {

    export class CodeAreaBuilder {

        name:string;

        mode:string;

        lineNumbers:boolean;

        size:api.ui.TextAreaSize;

        public setName(value:string) : CodeAreaBuilder {
            this.name = value;
            return this;
        }

        public setMode(value:string) : CodeAreaBuilder {
            this.mode = value;
            return this;
        }

        public setLineNumbers(value:boolean) : CodeAreaBuilder {
            this.lineNumbers = value;
            return this;
        }

        public setSize(value:api.ui.TextAreaSize) : CodeAreaBuilder {
            this.size = value;
            return this;
        }

        public build(): CodeArea {
            return new CodeArea(this);
        }
    }

    export class CodeArea extends api.ui.form.CompositeFormInputEl {

        private textArea: api.ui.TextArea;

        private options: CodeMirrorOptions;

        private codeMirror;

        private mode: string;

        constructor(builder:CodeAreaBuilder) {
            this.textArea = new TextArea(builder.name);

            super(this.textArea);

            this.textArea.setSize(builder.size);
            this.mode = builder.mode;
            this.options = {
                lineNumbers: builder.lineNumbers
            };
            CodeMirror.modeURL = api.util.getUri('admin/common/lib/codemirror/mode/%N.js');

            this.onAdded((event) => {
                this.codeMirror = CodeMirror.fromTextArea(<HTMLTextAreaElement>this.textArea.getHTMLElement(), this.options);
                this.codeMirror.setSize("540px", "350px");
                this.codeMirror.setOption("mode", this.mode);
                CodeMirror.autoLoadMode(this.codeMirror, this.mode);
                this.codeMirror.refresh();
            });

            this.onRendered((event) => {
                console.log("CodeMirror rendered");
                this.codeMirror.refresh();
            });

            this.onRemoved((event) => {
                console.log("CodeMirror removed");
            })
        }

        setValue(value: string) {
            this.codeMirror.setValue(value);
        }

        getValue(): string {
            return this.codeMirror.getValue();
        }
    }
}