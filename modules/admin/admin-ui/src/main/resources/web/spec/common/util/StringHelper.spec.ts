import StringHelper = api.util.StringHelper;

describe("api.util.StringHelper", () => {

    var sh = StringHelper;

    describe("limit", () => {

        var s = "Lorem ipsum 123% !";

        it("should return empty string if invalid arguments are passed", () => {
            expect(sh.limit(null, 11)).toBe("");
            expect(sh.limit("", 11)).toBe("");
        });
        it("should add ellipsis at the end by default", () => {
            expect(sh.limit(s, 11)).toBe(s.substr(0, 11) + "\u2026");
        });
        it("should allow to specify trailing char", () => {
            expect(sh.limit(s, 11, "!")).toBe(s.substr(0, 11) + "!");
        })

    });

    describe("capitalize", () => {

        it("should return empty string if invalid arguments are passed", () => {
            expect(sh.capitalize(null)).toBe("");
            expect(sh.capitalize("")).toBe("");
        });
        it("should capitalize just the first letter", () => {
            var s = "lorem ipsum 123% !";
            expect(sh.capitalize(s)).toBe(s.charAt(0).toUpperCase() + s.substr(1));
        });

    });

    describe("capitalizeAll", () => {

        it("should return empty string if invalid arguments are passed", () => {
            expect(sh.capitalizeAll(null)).toBe("");
            expect(sh.capitalizeAll("")).toBe("");
        });
        it("should capitalize first letters of every word", () => {
            expect(sh.capitalizeAll("lorem ipsum 123% !")).toBe("Lorem Ipsum 123% !");
        });

    });

    describe("isUpperCase", () => {

        it("should return false if invalid arguments are passed", () => {
            expect(sh.isUpperCase(null)).toBe(false);
            expect(sh.isUpperCase("")).toBe(false);
        });
        it("should be true if all letters are uppercase", () => {
            expect(sh.isUpperCase("LOREM IPSUM  123% !")).toBe(true);
        });
        it("should be false if not all letters are uppercase", () => {
            expect(sh.isUpperCase("LOReM IPSUM  123% !")).toBe(false);
        });
    });

    describe("isLowerCase", () => {

        it("should return false if invalid arguments are passed", () => {
            expect(sh.isLowerCase(null)).toBe(false);
            expect(sh.isLowerCase("")).toBe(false);
        });
        it("should be true if all letters are uppercase", () => {
            expect(sh.isLowerCase("lorem ipsum  123% !")).toBe(true);
        });
        it("should be false if not all letters are uppercase", () => {
            expect(sh.isLowerCase("lorem Ipsum  123% !")).toBe(false);
        });
    });

    describe("isMixedCase", () => {

        it("should return false if invalid arguments are passed", () => {
            expect(sh.isMixedCase(null)).toBe(false);
            expect(sh.isMixedCase("")).toBe(false);
        });
        it("should be true if value contains both cases", () => {
            expect(sh.isMixedCase("lorem ipsUm  123% !")).toBe(true);
        });
        it("should be false if all letters either lower or upper case", () => {
            expect(sh.isMixedCase("lorem ipsum  123% !")).toBe(false);
            expect(sh.isMixedCase("LOREM IPSUM  123% !")).toBe(false);
        });
    });

    describe("isEmpty", () => {

        it("should be true for null", () => {
            expect(sh.isEmpty(null)).toBeTruthy();
        });
        it("should be true for empty string", () => {
            expect(sh.isEmpty("")).toBeTruthy();
        });
        it("should be false for \' \' string", () => {
            expect(sh.isEmpty(" ")).toBeFalsy();
        });
        it("should be false for \'bob\'", () => {
            expect(sh.isEmpty("bob")).toBeFalsy();
        });
        it("should be false for \'  bob  \'", () => {
            expect(sh.isEmpty("  bob  ")).toBeFalsy();
        });

    });

    describe("isBlank", () => {

        it("should be true for null", () => {
            expect(sh.isBlank(null)).toBeTruthy();
        });
        it("should be true for empty string", () => {
            expect(sh.isBlank("")).toBeTruthy();
        });
        it("should be true for \' \' string", () => {
            expect(sh.isBlank(" ")).toBeTruthy();
        });
        it("should be false for \'bob\'", () => {
            expect(sh.isBlank("bob")).toBeFalsy();
        });
        it("should be false for \'  bob  \'", () => {
            expect(sh.isBlank("  bob  ")).toBeFalsy();
        });

    });

    describe("escapeHtml", () => {

        it("should be empty for null", () => {
            expect(sh.escapeHtml(null)).toBe("");
        });
        it("should be empty for empty", () => {
            expect(sh.escapeHtml("")).toBe("");
        });
        it("should '&amp;' empty for '&'", () => {
            expect(sh.escapeHtml("&")).toBe("&amp;");
        });
        it("should '&lt;' empty for '<'", () => {
            expect(sh.escapeHtml("<")).toBe("&lt;");
        });
        it("should '&gt;' empty for '>'", () => {
            expect(sh.escapeHtml(">")).toBe("&gt;");
        });
        it("should '&quot;' empty for '\"'", () => {
            expect(sh.escapeHtml("\"")).toBe("&quot;");
        });
        it("should '&#x2F;' empty for '\/'", () => {
            expect(sh.escapeHtml("\/")).toBe("&#x2F;");
        });

    });

    describe("removeCarriageChars", () => {

        it("should return empty string if invalid arguments are passed", () => {
            expect(sh.removeCarriageChars(null)).toBe("");
            expect(sh.removeCarriageChars("")).toBe("");
        });
        it("should remove just carriage chars", () => {
            expect(sh.removeCarriageChars("Lorem \ripsumr 123\r% !")).toBe("Lorem ipsumr 123% !");
        });

    });

    describe("removeCarriageChars", () => {

        it("should return empty string if invalid arguments are passed", () => {
            expect(sh.removeWhitespaces(null)).toBe("");
            expect(sh.removeWhitespaces("")).toBe("");
        });
        it("should remove just whitespace chars", () => {
            expect(sh.removeWhitespaces("Lorem   ipsum 123% !")).toBe("Loremipsum123%!");
        });

    });

    describe("removeCarriageChars", () => {

        it("should return empty array if invalid arguments are passed", () => {
            expect(sh.removeEmptyStrings(null)).toEqual([]);
            expect(sh.removeEmptyStrings([])).toEqual([]);
        });
        it("should remove null and empty strings from array", () => {
            expect(sh.removeEmptyStrings(["Lorem", null, " ipsum ", "", "123% ", " !"])).toEqual(["Lorem", " ipsum ", "123% ", " !"]);
        });

    });

    describe("substringBetween", () => {

        var s = "to be or not to eat bee ?";

        it("should return empty string if invalid arguments are passed", () => {
            expect(sh.substringBetween("abc", null, "c")).toBe("");
            expect(sh.substringBetween("abc", "a", null)).toBe("");
            expect(sh.substringBetween(null, "a", "c")).toBe("");
            expect(sh.substringBetween("abc", "", "c")).toBe("");
            expect(sh.substringBetween("abc", "a", "")).toBe("");
            expect(sh.substringBetween("", "a", "c")).toBe("");
            expect(sh.substringBetween(s, "to", "")).toBe("");
            expect(sh.substringBetween(s, "eat", "or")).toBe("");
        });
        it("should return string between first occurrence of left and right params", () => {
            expect(sh.substringBetween("to be or not to eat bee ?", "to", "be")).toBe(" ");
        });

    });

    describe("format", () => {

        var s = "{0} {{0}} {{0}{1}} {{0{1}}} {{{0}1}} {{{0}{1}}}";

        it("should return empty string if invalid arguments are passed", () => {
            expect(sh.format(null)).toBe("");
        });
        it("should format the string correctly", () => {
            expect(sh.format(s, 1)).toBe("1 {0} {0}undefined} {0undefined} {11} {1undefined}");
            expect(sh.format(s, null, 3.14)).toBe("null {0} {0}3.14} {03.14} {null1} {null3.14}");
            expect(sh.format(s, "{1}", "a%")).toBe("{1} {0} {0}a%} {0a%} {{1}1} {{1}a%}");
        });

    });

});

