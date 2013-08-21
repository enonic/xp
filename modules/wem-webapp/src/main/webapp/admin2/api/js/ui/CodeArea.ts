module api_ui {
    export class CodeArea extends TextArea {
        private options:CodeMirrorOptions = {};
        private codeMirror;
        private mode:string;

        constructor(name:string, mode:string) {
            super(name);
            this.mode = mode;
            CodeMirror.modeURL = "../../resources/lib/codemirror/mode/%N.js";

        }

        afterRender() {
            this.codeMirror = CodeMirror.fromTextArea(<HTMLTextAreaElement>this.getHTMLElement(), this.options);
            this.codeMirror.setSize("550px", "350px");
            this.codeMirror.setOption("mode", this.mode);
            CodeMirror.autoLoadMode(this.codeMirror, this.mode);
        }

        setLineNumbers(value:bool) {
            this.options.lineNumbers = value;
        }

        setValue(value:string) {
            this.codeMirror.setValue(value);
        }

        getValue():string {
            return this.codeMirror.getValue();
        }
    }
}