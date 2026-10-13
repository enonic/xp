import {execFileSync} from 'node:child_process';
import {copyFileSync, mkdirSync, readdirSync, readFileSync, rmSync, writeFileSync} from 'node:fs';
import {dirname, join} from 'node:path';

import {libDir, readJson, readMembers, xpPackageDir} from './packages.mjs';

const npmCli = process.env.npm_execpath;
if (!npmCli) {
    throw new Error('Run this script with `npm run test:packages`.');
}
const pnpmCli = join(libDir, 'node_modules', 'pnpm', 'bin', 'pnpm.cjs');
const tscCli = join(libDir, 'node_modules', 'typescript', 'bin', 'tsc');

const fixturesDir = join(libDir, 'test-packages');
const workDir = join(libDir, 'build', 'test-packages');
const tarballsDir = join(workDir, 'tarballs');

function run(cli, args, cwd) {
    return execFileSync(process.execPath, [cli, ...args], {cwd, encoding: 'utf8', stdio: 'pipe', maxBuffer: 64 * 1024 * 1024});
}

rmSync(workDir, {recursive: true, force: true});
mkdirSync(tarballsDir, {recursive: true});

const members = readMembers();
const packed = JSON.parse(run(npmCli, ['pack', '--json', '--pack-destination', tarballsDir,
    ...members.map(({dir}) => dir), xpPackageDir], libDir));
const tarballs = Object.fromEntries(packed.map(({name, filename}) => [name, `file:${join(tarballsDir, filename)}`]));

const memberNames = members.map(({pkg}) => pkg.name);
const memberTarballs = Object.fromEntries(memberNames.map(name => [name, tarballs[name]]));
const libNames = memberNames
    .filter(name => name.startsWith('@enonic-types/lib-'))
    .map(name => name.slice('@enonic-types/lib-'.length));

const requireAll = [
    'import type {Assert, IsTyped} from \'./assert\';',
    '',
    ...libNames.flatMap(name => [
        `const lib_${name} = require('/lib/xp/${name}');`,
        `export type Lib_${name}_IsTyped = Assert<IsTyped<typeof lib_${name}>>;`,
    ]),
    '',
].join('\n');

const compilerOptions = {
    target: 'es5',
    lib: ['es5'],
    module: 'commonjs',
    moduleResolution: 'node10',
    strict: true,
    noEmit: true,
    skipLibCheck: false,
};

const scenarios = [
    {
        // Only the meta package is a direct dependency, so every lib must come in through its references.
        name: 'require',
        dependencies: ['@enonic-types/xp'],
        compilerOptions: {types: ['@enonic-types/xp']},
        files: ['assert.ts', 'require.ts', 'require-all.ts'],
    },
    {
        name: 'import',
        dependencies: memberNames,
        compilerOptions: {
            types: ['@enonic-types/global'],
            paths: {'/lib/xp/*': ['./node_modules/@enonic-types/lib-*']},
        },
        files: ['assert.ts', 'import.ts', 'api/content.ts', 'api/context.ts', 'api/node.ts', 'api/portal.ts'],
    },
    {
        // The setup the xp README documents: pnpm only exposes the members through the hoist pattern.
        name: 'xp-import',
        dependencies: ['@enonic-types/xp'],
        compilerOptions: {
            types: ['@enonic-types/xp'],
            paths: {'/lib/xp/*': ['./node_modules/@enonic-types/lib-*']},
        },
        files: ['assert.ts', 'import.ts'],
        pnpmArgs: ['--config.public-hoist-pattern=@enonic-types/*'],
    },
];

const installers = {
    npm: cwd => run(npmCli, ['install', '--offline', '--ignore-scripts', '--no-audit', '--no-fund'], cwd),
    // Without `hoist=false`, pnpm lets a package resolve siblings it never declared.
    pnpm: (cwd, scenario) => run(pnpmCli, ['install', '--offline', '--ignore-scripts', '--no-frozen-lockfile',
        '--config.hoist=false', '--store-dir', join(workDir, 'pnpm-store'), ...(scenario.pnpmArgs ?? [])], cwd),
};

const failures = [];

// Overrides below pin every member to its tarball, so declared dependencies are checked here instead.
const typePackageReference = /(?:from\s+|import\s*\(?\s*|require\(\s*|reference\s+types=)['"](@enonic-types\/[^'"/]+)/g;
for (const {dir, pkg} of [...members, {dir: xpPackageDir, pkg: readJson(join(xpPackageDir, 'package.json'))}]) {
    const dependencies = pkg.dependencies ?? {};
    const used = readdirSync(dir)
        .filter(file => file.endsWith('.d.ts'))
        .flatMap(file => [...readFileSync(join(dir, file), 'utf8').matchAll(typePackageReference)].map(([, name]) => name));
    const problems = [
        ...[...new Set(used)].filter(name => !(name in dependencies)).map(name => `uses undeclared ${name}`),
        ...Object.entries(dependencies)
            .filter(([name, version]) => name.startsWith('@enonic-types/') && version !== pkg.version)
            .map(([name, version]) => `depends on ${name}@${version} instead of ${pkg.version}`),
    ];
    if (problems.length > 0) {
        console.log(`FAIL ${pkg.name} dependencies`);
        failures.push(`${pkg.name}:\n${problems.join('\n')}`);
    }
}

for (const [manager, install] of Object.entries(installers)) {
    for (const scenario of scenarios) {
        const label = `${manager}/${scenario.name}`;
        const appDir = join(workDir, manager, scenario.name);
        mkdirSync(appDir, {recursive: true});

        writeFileSync(join(appDir, 'package.json'), JSON.stringify({
            name: `fixture-${manager}-${scenario.name}`,
            private: true,
            devDependencies: Object.fromEntries(scenario.dependencies.map(name => [name, tarballs[name]])),
            // Members are not on the registry, so their transitive references must resolve to the local tarballs too.
            overrides: memberTarballs,
            pnpm: {overrides: memberTarballs},
        }, null, 4));
        writeFileSync(join(appDir, 'tsconfig.json'), JSON.stringify({
            compilerOptions: {...compilerOptions, ...scenario.compilerOptions},
            files: scenario.files,
        }, null, 4));
        writeFileSync(join(appDir, 'require-all.ts'), requireAll);
        for (const file of scenario.files.filter(file => file !== 'require-all.ts')) {
            mkdirSync(dirname(join(appDir, file)), {recursive: true});
            copyFileSync(join(fixturesDir, file), join(appDir, file));
        }

        try {
            install(appDir, scenario);
            run(tscCli, ['--project', 'tsconfig.json'], appDir);
            console.log(`ok   ${label}`);
        } catch (error) {
            console.log(`FAIL ${label}`);
            failures.push(`${label}:\n${error.stdout ?? ''}${error.stderr ?? ''}`.trim());
        }
    }
}

if (failures.length > 0) {
    console.error(`\n${failures.join('\n\n')}`);
    process.exitCode = 1;
} else {
    writeFileSync(join(workDir, '.ok'), '');
}
