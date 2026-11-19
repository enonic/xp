exports.execute = function (name, args) {
    var facts = {
        keys: Object.keys(args),
        types: {
            int: typeof args.int,
            long: typeof args.long,
            unsafe: typeof args.unsafe,
            big: typeof args.big,
            double: typeof args.double,
            flag: typeof args.flag,
            text: typeof args.text,
            nested: typeof args.nested,
            list: Array.isArray(args.list),
            inner: Array.isArray(args.list[2])
        },
        explicitNull: args.n === null,
        hasNull: 'n' in args,
        listLength: args.list.length,
        listNull: args.list[1] === null,
        deep: args.nested.deep.value,
        nestedNull: args.nested.deep.empty === null,
        sum: args.int + args.long + args.double
    };
    return {content: [{type: 'text', text: JSON.stringify(args)}, {type: 'text', text: JSON.stringify(facts)}]};
};

exports.limits = function () {
    return {
        defined: 'value',
        missing: undefined,
        explicitNull: null,
        list: ['a', null, 'b', undefined]
    };
};
