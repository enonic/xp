///<reference path='../../TestCase.d.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/jquery.d.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/ExtJs.d.ts' />

///<reference path='../../../../../../../main/webapp/admin2/api/js/dom/ElementHelper.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/dom/Element.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/dom/DivEl.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/dom/ButtonEl.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/dom/SpanEl.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/dom/UlEl.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/dom/LiEl.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/ui/tab/Tab.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/ui/tab/TabNavigator.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/ui/tab/TabMenuItem.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/ui/tab/TabMenuButton.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/ui/tab/TabMenu.ts' />

TestCase("TabMenu", {

    "test given TabMenu added to DOM when getElementById then element is returned ": function () {

        var tabMenu = new api_ui_tab.TabMenu();
        var tab1 = new api_ui_tab.TabMenuItem("Tab1");
        var tab2 = new api_ui_tab.TabMenuItem("Tab2");
        tabMenu.addTab(tab1);
        tabMenu.addTab(tab2);

        document.body.appendChild(tabMenu.getHTMLElement());
        var tabMenuEl = document.getElementById(tabMenu.getId());
        assertNotNull(tabMenuEl);
        assertEquals(tabMenu.getId(), tabMenuEl.id);
    },

    "test given TabMenu with two tabs when getSize then two is returned": function () {

        var tabMenu = new api_ui_tab.TabMenu();
        var tab1 = new api_ui_tab.TabMenuItem("Tab1");
        var tab2 = new api_ui_tab.TabMenuItem("Tab2");
        tabMenu.addTab(tab1);
        tabMenu.addTab(tab2);
        assertEquals(2, tabMenu.getSize());
    },

    "test given TabMenu with four tabs when second removed then the indexes of the succeeding tabs is lowered by one": function () {

        // setup
        var tabMenu = new api_ui_tab.TabMenu();
        var tab1 = new api_ui_tab.TabMenuItem("Tab1");
        var tab2 = new api_ui_tab.TabMenuItem("Tab2");
        var tab3 = new api_ui_tab.TabMenuItem("Tab3");
        var tab4 = new api_ui_tab.TabMenuItem("Tab4");
        tabMenu.addTab(tab1);
        tabMenu.addTab(tab2);
        tabMenu.addTab(tab3);
        tabMenu.addTab(tab4);

        // verify setup
        assertEquals(0, tab1.getTabIndex());
        assertEquals(1, tab2.getTabIndex());
        assertEquals(2, tab3.getTabIndex());
        assertEquals(3, tab4.getTabIndex());

        // exercise
        tabMenu.removeTab(tab2);

        // verify
        assertEquals(0, tab1.getTabIndex());
        assertEquals(1, tab3.getTabIndex());
        assertEquals(2, tab4.getTabIndex());
    },

    "test given TabMenu with three tabs and last tab is selected when last is removed then the second becomes the selected": function () {

        // setup
        var tabMenu = new api_ui_tab.TabMenu();
        var tab1 = new api_ui_tab.TabMenuItem("Tab1");
        var tab2 = new api_ui_tab.TabMenuItem("Tab2");
        var tab3 = new api_ui_tab.TabMenuItem("Tab3");
        tabMenu.addTab(tab1);
        tabMenu.addTab(tab2);
        tabMenu.addTab(tab3);
        tabMenu.selectTab(2);

        // verify setup
        assertEquals(2, tabMenu.getSelectedTabIndex());

        // exercise
        tabMenu.removeTab(tab3);

        // verify
        assertEquals(1, tabMenu.getSelectedTabIndex());
    }

});

