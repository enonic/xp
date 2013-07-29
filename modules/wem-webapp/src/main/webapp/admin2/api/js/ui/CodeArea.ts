module api_ui {
    export class CodeArea extends TextArea {
        private options:CodeMirrorOptions = {};
        private codeMirror;
        constructor(name:string) {
            super(name);
        }

        afterRender() {
            this.codeMirror = CodeMirror.fromTextArea(<HTMLTextAreaElement>this.getHTMLElement(), this.options);
            this.codeMirror.setSize("550px", "350px");
        }

        setLineNumbers(value:bool) {
            this.options.lineNumbers = value;
        }
    }
}