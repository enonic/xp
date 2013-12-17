module api_ui {

    export class CodeAreaBuilder {

        name:string;

        mode:string;

        lineNumbers:boolean;

        size:api_ui.TextAreaSize;

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

        public setSize(value:api_ui.TextAreaSize) : CodeAreaBuilder {
            this.size = value;
            return this;
        }

        public build(): CodeArea {
            return new CodeArea(this);
        }
    }

    export class CodeArea extends TextArea {

        private options: CodeMirrorOptions = {};

        private codeMirror;

        private mode: string;

        constructor(builder:CodeAreaBuilder) {
            super(builder.name);
            this.setSize(builder.size);
            this.mode = builder.mode;
            this.options.lineNumbers = builder.lineNumbers;
            CodeMirror.modeURL = api_util.getUri('admin/common/lib/codemirror/mode/%N.js');
        }

        onElementAddedToParent(parent: api_dom.Element) {

            this.codeMirror = CodeMirror.fromTextArea(<HTMLTextAreaElement>this.getHTMLElement(), this.options);
            this.codeMirror.setSize("550px", "350px");
            this.codeMirror.setOption("mode", this.mode);
            CodeMirror.autoLoadMode(this.codeMirror, this.mode);
            this.codeMirror.refresh();
        }

        afterRender() {
            this.codeMirror.refresh();
        }

        setValue(value: string) {
            this.codeMirror.setValue(value);
        }

        getValue(): string {
            return this.codeMirror.getValue();
        }
    }
}