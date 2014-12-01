describe("api.ClassHelperTest", function () {

    function namedFunction() {};
    var anonymousFunction = function () {};

    describe("tests for api.ClassHelper.getFunctionName() function", function () {

        it("returns function name as string", function () {
            expect(api.ClassHelper.getFunctionName(namedFunction)).toBe('namedFunction');
        });

        it("returns empty string for anonymous function", function () {
            expect(api.ClassHelper.getFunctionName(anonymousFunction)).toBe('');
        });

    });

    describe("tests for api.ClassHelper.getClassName() function", function () {

        it("returns class name for object", function () {
            var instance = new api.dom.ElementHelper(document.body);
            expect(api.ClassHelper.getClassName(instance)).toBe("ElementHelper");
        });

        it("should return instance['constructor']['name']", function () {
            expect(api.ClassHelper.getClassName(new function Some() {
            })).toBe("Some");
            expect(api.ClassHelper.getClassName({})).toBe("Object");
        });

    });

    describe("tests for api.ClassHelper.getModuleName() function", function () {

        it("returns full module path for class which given object is instance of", function () {
            var instance = new api.dom.ElementHelper(document.body);
            expect(api.ClassHelper.getModuleName(instance)).toBe("api.dom");
        });

        it("returns full module path for given typescript class", function () {
            expect(api.ClassHelper.getModuleName(api.dom.ElementHelper)).toBe("api.dom");
        });

        it("returns full module path for given exported function", function() {
            expect(api.ClassHelper.getModuleName(api.i18n.setLocale)).toBe('api.i18n');
        });

    });

    describe("tests for api.ClassHelper.getFullName() function", function () {

        it("returns full name for class which given object is instance of", function () {
            var instance = new api.dom.ElementHelper(document.body);
            expect(api.ClassHelper.getFullName(instance)).toBe("api.dom.ElementHelper");
        });

        it("returns full name for given typescript class", function () {
            expect(api.ClassHelper.getFullName(api.dom.ElementHelper)).toBe("api.dom.ElementHelper");
        });

        it("returns full name for given exported function", function () {
            expect(api.ClassHelper.getFullName(api.i18n.setLocale)).toBe('api.i18n.setLocale');
        });

        it("correctly resolves classes with equal names", function () {
            api.$package1 = { Class1: function Class1() {} };
            api.$package2 = { Class1: function Class1() {} };
            expect(api.ClassHelper.getFullName(api.$package1.Class1)).toBe('api.$package1.Class1');
            expect(api.ClassHelper.getFullName(api.$package2.Class1)).toBe('api.$package2.Class1');
        });

        it("returns empty string if function wasn't found", function () {
            expect(api.ClassHelper.getFullName(function Class1() {
            })).toBe('');
        });

    });

});