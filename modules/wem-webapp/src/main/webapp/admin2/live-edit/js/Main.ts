// Globals
declare var $liveedit;
declare var LiveEditMutationSummary;
declare var AdminLiveEdit;

///<reference path='../lib/jquery-1.8.3.d.ts' />
///<reference path='../lib/jqueryui.d.ts' />

///<reference path='helper/DomHelper.ts' />
///<reference path='helper/ComponentHelper.ts' />
///<reference path='helper/MutationObserver.ts' />

///<reference path='ui/DragDropSort.ts' />

///<reference path='model/component/Base.ts' />
///<reference path='model/component/Page.ts' />
///<reference path='model/component/Region.ts' />
///<reference path='model/component/Layout.ts' />
///<reference path='model/component/Part.ts' />
///<reference path='model/component/Content.ts' />
///<reference path='model/component/Paragraph.ts' />

///<reference path='ui/Base.ts' />
///<reference path='ui/HtmlElementReplacer.ts' />
///<reference path='ui/htmleditor/Editor.ts' />
///<reference path='ui/htmleditor/EditorToolbar.ts' />
///<reference path='ui/Shader.ts' />
///<reference path='ui/Cursor.ts' />
///<reference path='ui/Highlighter.ts' />
///<reference path='ui/ToolTip.ts' />

///<reference path='ui/menu/Menu.ts' />
///<reference path='ui/menu/BaseButton.ts' />
///<reference path='ui/menu/ParentButton.ts' />
///<reference path='ui/menu/OpenContentButton.ts' />
///<reference path='ui/menu/InsertButton.ts' />
///<reference path='ui/menu/DetailsButton.ts' />
///<reference path='ui/menu/EditButton.ts' />
///<reference path='ui/menu/ResetButton.ts' />
///<reference path='ui/menu/ClearButton.ts' />
///<reference path='ui/menu/ViewButton.ts' />
///<reference path='ui/menu/SettingsButton.ts' />
///<reference path='ui/menu/RemoveButton.ts' />
///<reference path='ui/componentbar/ComponentBar.ts' />


(function ($) {
    'use strict';

    $(window).load(() => {
        var loaderSplash:JQuery = $('.live-edit-loader-splash-container');

        loaderSplash.fadeOut('fast', function () {
            loaderSplash.remove();

            new LiveEdit.model.Page();
            new LiveEdit.model.Region();
            new LiveEdit.model.Layout();
            new LiveEdit.model.Part();
            new LiveEdit.model.Paragraph();
            new LiveEdit.model.Content();

            new LiveEdit.ui.HtmlElementReplacer();
            new LiveEdit.ui.Highlighter();
            new LiveEdit.ui.ToolTip();
            new LiveEdit.ui.Cursor();
            new LiveEdit.ui.Menu();
            new LiveEdit.ui.Shader();
            new LiveEdit.ui.Editor();
            new LiveEdit.ui.ComponentBar();
            new LiveEdit.MutationObserver();

            AdminLiveEdit.DragDropSort.initialize();

            $(window).resize(() => {
                $(window).trigger('liveEdit.onWindowResize');
            });
        });
    });

    // Prevent the user from clicking on things
    // This needs more work as we want them to click on Live Edit ui stuff.
    $(document).ready(() => {
        $(document).on('mousedown', 'btn, button, a, select', (event) => {
            event.preventDefault();
            return false;
        });
    });

}($liveedit));
