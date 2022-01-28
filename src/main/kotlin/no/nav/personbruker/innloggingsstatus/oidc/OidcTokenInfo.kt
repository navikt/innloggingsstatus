package no.nav.personbruker.innloggingsstatus.oidc

import no.nav.personbruker.innloggingsstatus.auth.TokenInfo
import java.time.LocalDateTime

data class OidcTokenInfo(
    override val subject: String,
    override val authLevel: Int,
    override val issueTime: LocalDateTime,
    override val expiryTime: LocalDateTime
): TokenInfo {

    fun mostRecentlyIssued(other: OidcTokenInfo): OidcTokenInfo {
        // This assumes that there is virtually no possibility of these tokens having the exact same issueTime.
        // This also assumes that in the event that two tokens under comparison having different subjects and authLevels,
        // the latest issued token is the most correct (as it has been acquired most recently).
        if (this.issueTime.isAfter(other.issueTime)) {
            return this
        }
        return other
    }
}
