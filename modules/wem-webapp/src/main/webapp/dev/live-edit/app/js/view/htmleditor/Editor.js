AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view.htmleditor');

(function ($) {
    'use strict';

    var editor = AdminLiveEdit.view.htmleditor.Editor = function () {

        this.toolbar = new AdminLiveEdit.view.htmleditor.Toolbar();

        this.registerGlobalListeners();
    };

    var proto = editor.prototype;

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *


    proto.registerGlobalListeners = function () {
        var me = this;
        $(window).on('component:paragraph:edit:init', function (event, $paragraph) {
            me.activate($paragraph);
        });
        $(window).on('component:paragraph:edit:leave', function (event, $paragraph) {
            me.deActivate($paragraph);
        });
        $(window).on('editor:toolbar:button:click', function (event, tag) {
            // Simplest implementation for now.
            document.execCommand(tag, false, null);
        });
    };


    proto.activate = function ($paragraph) {
        $paragraph.get(0).contentEditable = true;
        $paragraph.get(0).focus();

    };


    proto.deActivate = function ($paragraph) {
        $paragraph.get(0).contentEditable = false;
        $paragraph.get(0).blur();
    };

}($liveedit));

