// Globals
declare var $liveEdit;
declare var LiveEditMutationSummary;

///<reference path='../lib/jquery-1.8.3.d.ts' />
///<reference path='../lib/jqueryui.d.ts' />

///<reference path='shared/DomHelper.ts' />
///<reference path='shared/ComponentHelper.ts' />
///<reference path='shared/MutationObserver.ts' />

///<reference path='ui/DragDropSort.ts' />

///<reference path='component/Component.ts' />

///<reference path='component/observer/Base.ts' />
///<reference path='component/observer/Page.ts' />
///<reference path='component/observer/Region.ts' />
///<reference path='component/observer/Layout.ts' />
///<reference path='component/observer/Part.ts' />
///<reference path='component/observer/Content.ts' />
///<reference path='component/observer/Paragraph.ts' />

///<reference path='ui/Base.ts' />
///<reference path='ui/HtmlElementReplacer.ts' />
///<reference path='ui/htmleditor/Editor.ts' />
///<reference path='ui/htmleditor/EditorToolbar.ts' />
///<reference path='ui/Shader.ts' />
///<reference path='ui/Cursor.ts' />
///<reference path='ui/Highlighter.ts' />
///<reference path='ui/ToolTip.ts' />

///<reference path='ui/contextmenu/Menu.ts' />
///<reference path='ui/contextmenu/menuitem/Base.ts' />
///<reference path='ui/contextmenu/menuitem/Parent.ts' />
///<reference path='ui/contextmenu/menuitem/OpenContent.ts' />
///<reference path='ui/contextmenu/menuitem/Insert.ts' />
///<reference path='ui/contextmenu/menuitem/Details.ts' />
///<reference path='ui/contextmenu/menuitem/Edit.ts' />
///<reference path='ui/contextmenu/menuitem/Reset.ts' />
///<reference path='ui/contextmenu/menuitem/Empty.ts' />
///<reference path='ui/contextmenu/menuitem/View.ts' />
///<reference path='ui/contextmenu/menuitem/Remove.ts' />
///<reference path='ui/contextmenu/menuitem/PlayVideo.ts' />

(function ($) {
    'use strict';

    $(window).load(() => {
        // Fade out the loader splash and start the app.
        var loaderSplash:JQuery = $('.live-edit-loader-splash-container');
        loaderSplash.fadeOut('fast', () => {
            loaderSplash.remove();

            new LiveEdit.component.observer.Page();
            new LiveEdit.component.observer.Region();
            new LiveEdit.component.observer.Layout();
            new LiveEdit.component.observer.Part();
            new LiveEdit.component.observer.Paragraph();
            new LiveEdit.component.observer.Content();

            new LiveEdit.ui.HtmlElementReplacer();
            new LiveEdit.ui.Highlighter();
            new LiveEdit.ui.ToolTip();
            new LiveEdit.ui.Cursor();
            new LiveEdit.ui.contextmenu.Menu();
            new LiveEdit.ui.Shader();
            new LiveEdit.ui.Editor();
            new LiveEdit.MutationObserver();

            LiveEdit.DragDropSort.init();

            $(window).resize(() => $(window).trigger('resizeBrowserWindow.liveEdit'));
        });
    });

    // Prevent the user from clicking on things
    // This needs more work as we want them to click on Live Edit ui stuff.
    $(document).ready(() => {
        $(document).on('mousedown', 'btn, button, a, select, input', (event) => {
            event.preventDefault();
            return false;
        });
    });

}($liveEdit));
