package no.nav.dekoratoren.api.innloggingsstatus.pdl.query

data class PdlResponse(
        val data: PdlData
)

data class PdlData (
        val person: PdlPersonInfo
)