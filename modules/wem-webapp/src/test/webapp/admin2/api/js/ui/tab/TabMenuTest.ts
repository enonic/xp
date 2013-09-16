///<reference path='../../TestCase.d.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/jquery.d.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/ExtJs.d.ts' />

///<reference path='../../../../../../../main/webapp/admin2/api/js/ui/PanelNavigationItem.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/ui/DeckPanelNavigator.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/ui/DeckPanelNavigatorListener.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/ui/tab/TabMenuItem.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/ui/tab/TabMenuItemListener.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/ui/tab/TabMenuButton.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/ui/tab/TabMenu.ts' />

TestCase("TabMenu", {

    "test given TabMenu added to DOM when getElementById then element is returned ": function () {

        var tabMenu = new api_ui_tab.TabMenu();
        var tab1 = new api_ui_tab.TabMenuItem("Tab1");
        var tab2 = new api_ui_tab.TabMenuItem("Tab2");
        tabMenu.addNavigationItem(tab1);
        tabMenu.addNavigationItem(tab2);

        document.body.appendChild(tabMenu.getHTMLElement());
        var tabMenuEl = document.getElementById(tabMenu.getId());
        assertNotNull(tabMenuEl);
        assertEquals(tabMenu.getId(), tabMenuEl.id);
    },

    "test given TabMenu with two tabs when getSize then two is returned": function () {

        var tabMenu = new api_ui_tab.TabMenu();
        var tab1 = new api_ui_tab.TabMenuItem("Tab1");
        var tab2 = new api_ui_tab.TabMenuItem("Tab2");
        tabMenu.addNavigationItem(tab1);
        tabMenu.addNavigationItem(tab2);
        assertEquals(2, tabMenu.getSize());
    },

    "test given TabMenu with four tabs when second removed then the indexes of the succeeding tabs is lowered by one": function () {

        // setup
        var tabMenu = new api_ui_tab.TabMenu();
        var tab1 = new api_ui_tab.TabMenuItem("Tab1");
        var tab2 = new api_ui_tab.TabMenuItem("Tab2");
        var tab3 = new api_ui_tab.TabMenuItem("Tab3");
        var tab4 = new api_ui_tab.TabMenuItem("Tab4");
        tabMenu.addNavigationItem(tab1);
        tabMenu.addNavigationItem(tab2);
        tabMenu.addNavigationItem(tab3);
        tabMenu.addNavigationItem(tab4);

        // verify setup
        assertEquals(0, tab1.getIndex());
        assertEquals(1, tab2.getIndex());
        assertEquals(2, tab3.getIndex());
        assertEquals(3, tab4.getIndex());

        // exercise
        tabMenu.removeNavigationItem(tab2);

        // verify
        assertEquals(0, tab1.getIndex());
        assertEquals(1, tab3.getIndex());
        assertEquals(2, tab4.getIndex());
    },

    "test given TabMenu with selected tab when a tab before selected tab is removed then selected index is reduced by one": function () {

        // setup
        var tabMenu = new api_ui_tab.TabMenu();
        var tab1 = new api_ui_tab.TabMenuItem("Tab1");
        var tab2 = new api_ui_tab.TabMenuItem("Tab2");
        var tab3 = new api_ui_tab.TabMenuItem("Tab3");
        var tab4 = new api_ui_tab.TabMenuItem("Tab4");
        tabMenu.addNavigationItem(tab1);
        tabMenu.addNavigationItem(tab2);
        tabMenu.addNavigationItem(tab3);
        tabMenu.addNavigationItem(tab4);
        tabMenu.selectNavigationItem(2);

        // verify setup
        assertEquals(2, tabMenu.getSelectedIndex());

        // exercise
        tabMenu.removeNavigationItem(tab2);

        // verify
        assertEquals(1, tabMenu.getSelectedIndex());
    },

    "test given TabMenu with selected tab when all tabs are removed then no selected": function () {

        // setup
        var tabMenu = new api_ui_tab.TabMenu();
        var tab1 = new api_ui_tab.TabMenuItem("Tab1");
        var tab2 = new api_ui_tab.TabMenuItem("Tab2");
        tabMenu.addNavigationItem(tab1);
        tabMenu.addNavigationItem(tab2);
        tabMenu.selectNavigationItem(1);

        // verify setup
        assertEquals(1, tabMenu.getSelectedIndex());

        // exercise
        tabMenu.removeNavigationItem(tab1);
        tabMenu.removeNavigationItem(tab2);

        // verify
        assertEquals(-1, tabMenu.getSelectedIndex());
    },

    "test given TabMenu with three tabs and last tab is selected when last is removed then the second becomes the selected": function () {

        // setup
        var tabMenu = new api_ui_tab.TabMenu();
        var tab1 = new api_ui_tab.TabMenuItem("Tab1");
        var tab2 = new api_ui_tab.TabMenuItem("Tab2");
        var tab3 = new api_ui_tab.TabMenuItem("Tab3");
        tabMenu.addNavigationItem(tab1);
        tabMenu.addNavigationItem(tab2);
        tabMenu.addNavigationItem(tab3);
        tabMenu.selectNavigationItem(2);

        // verify setup
        assertEquals(2, tabMenu.getSelectedIndex());

        // exercise
        tabMenu.removeNavigationItem(tab3);

        // verify
        assertEquals(1, tabMenu.getSelectedIndex());
    }

});

