# Exchange-Rate
간단한 환율 변환앱 + MockWebServer를 이용한 Repository test 및 ViewModel의 Flow test

실행전 https://www.exchangerate-api.com/ 에서 API key를 받급받아 local.properties에 EXCHANGE_RATE_API_KEY property로 추가 후 실행해주세요.

- Architecture : MVVM
- DI : Dagger2
- Async : Coroutine, Flow
- Network : Retrofit2 + Gson
- Testing : JUnit4, MockK, MockWebServer,Truth
