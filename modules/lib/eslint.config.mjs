import enonicConfig from '@enonic/eslint-config';
import enonicXpConfig from '@enonic/eslint-config/xp.js';
import tsPlugin from '@typescript-eslint/eslint-plugin';

export default [
    ...enonicConfig,
    enonicXpConfig,
    {
        files: ['**/*.ts', '**/*.d.ts'],
        rules: {
            '@typescript-eslint/no-var-requires': 'off',
            '@typescript-eslint/triple-slash-reference': 'off',
            '@typescript-eslint/no-empty-interface': 'off',
            '@typescript-eslint/no-redundant-type-constituents': 'off',
            '@typescript-eslint/no-unsafe-assignment': 'off',
            '@typescript-eslint/no-unsafe-call': 'off',
            '@typescript-eslint/no-unsafe-member-access': 'off',
            '@typescript-eslint/no-unsafe-return': 'off',
            '@typescript-eslint/no-empty-object-type': 'off',
            'spaced-comment': ['error', 'always', { markers: ['/'] }],
        },
    },
    {
        files: ['scripts/**/*.mjs'],
        ...tsPlugin.configs['flat/disable-type-checked'],
    },
    {
        ignores: [
            '**/out',
            '**/build',
            'eslint.config.mjs',
            'jsdoc/**/*.js',
            'test-packages/**',
            'lib-*/**/*.d.ts',
            "node_modules/**/*",
            '**/src/main/resources/lib/xp/*.js',
            '**/src/test/**/*.js',
            '**/src/main/resources/lib/xp/examples/**/*.js',
        ],
    },
];
