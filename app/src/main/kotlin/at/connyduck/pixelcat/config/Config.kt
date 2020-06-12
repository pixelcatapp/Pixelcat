package at.connyduck.pixelcat.config

object Config {

    const val website = "https://pixelcat.app"

    const val oAuthScheme = "pixelcat"
    const val oAuthHost = "oauth"
    const val oAuthRedirect = "$oAuthScheme://$oAuthHost"
    const val oAuthScopes = "read write follow"

    val domainExceptions = arrayOf("gab.com", "gab.ai", "gabfed.com")

}