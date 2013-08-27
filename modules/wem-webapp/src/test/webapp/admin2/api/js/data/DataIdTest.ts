///<reference path='../TestCase.d.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/data/DataId.ts' />

TestCase("DataId", {

    "test getName": function () {

        assertEquals("myName", new api_data.DataId('myName', 0));
    },
    "test getArrayIndex": function () {

        assertEquals(0, new api_data.DataId('myName', 0).getArrayIndex());
        assertEquals(1, new api_data.DataId('myName', 1).getArrayIndex());
        assertEquals(999, new api_data.DataId('myName', 999).getArrayIndex());
    },
    "test toString": function () {

        assertEquals("myName", new api_data.DataId('myName', 0).toString());
        assertEquals("myName[1]", new api_data.DataId('myName', 1).toString());
        assertEquals("myName[999]", new api_data.DataId('myName', 999).toString());

    },
    "test toString when created using from": function () {
        assertEquals("myName", api_data.DataId.from('myName[0]').toString());
        assertEquals("myName[1]", api_data.DataId.from('myName[1]').toString());
        assertEquals("myName[999]", api_data.DataId.from('myName[999]').toString());
    }
});

