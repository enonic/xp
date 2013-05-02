// Globals
declare var $liveedit;
declare var LiveEditMutationSummary;
declare var AdminLiveEdit;

///<reference path='../lib/jquery-1.8.3.d.ts' />
///<reference path='../lib/jqueryui.d.ts' />

///<reference path='namespace.ts' />
///<reference path='Util.ts' />
///<reference path='MutationObserver.ts' />
///<reference path='DragDropSort.ts' />
///<reference path='PageLeave.ts' />

///<reference path='model/component/Base.ts' />
///<reference path='model/component/Page.ts' />
///<reference path='model/component/Region.ts' />
///<reference path='model/component/Layout.ts' />
///<reference path='model/component/Part.ts' />
///<reference path='model/component/Content.ts' />
///<reference path='model/component/Paragraph.ts' />
///<reference path='view/Base.ts' />
///<reference path='view/HtmlElementReplacer.ts' />
///<reference path='view/htmleditor/Editor.ts' />
///<reference path='view/htmleditor/Toolbar.ts' />
///<reference path='view/Shader.ts' />
///<reference path='view/Cursor.ts' />

///<reference path='view/Highlighter.ts' />
///<reference path='view/ToolTip.ts' />
///<reference path='view/menu/Menu.ts' />
///<reference path='view/menu/BaseButton.ts' />
///<reference path='view/menu/ParentButton.ts' />
///<reference path='view/menu/OpenContentButton.ts' />
///<reference path='view/menu/InsertButton.ts' />
///<reference path='view/menu/DetailsButton.ts' />
///<reference path='view/menu/EditButton.ts' />
///<reference path='view/menu/ResetButton.ts' />
///<reference path='view/menu/ClearButton.ts' />
///<reference path='view/menu/ViewButton.ts' />
///<reference path='view/menu/SettingsButton.ts' />
///<reference path='view/menu/RemoveButton.ts' />
///<reference path='view/componentbar/ComponentBar.ts' />

(function ($) {
    'use strict';

    $(window).load(function () {

        $('.live-edit-loader-splash-container').fadeOut('fast', function () {
            $(this).remove();

            new AdminLiveEdit.model.component.Page();
            new AdminLiveEdit.model.component.Region();
            new AdminLiveEdit.model.component.Layout();
            new AdminLiveEdit.model.component.Part();
            new AdminLiveEdit.model.component.Content();
            new AdminLiveEdit.model.component.Paragraph();

            new AdminLiveEdit.view.HtmlElementReplacer();
            new AdminLiveEdit.view.Highlighter();
            new AdminLiveEdit.view.ToolTip();
            new AdminLiveEdit.view.Cursor();
            new AdminLiveEdit.view.menu.Menu();
            new AdminLiveEdit.view.Shader();
            new AdminLiveEdit.view.htmleditor.Editor();
            new AdminLiveEdit.view.componentbar.ComponentBar();
            new AdminLiveEdit.MutationObserver();

            AdminLiveEdit.DragDropSort.initialize();

            $(window).resize(function () {
                $(window).trigger('liveEdit.onWindowResize');
            });
        });

    });

    $(document).ready(function () {

        $(document).on('mousedown', 'btn, button, a, select', function (event) {
            event.preventDefault();
            return false;
        });
    });

}($liveedit));