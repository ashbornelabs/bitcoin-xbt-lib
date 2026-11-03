# Build instructions

> Modified by Ashborne Labs (2026) for the Bitcoin BLAKE2b (XBT) chain. See NOTICE.

## Requirements

To build bitcoin-xbt-lib, you need:
 - a JDK 21
 - Maven 3.9 or newer, or the included Maven wrapper (`./mvnw`)

## Installing bitcoin-xbt-kmp

bitcoin-xbt-lib depends on [bitcoin-xbt-kmp](https://github.com/ashbornelabs/bitcoin-xbt-kmp), which is not published to a public Maven repository yet. Install it in your local Maven repository first:

```
$ git clone https://github.com/ashbornelabs/bitcoin-xbt-kmp.git
$ cd bitcoin-xbt-kmp
$ ./gradlew publishKotlinMultiplatformPublicationToMavenLocal publishJvmPublicationToMavenLocal
```

## Building the Scala library

Then build bitcoin-xbt-lib, run its tests and install it in your local Maven repository:

```
$ ./mvnw install
```

## JNI bindings for libsecp256k1

You do not need to build libsecp256k1: its JNI bindings come from [secp256k1-kmp](https://github.com/ACINQ/secp256k1-kmp/), with native libraries for Linux, Windows and macOS. See its documentation to build them yourself.
