///<reference path='../../TestCase.d.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/jquery.d.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/ExtJs.d.ts' />

///<reference path='../../../../../../../main/webapp/admin2/api/js/event/Event.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/event/EventBus.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/dom/ElementHelper.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/dom/Element.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/dom/DivEl.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/dom/ButtonEl.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/dom/SpanEl.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/dom/UlEl.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/dom/LiEl.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/ui/Panel.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/ui/DeckPanel.ts' />

TestCase("DecPanel", {

    "test given DeckPanel with three panels and last panel is shown when last is removed then the second becomes the shown": function () {

        // setup
        var deckPanel = new api_ui.DeckPanel();
        var panel1 = new api_ui.Panel();
        var panel2 = new api_ui.Panel();
        var panel3 = new api_ui.Panel();
        deckPanel.addPanel(panel1);
        deckPanel.addPanel(panel2);
        deckPanel.addPanel(panel3);
        deckPanel.showPanel(2);

        // verify setup
        assertEquals(2, deckPanel.getPanelShownIndex());
        assertEquals(panel3, deckPanel.getPanelShown());

        // exercise
        deckPanel.removePanelByIndex(2);

        // verify
        assertEquals(1, deckPanel.getPanelShownIndex());
        assertEquals(panel2, deckPanel.getPanelShown());
    }

});

