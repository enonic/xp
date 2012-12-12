/*global CodeMirror */
Ext.define('Admin.lib.CodeMirror', {
    extend: 'Ext.form.field.TextArea',
    alias: 'widget.codemirror',

    codeMirror: null,
    codeMirrorPath: '../../admin/resources/lib/codemirror',
    languageMode: 'xml', // Se codemirror/mode/ for valid modes

    initComponent: function () {
        var me = this;
        me.initialized = false;

        CodeMirror.modeURL = me.codeMirrorPath + '/mode/%N/%N.js';

        me.on({
            resize: function (textArea, width, height, newWidth, newHeight) {
                //Resize logic
                //Should resize both the textarea input element and the codemirror view
            },
            afterrender: function () {
                me.initCodeMirror();
                me.setValue(me.initialConfig.value);
            }
        });

        me.callParent();
    },


    initCodeMirror: function () {
        var me = this;

        me.codeMirror = CodeMirror.fromTextArea(document.getElementById(me.id + '-inputEl'), {
            lineNumbers: true,
            tabSize: 2
        });
        me.codeMirror.setOption('mode', me.languageMode);
        CodeMirror.autoLoadMode(me.codeMirror, me.languageMode);
        me.initialized = true;
    },


    focus: function () {
        this.codeMirror.focus();
    },


    getValue: function () {
        if (this.initialized) {
            return this.codeMirror.getValue();
        }
        return this.initialConfig.value;
    },


    setValue: function (value) {
        if (this.initialized) {
            this.codeMirror.setValue(value);
        }
    }

    //Validate
});