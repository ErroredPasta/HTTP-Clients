package com.example.exchangerate.data.repository

import com.example.exchangerate.data.remote.ExchangeRateApi
import com.example.exchangerate.data.remote.ExchangeRateApiImpl
import com.example.exchangerate.data.repository.response.*
import com.example.exchangerate.domain.exception.ConversionException
import com.example.exchangerate.domain.model.Currency
import com.example.exchangerate.domain.repository.ConversionRepository
import com.example.exchangerate.rule.TestCoroutineRule
import com.example.exchangerate.util.runCoroutineCatching
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.utils.io.core.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.*


@ExperimentalCoroutinesApi
class ConversionRepositoryImplTest {

    private lateinit var sut: ConversionRepository

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    // region constants ============================================================================
    private val validRequest = Request(from = "EUR", to = "GBP", amount = 2.0)
    private val unsupportedCodeRequest = Request(from = "EUR", to = "ABC", amount = 2.0)
    private val malformedRequest = Request(from = "EUR", to = "GBPE", amount = 2.0)
    private val invalidAmountRequest = Request(from = "EUR", to = "GBP", amount = -1.0)
    // endregion constants =========================================================================

    @Test
    fun `convert currency, successful response, returns conversion result`() = runTest {
        // arrange
        setupWithKtorMockEngine(response = validConversionResponse)

        // act
        val (from, to, amount) = validRequest
        val result = runCoroutineCatching {
            sut.convertCurrency(from = from.toCurrency(), to = to.toCurrency(), amount = amount)
        }.getOrElse {
            Assert.fail(it.toString())
            return@runTest
        }

        // assert
        assertThat(result.from).isEqualTo(Currency.EUR)
        assertThat(result.to).isEqualTo(Currency.GBP)
        assertThat(result.conversionResult).isEqualTo(1.7178)
    }

    @Test
    fun `convert currency, unsupported code request, throws UnsupportedCode exception`() = runTest {
        // arrange
        setupWithKtorMockEngine(response = unsupportedCodeResponse)

        // act
        val (from, to, amount) = unsupportedCodeRequest

        runCoroutineCatching {
            sut.convertCurrency(from = from.toCurrency(), to = to.toCurrency(), amount = amount)
        }.onFailure {
            // assert
            assertThat(it).isEqualTo(ConversionException.UnsupportedCode)
            return@runTest
        }

        noExceptionThrown(expectedException = ConversionException.UnsupportedCode::class.java)
    }

    @Test
    fun `convert currency, malformed request, throws MalformedRequest exception`() = runTest {
        // arrange
        setupWithKtorMockEngine(response = malformedRequestResponse)

        // act
        val (from, to, amount) = malformedRequest

        runCoroutineCatching {
            sut.convertCurrency(from = from.toCurrency(), to = to.toCurrency(), amount = amount)
        }.onFailure {
            // assert
            assertThat(it).isEqualTo(ConversionException.MalformedRequest)
            return@runTest
        }

        noExceptionThrown(expectedException = ConversionException.MalformedRequest::class.java)
    }

    @Test
    fun `convert currency, request using invalid key, throws InvalidKey exception`() = runTest {
        // arrange
        setupWithKtorMockEngine(response = invalidKeyResponse)


        // act
        val (from, to, amount) = validRequest

        runCoroutineCatching {
            sut.convertCurrency(from = from.toCurrency(), to = to.toCurrency(), amount = amount)
        }.onFailure {
            // assert
            assertThat(it).isEqualTo(ConversionException.InvalidKey)
            return@runTest
        }

        noExceptionThrown(expectedException = ConversionException.InvalidKey::class.java)
    }

    @Test
    fun `convert currency, when the account is inactive, throws InactiveAccount exception`() =
        runTest {
            // arrange
            setupWithKtorMockEngine(response = inactiveAccountResponse)

            // act
            val (from, to, amount) = validRequest

            runCoroutineCatching {
                sut.convertCurrency(from = from.toCurrency(), to = to.toCurrency(), amount = amount)
            }.onFailure {
                // assert
                assertThat(it).isEqualTo(ConversionException.InactiveAccount)
                return@runTest
            }

            noExceptionThrown(expectedException = ConversionException.InactiveAccount::class.java)
        }

    @Test
    fun `convert currency, when quota reached, throws QuotaReached exception`() = runTest {
        // arrange
        setupWithKtorMockEngine(response = quotaReachedResponse)

        // act
        val (from, to, amount) = validRequest

        runCoroutineCatching {
            sut.convertCurrency(from = from.toCurrency(), to = to.toCurrency(), amount = amount)
        }.onFailure {
            // assert
            assertThat(it).isEqualTo(ConversionException.QuotaReached)
            return@runTest
        }

        noExceptionThrown(expectedException = ConversionException.QuotaReached::class.java)
    }

    @Test
    fun `convert currency, given invalid amount, throws 404 HttpException`() = runTest {
        // arrange
        setupWithKtorMockEngine(response = invalidAmountResponse)

        // act
        val (from, to, amount) = invalidAmountRequest

        runCoroutineCatching {
            sut.convertCurrency(from = from.toCurrency(), to = to.toCurrency(), amount = amount)
        }.onFailure {
            // assert
            assertThat(it).isInstanceOf(ClientRequestException::class.java)
            assertThat(it.message).contains("404")
            return@runTest
        }

        noExceptionThrown(expectedException = ClientRequestException::class.java)
    }

    @Test
    fun `convert currency, given an exception other than ConversionException, rethrows it`() =
        runTest {
            // arrange
            setUpWithMock {
                coEvery {
                    it.convertCurrency(from = "EUR", to = "GBP", amount = String.format("%f", 2.0))
                } throws Exception("Not a ConversionException")
            }

            // act
            val (from, to, amount) = validRequest

            runCoroutineCatching {
                sut.convertCurrency(from = from.toCurrency(), to = to.toCurrency(), amount = amount)
            }.onFailure {
                // assert
                assertThat(it).isNotInstanceOf(ConversionException::class.java)
                assertThat(it.message).isEqualTo("Not a ConversionException")
                return@runTest
            }

            noExceptionThrown(expectedException = Exception::class.java)
        }

    // region helper methods =======================================================================
    private fun setupWithKtorMockEngine(response: Response) {
        val mockEngine = MockEngine {
            respond(
                content = response.body,
                status = response.code,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        sut = ConversionRepositoryImpl(
            api = ExchangeRateApiImpl(httpClient = createHttpClient(engine = mockEngine)),
            ioDispatcher = testCoroutineRule.dispatcher
        )
    }

    private val jsonSerializer = object : JsonSerializer {
        private val gson = Gson()

        override fun read(type: TypeInfo, body: Input): Any {
            return gson.fromJson(body.readText(), type.type.java)
        }

        override fun write(data: Any, contentType: ContentType): OutgoingContent {
            return TextContent(gson.toJson(data), contentType)
        }
    }

    private fun createHttpClient(engine: HttpClientEngine) = HttpClient(engine) {
        expectSuccess = true

        HttpResponseValidator {
            handleResponseException {
                val clientException = it as? ClientRequestException
                    ?: return@handleResponseException
                val responseBody = clientException.response.receive<String>()

                throw ConversionException.findExceptionInErrorBodyMessage(responseBody)
                    ?: clientException
            }
        }

        install(JsonFeature) {
            serializer = this@ConversionRepositoryImplTest.jsonSerializer
        }

        install(DefaultRequest) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }
    }

    /**
     * String을 Currency로 바꾸어주는 extension function
     *
     * @receiver Currency의 String
     * @return String과 일치하는 Currency를 return,
     * 테스트를 위해 만약 일치하는 String이 없으면 Currency.EUR을 return
     */
    private fun String.toCurrency() = kotlin.runCatching {
        Currency.valueOf(this)
    }.recover {
        Currency.EUR
    }.getOrThrow()

    private fun <T> noExceptionThrown(expectedException: Class<T>) {
        Assert.fail("Expected ${expectedException.simpleName} exception, but no exception was thrown")
    }

    private fun setUpWithMock(mockApiBehavior: (ExchangeRateApi) -> Unit) {
        sut = ConversionRepositoryImpl(
            api = mockk<ExchangeRateApi>().also { mockApiBehavior(it) },
            ioDispatcher = testCoroutineRule.dispatcher
        )
    }
    // endregion helper methods ====================================================================

    // region helper classes =======================================================================
    private data class Request(
        val from: String,
        val to: String,
        val amount: Double
    )
    // endregion helper classes ====================================================================
}