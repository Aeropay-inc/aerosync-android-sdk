# Releasing bank-link-sdk

How to publish a new version of `com.aerosync:bank-link-sdk` to Maven Central.

**The one thing to remember:** stage with Gradle, then click **Publish** in the Central Portal UI.
Do *not* use the `closeAndRelease` Gradle tasks — see [Do not use](#do-not-use) below.

## Prerequisites

- **JDK 17.** Gradle 8.0 fails on JDK 21, which is the default `java` on the dev machines here.
  ```powershell
  $env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
  ```
- **Credentials in `local.properties`** (gitignored, never commit):
  ```properties
  ossrhUsername=<Central Portal user token username>
  ossrhPassword=<Central Portal user token password>
  signing.keyId=<PGP key id>
  signing.key=<ASCII-armored PGP private key>
  signing.password=<PGP key passphrase>
  ```
  Portal tokens come from [central.sonatype.com](https://central.sonatype.com) → Account → Generate User Token.
  These are *not* the old `s01.oss.sonatype.org` credentials.

## Release steps

**1. Bump the version** in [`bank-link-sdk/build.gradle`](bank-link-sdk/build.gradle):

```groovy
PUBLISH_VERSION = '2.1.0'
```

**2. Stage to Sonatype:**

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
.\gradlew.bat clean :bank-link-sdk:assembleRelease publishToSonatype
```

This builds, PGP-signs, and uploads the `.aar`, sources jar, POM, and Gradle module metadata.
It prints the staging repository id, e.g. `com.aerosync--96fcec89-...`.

**3. Hand the staging repo to the Portal.** `publishToSonatype` uploads into the OSSRH compat
layer only — the repo sits in state `open` with `portal_deployment_id: null` and **will not appear
in the Portal** until it is transferred. Do that explicitly (Git Bash):

```bash
U=$(sed -n 's/^ossrhUsername=//p' local.properties | tr -d '\r')
P=$(sed -n 's/^ossrhPassword=//p' local.properties | tr -d '\r')
BASE=https://ossrh-staging-api.central.sonatype.com

# Get the repo key
curl -s -u "$U:$P" "$BASE/manual/search/repositories?state=open"

# Transfer it. user_managed = lands as pending; nothing goes live yet.
curl -u "$U:$P" -X POST "$BASE/manual/upload/repository/<url-encoded-key>?publishing_type=user_managed"
```

The `/` in the key must be URL-encoded as `%2F`, e.g.
`5BlSPg%2Fany%2Fcom.aerosync--96fcec89-c264-4517-9748-b2ea6e91bc05`.

Afterwards the repo flips to `state: closed` with a real `portal_deployment_id`. Verify with
`?state=closed` and check `warnings` is empty.

**4. Publish in the Portal.** Go to [central.sonatype.com](https://central.sonatype.com) →
**Deployments**. The deployment appears as `com.aerosync (via OSSRH Staging API)`. Confirm the
coordinates read `bank-link-sdk / com.aerosync / <your version>`, then click **Publish**.
`Drop` discards it if something looks wrong.

This is the promotion-to-Maven-Central step. It is **irreversible** — a published version can
never be overwritten or deleted, only superseded by a new one.

**5. Tag the release.** Tags go on the release branch (`master` has historically lagged behind):

```bash
git tag -a v2.1.0 -m "<summary>"
git push origin v2.1.0
gh release create v2.1.0 --title "v2.1.0" --notes "<release notes>"
```

The artifact appears on Maven Central within ~15 minutes, and in search a few hours later.

## Do not use

These tasks target the legacy Nexus API. We are on the Central Portal's **OSSRH Staging API
compatibility service** ([`scripts/publish-root.gradle`](scripts/publish-root.gradle)), where they
fail:

```
closeAndReleaseSonatypeStagingRepository
findSonatypeStagingRepository
```

`findSonatypeStagingRepository` fails with `No staging repositories found for stagingProfileId:
com.aerosync, descriptionRegex: \b\Q:Sample:unspecified\E(\s|$)`. The root project has no `group`
and is named `Sample`, so the plugin's default staging description is `:Sample:unspecified` — and
its `\b`-anchored search regex can never match a string starting with `:`. The plugin cannot find
the repository it just created.

This only bites when close/release runs in a *separate* Gradle invocation from the publish, which
is why it stayed hidden for so long. Publishing via the Portal UI avoids the whole path.

## Troubleshooting

Staging repos are only visible through the compat API. Useful queries (Git Bash):

```bash
U=$(sed -n 's/^ossrhUsername=//p' local.properties | tr -d '\r')
P=$(sed -n 's/^ossrhPassword=//p' local.properties | tr -d '\r')
BASE=https://ossrh-staging-api.central.sonatype.com

# What is staged, and has it reached the Portal yet?
curl -s -u "$U:$P" "$BASE/manual/search/repositories?state=open"
curl -s -u "$U:$P" "$BASE/manual/search/repositories?state=closed"

# Discard a bad staging repo (safe, nothing was published).
curl -u "$U:$P" -X DELETE "$BASE/manual/drop/repository/<url-encoded-key>"

# What is already live on Maven Central?
curl -s https://repo1.maven.org/maven2/com/aerosync/bank-link-sdk/maven-metadata.xml
```

The repository key contains `/` characters that must be URL-encoded as `%2F`, e.g.
`5BlSPg%2Fany%2Fcom.aerosync--96fcec89-c264-4517-9748-b2ea6e91bc05`.

**A staging repo with `portal_deployment_id: null` has not reached the Portal yet** — that is why
the Deployments tab can look empty right after staging.

**`initializeSonatypeStagingRepository FAILED`** usually means the `ossrhUsername` /
`ossrhPassword` in `local.properties` are stale OSSRH credentials rather than Central Portal
user tokens.
