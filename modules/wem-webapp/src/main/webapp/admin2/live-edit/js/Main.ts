// Globals
declare var $liveedit;
declare var LiveEditMutationSummary;
declare var AdminLiveEdit;

///<reference path='../lib/jquery-1.8.3.d.ts' />
///<reference path='../lib/jqueryui.d.ts' />

///<reference path='namespace.ts' />
///<reference path='helper/DomHelper.ts' />
///<reference path='helper/ComponentHelper.ts' />
///<reference path='MutationObserver.ts' />
///<reference path='DragDropSort.ts' />

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
///<reference path='ui/htmleditor/Toolbar.ts' />
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