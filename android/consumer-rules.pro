# R8 Missing-class warnings from digiocamerakit — classes live in in.digio.api.*
# (a separate module not currently bundled transitively). Suppression is
# cosmetic: if digiocamerakit's camera/liveness code path runs without the
# missing module on the classpath, it will still fail at runtime with
# NoClassDefFoundError on DigioApiClient / DigioNetworkLauncher / etc.
-dontwarn in.digio.api.**
