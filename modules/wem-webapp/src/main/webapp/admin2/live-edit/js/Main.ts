// Globals
declare var $liveEdit;
declare var LiveEditMutationSummary;

///<reference path='../lib/jquery-1.8.3.d.ts' />
///<reference path='../lib/jqueryui.d.ts' />

///<reference path='shared/DomHelper.ts' />
///<reference path='shared/Selection.ts' />

///<reference path='component/ComponentHelper.ts' />
///<reference path='component/Component.ts' />
///<reference path='component/ComponentType.ts' />

///<reference path='component/listener/Base.ts' />
///<reference path='component/listener/Page.ts' />
///<reference path='component/listener/Region.ts' />
///<reference path='component/listener/Layout.ts' />
///<reference path='component/listener/Image.ts' />
///<reference path='component/listener/Part.ts' />
///<reference path='component/listener/Content.ts' />
///<reference path='component/listener/Paragraph.ts' />

///<reference path='ui/Base.ts' />
///<reference path='ui/htmleditor/Editor.ts' />
///<reference path='ui/htmleditor/EditorToolbar.ts' />
///<reference path='ui/Shader.ts' />
///<reference path='ui/Cursor.ts' />
///<reference path='ui/Highlighter.ts' />
///<reference path='ui/ToolTip.ts' />

///<reference path='ui/contextmenu/ContextMenu.ts' />
///<reference path='ui/contextmenu/menuitem/BaseMenuItem.ts' />
///<reference path='ui/contextmenu/menuitem/ParentMenuItem.ts' />
///<reference path='ui/contextmenu/menuitem/OpenContentMenuItem.ts' />
///<reference path='ui/contextmenu/menuitem/InsertMenuItem.ts' />
///<reference path='ui/contextmenu/menuitem/DetailsMenuItem.ts' />
///<reference path='ui/contextmenu/menuitem/EditMenuItem.ts' />
///<reference path='ui/contextmenu/menuitem/ResetMenuItem.ts' />
///<reference path='ui/contextmenu/menuitem/EmptyMenuItem.ts' />
///<reference path='ui/contextmenu/menuitem/ViewMenuItem.ts' />
///<reference path='ui/contextmenu/menuitem/RemoveMenuItem.ts' />

///<reference path='shared/MutationObserver.ts' />

///<reference path='ui/DragDropSort.ts' />

(function ($) {
    'use strict';

    $(window).load(() => {
        // Fade out the loader splash and start the app.
        var loaderSplash:JQuery = $('.live-edit-loader-splash-container');
        loaderSplash.fadeOut('fast', () => {
            loaderSplash.remove();

            console.log('Init Live Edit. Using jQuery version: ' + $liveEdit.fn.jquery);

            new LiveEdit.component.listener.Page();
            new LiveEdit.component.listener.Region();
            new LiveEdit.component.listener.Layout();
            new LiveEdit.component.listener.Part();
            new LiveEdit.component.listener.Image();
            new LiveEdit.component.listener.Paragraph();
            new LiveEdit.component.listener.Content();

            // new LiveEdit.ui.HtmlElementReplacer();
            new LiveEdit.ui.Highlighter();
            new LiveEdit.ui.ToolTip();
            new LiveEdit.ui.Cursor();
            new LiveEdit.ui.contextmenu.ContextMenu();
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
