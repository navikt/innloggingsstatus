package no.nav.personbruker.innloggingsstatus.varsel

import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import no.nav.personbruker.innloggingsstatus.auth.AuthTokenService

fun Route.varselApi(authService: AuthTokenService, varselbjelleConsumer: VarselbjelleConsumer) {
    get("/rest/varsel/hentsiste") {
        doIfAuthenticated(authService) { ident, authLevel ->
            val response = varselbjelleConsumer.getVarselSummary(ident, authLevel)

            call.respond(response.status, response.readBytes())
        }
    }

    post("/rest/varsel/erlest/{varselId}") {
        doIfAuthenticated(authService) { ident, _ ->
            val varselId = call.parameters["varselId"]?: ""

            val response = varselbjelleConsumer.postErLest(ident, varselId)

            call.respond(response.status, response.readBytes())
        }
    }

    get("/varsel/proxy/{path}") {
        doIfAuthenticated(authService) { ident, authLevel ->
            val path = call.parameters["path"]?: ""

            val response = varselbjelleConsumer.makeGetProxyCall(path, ident, authLevel)

            call.respond(response.status, response.readBytes())
        }
    }

    post("/varsel/proxy/{path}") {
        doIfAuthenticated(authService) { ident, authLevel ->
            val path = call.parameters["path"]?: ""

            val response = varselbjelleConsumer.makePostProxyCall(path, ident, authLevel)

            call.respond(response.status, response.readBytes())
        }
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.doIfAuthenticated(
    authService: AuthTokenService,
    block: suspend (String, Int) -> Unit
) {
    val authInfo = authService.fetchAndParseAuthInfo(call)

    if (authInfo.authenticated) {
        block(authInfo.subject!!, authInfo.authLevel!!)
    } else {
        call.respond(HttpStatusCode.Unauthorized)
    }
}
