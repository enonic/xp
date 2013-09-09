// Globals

///<reference path='../lib/jquery.d.ts' />
///<reference path='../lib/jqueryui.d.ts' />

///<reference path='shared/DomHelper.ts' />
///<reference path='shared/Selection.ts' />
///<reference path='shared/PlaceholderCreator.ts' />

///<reference path='component/ComponentHelper.ts' />
///<reference path='component/Component.ts' />
///<reference path='component/ComponentType.ts' />
///<reference path='component/ComponentInserter.ts' />
///<reference path='component/ComponentResizeObserver.ts' />

///<reference path='component/mouseevent/Base.ts' />
///<reference path='component/mouseevent/Page.ts' />
///<reference path='component/mouseevent/Region.ts' />
///<reference path='component/mouseevent/Layout.ts' />
///<reference path='component/mouseevent/Image.ts' />
///<reference path='component/mouseevent/Part.ts' />
///<reference path='component/mouseevent/Content.ts' />
///<reference path='component/mouseevent/Paragraph.ts' />

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

///<reference path='ui/DragDropSort.ts' />

declare var $liveEdit;

(function ($) {
    'use strict';

    $(window).load(() => {
        // Fade out the loader splash and start the app.
        var loaderSplash:JQuery = $('.live-edit-loader-splash-container');
        loaderSplash.fadeOut('fast', () => {
            loaderSplash.remove();

            new LiveEdit.component.mouseevent.Page();
            new LiveEdit.component.mouseevent.Region();
            new LiveEdit.component.mouseevent.Layout();
            new LiveEdit.component.mouseevent.Part();
            new LiveEdit.component.mouseevent.Image();
            new LiveEdit.component.mouseevent.Paragraph();
            new LiveEdit.component.mouseevent.Content();

            new LiveEdit.component.ComponentResizeObserver();

            // new LiveEdit.ui.HtmlElementReplacer();
            new LiveEdit.ui.Highlighter();
            new LiveEdit.ui.ToolTip();
            new LiveEdit.ui.Cursor();
            new LiveEdit.ui.contextmenu.ContextMenu();
            new LiveEdit.ui.Shader();
            new LiveEdit.ui.Editor();

            LiveEdit.DragDropSort.init();

            $(window).resize(() => $(window).trigger('resizeBrowserWindow.liveEdit'));

            $(window).unload(() => console.log('Clean up any css classes etc. that live edit / sortable has added') );

            console.log('Live Edit Initialized. Using jQuery version: ' + $.fn.jquery);
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
