package no.nav.dekoratoren.api.auth

import io.ktor.server.application.ApplicationCall
import no.nav.dekoratoren.api.oidc.OidcTokenInfo
import no.nav.dekoratoren.api.oidc.OidcTokenService
import no.nav.dekoratoren.api.selfissued.SelfIssuedTokenService
import no.nav.dekoratoren.api.user.SubjectNameService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AuthTokenService(
    private val oidcTokenService: OidcTokenService,
    private val subjectNameService: SubjectNameService,
    private val selfIssuedTokenService: SelfIssuedTokenService,
) {
    private val log: Logger = LoggerFactory.getLogger(no.nav.dekoratoren.api.auth.AuthTokenService::class.java)

    suspend fun getAuthenticatedUserInfo(call: ApplicationCall): no.nav.dekoratoren.api.auth.UserInfo {
        return try {
            fetchAndParseAuthenticatedUserInfo(call)
        } catch (e: Exception) {
            log.warn("Feil ved henting av brukers innloggingsinfo", e)
            no.nav.dekoratoren.api.auth.UserInfo.Companion.unAuthenticated()
        }
    }

    fun getAuthSummary(call: ApplicationCall): no.nav.dekoratoren.api.auth.AuthSummary {
        return fetchAndParseAuthInfo(call).let { authInfo ->
            no.nav.dekoratoren.api.auth.AuthSummary.Companion.fromAuthInfo(authInfo)
        }
    }

    private suspend fun fetchAndParseAuthenticatedUserInfo(call: ApplicationCall): no.nav.dekoratoren.api.auth.UserInfo {
        val authInfo = fetchAndParseAuthInfo(call)
        return getUserInfo(authInfo)
    }

    fun fetchAndParseAuthInfo(call: ApplicationCall): no.nav.dekoratoren.api.auth.AuthInfo {
        val oidcToken = getNewestOidcToken(call)
        return no.nav.dekoratoren.api.auth.AuthInfo(oidcToken)
    }

    private suspend fun getUserInfo(authInfo: no.nav.dekoratoren.api.auth.AuthInfo): no.nav.dekoratoren.api.auth.UserInfo {
        return if (authInfo.subject != null) {
            val subjectName = subjectNameService.getSubjectName(authInfo.subject!!)
            no.nav.dekoratoren.api.auth.UserInfo.Companion.authenticated(subjectName, authInfo.authLevel!!)
        } else {
            no.nav.dekoratoren.api.auth.UserInfo.Companion.unAuthenticated()
        }
    }

    private fun getNewestOidcToken(call: ApplicationCall): OidcTokenInfo? {
        val oidcToken = oidcTokenService.getOidcToken(call)
        val selfIssuedToken = selfIssuedTokenService.getSelfIssuedToken(call)

        return when {
            oidcToken == null && selfIssuedToken != null -> selfIssuedToken
            oidcToken != null && selfIssuedToken == null -> oidcToken
            oidcToken != null && selfIssuedToken != null -> oidcToken.mostRecentlyIssued(selfIssuedToken)
            else -> null
        }
    }
}