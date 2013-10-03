///<reference path='../lib/jasmine.d.ts' />
///<reference path='../../main/js/main.ts' />

describe("UriHelper", () => {

    it("create absolute url", () => {

        var url = api_util.getAbsoluteUri("a/path");
        expect(url).toBe("../../../a/path");

        /*
         console.log(window.location);

         var h1 = document.createElement('h1');
         h1.innerHTML = 'Test';
         document.body.appendChild(h1);
         console.log(document.body.innerHTML);
         */

    });

});
