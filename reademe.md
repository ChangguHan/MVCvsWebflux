## 궁금했던점들
1. MVC는 하나의 스레드가 Controller → Service 까지 모두 실행하고 반환할까?
2. WebFlux는 Request-Response 처리하는 스레드와 Controller, Service 스레드가 정말 다를까?
3. WebFlux에 여러개 요청이 들어오면, 비즈니스 로직 처리하는 스레드는 어떻게 관리될까?
4. 100개의 요청이 들어오면 1개의 스레드에서만 요청 관리하고, 그 뒤 100개의 스래드에서 비즈니스 로직 처리할까?

## MVC는 하나의 스레드가 Controller → Service 까지 모두 실행하고 반환할까?
- 맞음
    - MVC controller: https://github.com/ChangguHan/MVCvsWebflux/blob/main/mvc/src/main/java/com/example/mvc/MvcApplication.java
    - Load Test: https://github.com/ChangguHan/MVCvsWebflux/blob/main/mvc/src/main/java/com/example/mvc/MVCLoadTest.java
- 요청 10개 동시에 들어오면 Dispatcher Servlet에서 각각 스레드 할당해서 controller에 매핑시켜줌
```
2023-03-15T09:43:42.628+09:00 DEBUG 47628 --- [nio-9001-exec-7] o.s.web.servlet.DispatcherServlet        : GET "/test?num=4", parameters={masked}
2023-03-15T09:43:42.628+09:00 DEBUG 47628 --- [nio-9001-exec-5] o.s.web.servlet.DispatcherServlet        : GET "/test?num=5", parameters={masked}
2023-03-15T09:43:42.628+09:00 DEBUG 47628 --- [nio-9001-exec-8] o.s.web.servlet.DispatcherServlet        : GET "/test?num=2", parameters={masked}
2023-03-15T09:43:42.629+09:00 DEBUG 47628 --- [nio-9001-exec-4] o.s.web.servlet.DispatcherServlet        : GET "/test?num=8", parameters={masked}
2023-03-15T09:43:42.628+09:00 DEBUG 47628 --- [io-9001-exec-10] o.s.web.servlet.DispatcherServlet        : GET "/test?num=0", parameters={masked}
2023-03-15T09:43:42.628+09:00 DEBUG 47628 --- [nio-9001-exec-2] o.s.web.servlet.DispatcherServlet        : GET "/test?num=1", parameters={masked}
2023-03-15T09:43:42.629+09:00 DEBUG 47628 --- [nio-9001-exec-1] o.s.web.servlet.DispatcherServlet        : GET "/test?num=9", parameters={masked}
2023-03-15T09:43:42.628+09:00 DEBUG 47628 --- [nio-9001-exec-6] o.s.web.servlet.DispatcherServlet        : GET "/test?num=7", parameters={masked}
2023-03-15T09:43:42.629+09:00 DEBUG 47628 --- [nio-9001-exec-3] o.s.web.servlet.DispatcherServlet        : GET "/test?num=6", parameters={masked}
2023-03-15T09:43:42.630+09:00 DEBUG 47628 --- [nio-9001-exec-9] o.s.web.servlet.DispatcherServlet        : GET "/test?num=3", parameters={masked}
```
  - Dispatcher Servlet에서 매핑된 스레드 그대로, Controller, Service 처리
```
2023-03-15T09:43:42.685+09:00  INFO 47628 --- [io-9001-exec-10] c.e.mvc.MvcApplication$TestController    : Controller 0
2023-03-15T09:43:42.686+09:00  INFO 47628 --- [nio-9001-exec-8] c.e.mvc.MvcApplication$TestController    : Controller 2
2023-03-15T09:43:42.686+09:00  INFO 47628 --- [nio-9001-exec-9] c.e.mvc.MvcApplication$TestController    : Controller 3
2023-03-15T09:43:42.685+09:00  INFO 47628 --- [nio-9001-exec-7] c.e.mvc.MvcApplication$TestController    : Controller 4
2023-03-15T09:43:42.685+09:00  INFO 47628 --- [nio-9001-exec-3] c.e.mvc.MvcApplication$TestController    : Controller 6
2023-03-15T09:43:42.687+09:00  INFO 47628 --- [nio-9001-exec-9] c.e.mvc.MvcApplication$TestService       : Service 3
2023-03-15T09:43:42.687+09:00  INFO 47628 --- [nio-9001-exec-7] c.e.mvc.MvcApplication$TestService       : Service 4
2023-03-15T09:43:42.687+09:00  INFO 47628 --- [nio-9001-exec-3] c.e.mvc.MvcApplication$TestService       : Service 6
2023-03-15T09:43:42.686+09:00  INFO 47628 --- [nio-9001-exec-6] c.e.mvc.MvcApplication$TestController    : Controller 7
2023-03-15T09:43:42.686+09:00  INFO 47628 --- [nio-9001-exec-4] c.e.mvc.MvcApplication$TestController    : Controller 8
2023-03-15T09:43:42.685+09:00  INFO 47628 --- [nio-9001-exec-5] c.e.mvc.MvcApplication$TestController    : Controller 5
2023-03-15T09:43:42.687+09:00  INFO 47628 --- [io-9001-exec-10] c.e.mvc.MvcApplication$TestService       : Service 0
2023-03-15T09:43:42.685+09:00  INFO 47628 --- [nio-9001-exec-1] c.e.mvc.MvcApplication$TestController    : Controller 9
2023-03-15T09:43:42.687+09:00  INFO 47628 --- [nio-9001-exec-6] c.e.mvc.MvcApplication$TestService       : Service 7
2023-03-15T09:43:42.685+09:00  INFO 47628 --- [nio-9001-exec-2] c.e.mvc.MvcApplication$TestController    : Controller 1
2023-03-15T09:43:42.687+09:00  INFO 47628 --- [nio-9001-exec-5] c.e.mvc.MvcApplication$TestService       : Service 5
2023-03-15T09:43:42.687+09:00  INFO 47628 --- [nio-9001-exec-8] c.e.mvc.MvcApplication$TestService       : Service 2
2023-03-15T09:43:42.687+09:00  INFO 47628 --- [nio-9001-exec-1] c.e.mvc.MvcApplication$TestService       : Service 9
2023-03-15T09:43:42.687+09:00  INFO 47628 --- [nio-9001-exec-4] c.e.mvc.MvcApplication$TestService       : Service 8
2023-03-15T09:43:42.687+09:00  INFO 47628 --- [nio-9001-exec-2] c.e.mvc.MvcApplication$TestService       : Service 1
```

## WebFlux는 Request-Response 처리하는 스레드와 Controller, Service 스레드가 정말 다를까?
### 관련 삽질: 처음에 실행했을때 아니었음
- https://github.com/ChangguHan/MVCvsWebflux/blob/main/webflux/src/main/java/com/example/webflux/WebFluxLoadTest.java
- 결과: 요청 10개 동시에 들어오면 WebHandlerAdapter 각각 스레드 할당해서 
```
2023-03-15T09:54:25.456+09:00 DEBUG 50076 --- [ctor-http-nio-5] o.s.w.s.adapter.HttpWebHandlerAdapter    : [7841181b-2] HTTP GET "/test?num=0"
2023-03-15T09:54:25.458+09:00 DEBUG 50076 --- [ctor-http-nio-7] o.s.w.s.adapter.HttpWebHandlerAdapter    : [0c6b669b-3] HTTP GET "/test?num=8"
2023-03-15T09:54:25.460+09:00 DEBUG 50076 --- [tor-http-nio-10] o.s.w.s.adapter.HttpWebHandlerAdapter    : [adac019b-4] HTTP GET "/test?num=5"
2023-03-15T09:54:25.460+09:00 DEBUG 50076 --- [ctor-http-nio-4] o.s.w.s.adapter.HttpWebHandlerAdapter    : [5310fe85-6] HTTP GET "/test?num=1"
2023-03-15T09:54:25.460+09:00 DEBUG 50076 --- [tor-http-nio-12] o.s.w.s.adapter.HttpWebHandlerAdapter    : [12d02465-7] HTTP GET "/test?num=7"
2023-03-15T09:54:25.460+09:00 DEBUG 50076 --- [ctor-http-nio-9] o.s.w.s.adapter.HttpWebHandlerAdapter    : [bf725450-5] HTTP GET "/test?num=3"
2023-03-15T09:54:25.461+09:00 DEBUG 50076 --- [tor-http-nio-11] o.s.w.s.adapter.HttpWebHandlerAdapter    : [faf70309-8] HTTP GET "/test?num=2"
2023-03-15T09:54:25.469+09:00 DEBUG 50076 --- [ctor-http-nio-3] o.s.w.s.adapter.HttpWebHandlerAdapter    : [4c5dcc69-9] HTTP GET "/test?num=9"
2023-03-15T09:54:25.470+09:00 DEBUG 50076 --- [ctor-http-nio-6] o.s.w.s.adapter.HttpWebHandlerAdapter    : [264b4cd1-10] HTTP GET "/test?num=4"
2023-03-15T09:54:25.476+09:00 DEBUG 50076 --- [ctor-http-nio-8] o.s.w.s.adapter.HttpWebHandlerAdapter    : [aace1542-11] HTTP GET "/test?num=6"
```
- Controller → Service 까지 같은 스레드로 가져감

```
2023-03-15T09:54:25.460+09:00  INFO 50076 --- [ctor-http-nio-5] c.e.w.WebfluxApplication$TestController  : Controller 0
2023-03-15T09:54:25.460+09:00  INFO 50076 --- [ctor-http-nio-5] c.e.w.WebfluxApplication$TestService     : Service 0
2023-03-15T09:54:25.461+09:00  INFO 50076 --- [ctor-http-nio-7] c.e.w.WebfluxApplication$TestController  : Controller 8
2023-03-15T09:54:25.461+09:00  INFO 50076 --- [ctor-http-nio-7] c.e.w.WebfluxApplication$TestService     : Service 8
2023-03-15T09:54:25.463+09:00  INFO 50076 --- [ctor-http-nio-4] c.e.w.WebfluxApplication$TestController  : Controller 1
2023-03-15T09:54:25.462+09:00  INFO 50076 --- [tor-http-nio-10] c.e.w.WebfluxApplication$TestController  : Controller 5
2023-03-15T09:54:25.463+09:00  INFO 50076 --- [ctor-http-nio-9] c.e.w.WebfluxApplication$TestController  : Controller 3
2023-03-15T09:54:25.463+09:00  INFO 50076 --- [ctor-http-nio-4] c.e.w.WebfluxApplication$TestService     : Service 1
2023-03-15T09:54:25.463+09:00  INFO 50076 --- [tor-http-nio-10] c.e.w.WebfluxApplication$TestService     : Service 5
2023-03-15T09:54:25.463+09:00  INFO 50076 --- [ctor-http-nio-9] c.e.w.WebfluxApplication$TestService     : Service 3
2023-03-15T09:54:25.464+09:00  INFO 50076 --- [tor-http-nio-12] c.e.w.WebfluxApplication$TestController  : Controller 7
2023-03-15T09:54:25.467+09:00  INFO 50076 --- [tor-http-nio-12] c.e.w.WebfluxApplication$TestService     : Service 7
2023-03-15T09:54:25.467+09:00  INFO 50076 --- [tor-http-nio-11] c.e.w.WebfluxApplication$TestController  : Controller 2
2023-03-15T09:54:25.468+09:00  INFO 50076 --- [tor-http-nio-11] c.e.w.WebfluxApplication$TestService     : Service 2
2023-03-15T09:54:25.472+09:00  INFO 50076 --- [ctor-http-nio-3] c.e.w.WebfluxApplication$TestController  : Controller 9
2023-03-15T09:54:25.472+09:00  INFO 50076 --- [ctor-http-nio-3] c.e.w.WebfluxApplication$TestService     : Service 9
2023-03-15T09:54:25.472+09:00  INFO 50076 --- [ctor-http-nio-6] c.e.w.WebfluxApplication$TestController  : Controller 4
2023-03-15T09:54:25.473+09:00  INFO 50076 --- [ctor-http-nio-6] c.e.w.WebfluxApplication$TestService     : Service 4
2023-03-15T09:54:25.477+09:00  INFO 50076 --- [ctor-http-nio-8] c.e.w.WebfluxApplication$TestController  : Controller 6
2023-03-15T09:54:25.478+09:00  INFO 50076 --- [ctor-http-nio-8] c.e.w.WebfluxApplication$TestService     : Service 6
```
## 수정: Thread.sleep(블록킹 작업) 을 Publisher로 옮겨줌
- https://github.com/ChangguHan/MVCvsWebflux/blob/main/webflux/src/main/java/com/example/webflux2/WebFluxLoadTest2.java
- 결과: 요청 10개 동시에 들어오면 WebHandlerAdapter 각각 스레드 할당한뒤
```
2023-03-15T09:58:37.422+09:00 DEBUG 47520 --- [ctor-http-nio-6] o.s.w.s.adapter.HttpWebHandlerAdapter    : [c0186e3a-9] HTTP GET "/test?num=9"
2023-03-15T09:58:37.422+09:00 DEBUG 47520 --- [ctor-http-nio-8] o.s.w.s.adapter.HttpWebHandlerAdapter    : [a53ae936-10] HTTP GET "/test?num=4"
2023-03-15T09:58:37.422+09:00 DEBUG 47520 --- [ctor-http-nio-2] o.s.w.s.adapter.HttpWebHandlerAdapter    : [45dcb2ff-3] HTTP GET "/test?num=0"
2023-03-15T09:58:37.422+09:00 DEBUG 47520 --- [ctor-http-nio-5] o.s.w.s.adapter.HttpWebHandlerAdapter    : [ab53b10f-7] HTTP GET "/test?num=6"
2023-03-15T09:58:37.422+09:00 DEBUG 47520 --- [ctor-http-nio-9] o.s.w.s.adapter.HttpWebHandlerAdapter    : [315ae850-8] HTTP GET "/test?num=2"
2023-03-15T09:58:37.422+09:00 DEBUG 47520 --- [tor-http-nio-10] o.s.w.s.adapter.HttpWebHandlerAdapter    : [2261a8f5-5] HTTP GET "/test?num=3"
2023-03-15T09:58:37.422+09:00 DEBUG 47520 --- [ctor-http-nio-3] o.s.w.s.adapter.HttpWebHandlerAdapter    : [3aef298b-4] HTTP GET "/test?num=7"
2023-03-15T09:58:37.422+09:00 DEBUG 47520 --- [ctor-http-nio-4] o.s.w.s.adapter.HttpWebHandlerAdapter    : [c2e6dd53-6] HTTP GET "/test?num=8"
2023-03-15T09:58:37.422+09:00 DEBUG 47520 --- [tor-http-nio-11] o.s.w.s.adapter.HttpWebHandlerAdapter    : [8b4fee12-2] HTTP GET "/test?num=1"
2023-03-15T09:58:37.422+09:00 DEBUG 47520 --- [ctor-http-nio-7] o.s.w.s.adapter.HttpWebHandlerAdapter    : [541deff5-1] HTTP GET "/test?num=5"
```
- request 이후부터 다른 스레드에서 진행
```
2023-03-15T09:58:37.590+09:00  INFO 47520 --- [ctor-http-nio-8] reactor.Mono.DelayElement.4              : onSubscribe([Fuseable] MonoDelayElement.DelayElementSubscriber)
2023-03-15T09:58:37.590+09:00  INFO 47520 --- [ctor-http-nio-2] reactor.Mono.DelayElement.3              : onSubscribe([Fuseable] MonoDelayElement.DelayElementSubscriber)
2023-03-15T09:58:37.590+09:00  INFO 47520 --- [ctor-http-nio-4] reactor.Mono.DelayElement.9              : onSubscribe([Fuseable] MonoDelayElement.DelayElementSubscriber)
2023-03-15T09:58:37.590+09:00  INFO 47520 --- [ctor-http-nio-6] reactor.Mono.DelayElement.2              : onSubscribe([Fuseable] MonoDelayElement.DelayElementSubscriber)
2023-03-15T09:58:37.590+09:00  INFO 47520 --- [tor-http-nio-11] reactor.Mono.DelayElement.1              : onSubscribe([Fuseable] MonoDelayElement.DelayElementSubscriber)
2023-03-15T09:58:37.590+09:00  INFO 47520 --- [ctor-http-nio-7] reactor.Mono.DelayElement.5              : onSubscribe([Fuseable] MonoDelayElement.DelayElementSubscriber)
2023-03-15T09:58:37.590+09:00  INFO 47520 --- [ctor-http-nio-5] reactor.Mono.DelayElement.7              : onSubscribe([Fuseable] MonoDelayElement.DelayElementSubscriber)
2023-03-15T09:58:37.590+09:00  INFO 47520 --- [ctor-http-nio-9] reactor.Mono.DelayElement.6              : onSubscribe([Fuseable] MonoDelayElement.DelayElementSubscriber)
2023-03-15T09:58:37.590+09:00  INFO 47520 --- [tor-http-nio-10] reactor.Mono.DelayElement.8              : onSubscribe([Fuseable] MonoDelayElement.DelayElementSubscriber)
2023-03-15T09:58:37.590+09:00  INFO 47520 --- [ctor-http-nio-3] reactor.Mono.DelayElement.10             : onSubscribe([Fuseable] MonoDelayElement.DelayElementSubscriber)
2023-03-15T09:58:37.592+09:00  INFO 47520 --- [ctor-http-nio-2] reactor.Mono.DelayElement.3              : request(unbounded)
2023-03-15T09:58:37.592+09:00  INFO 47520 --- [ctor-http-nio-7] reactor.Mono.DelayElement.5              : request(unbounded)
2023-03-15T09:58:37.592+09:00  INFO 47520 --- [tor-http-nio-10] reactor.Mono.DelayElement.8              : request(unbounded)
2023-03-15T09:58:37.592+09:00  INFO 47520 --- [tor-http-nio-11] reactor.Mono.DelayElement.1              : request(unbounded)
2023-03-15T09:58:37.592+09:00  INFO 47520 --- [ctor-http-nio-5] reactor.Mono.DelayElement.7              : request(unbounded)
2023-03-15T09:58:37.592+09:00  INFO 47520 --- [ctor-http-nio-4] reactor.Mono.DelayElement.9              : request(unbounded)
2023-03-15T09:58:37.592+09:00  INFO 47520 --- [ctor-http-nio-9] reactor.Mono.DelayElement.6              : request(unbounded)
2023-03-15T09:58:37.592+09:00  INFO 47520 --- [ctor-http-nio-8] reactor.Mono.DelayElement.4              : request(unbounded)
2023-03-15T09:58:37.592+09:00  INFO 47520 --- [ctor-http-nio-6] reactor.Mono.DelayElement.2              : request(unbounded)
2023-03-15T09:58:37.593+09:00  INFO 47520 --- [ctor-http-nio-3] reactor.Mono.DelayElement.10             : request(unbounded)
2023-03-15T09:58:38.595+09:00  INFO 47520 --- [     parallel-1] reactor.Mono.DelayElement.2              : onNext(Test 9)
2023-03-15T09:58:38.595+09:00  INFO 47520 --- [     parallel-5] reactor.Mono.DelayElement.7              : onNext(Test 6)
2023-03-15T09:58:38.595+09:00  INFO 47520 --- [     parallel-3] reactor.Mono.DelayElement.10             : onNext(Test 7)
2023-03-15T09:58:38.595+09:00  INFO 47520 --- [     parallel-2] reactor.Mono.DelayElement.5              : onNext(Test 5)
2023-03-15T09:58:38.595+09:00  INFO 47520 --- [     parallel-4] reactor.Mono.DelayElement.3              : onNext(Test 0)
2023-03-15T09:58:38.595+09:00  INFO 47520 --- [     parallel-6] reactor.Mono.DelayElement.4              : onNext(Test 4)
2023-03-15T09:58:38.597+09:00 DEBUG 47520 --- [     parallel-6] org.springframework.web.HttpLogging      : [a53ae936-10] Writing "Test 4"
2023-03-15T09:58:38.597+09:00 DEBUG 47520 --- [     parallel-1] org.springframework.web.HttpLogging      : [c0186e3a-9] Writing "Test 9"
2023-03-15T09:58:38.597+09:00 DEBUG 47520 --- [     parallel-4] org.springframework.web.HttpLogging      : [45dcb2ff-3] Writing "Test 0"
2023-03-15T09:58:38.597+09:00 DEBUG 47520 --- [     parallel-3] org.springframework.web.HttpLogging      : [3aef298b-4] Writing "Test 7"
2023-03-15T09:58:38.597+09:00 DEBUG 47520 --- [     parallel-5] org.springframework.web.HttpLogging      : [ab53b10f-7] Writing "Test 6"
2023-03-15T09:58:38.598+09:00 DEBUG 47520 --- [     parallel-2] org.springframework.web.HttpLogging      : [541deff5-1] Writing "Test 5"
2023-03-15T09:58:38.605+09:00  INFO 47520 --- [     parallel-6] reactor.Mono.DelayElement.4              : onComplete()
2023-03-15T09:58:38.605+09:00  INFO 47520 --- [     parallel-3] reactor.Mono.DelayElement.10             : onComplete()
2023-03-15T09:58:38.605+09:00  INFO 47520 --- [     parallel-2] reactor.Mono.DelayElement.5              : onComplete()
2023-03-15T09:58:38.605+09:00  INFO 47520 --- [     parallel-5] reactor.Mono.DelayElement.7              : onComplete()
2023-03-15T09:58:38.605+09:00  INFO 47520 --- [     parallel-1] reactor.Mono.DelayElement.2              : onComplete()
2023-03-15T09:58:38.605+09:00  INFO 47520 --- [     parallel-4] reactor.Mono.DelayElement.3              : onComplete()
2023-03-15T09:58:38.616+09:00  INFO 47520 --- [     parallel-6] reactor.Mono.DelayElement.9              : onNext(Test 8)
2023-03-15T09:58:38.616+09:00  INFO 47520 --- [     parallel-5] reactor.Mono.DelayElement.8              : onNext(Test 3)
2023-03-15T09:58:38.616+09:00  INFO 47520 --- [     parallel-2] reactor.Mono.DelayElement.6              : onNext(Test 2)
2023-03-15T09:58:38.616+09:00 DEBUG 47520 --- [     parallel-6] org.springframework.web.HttpLogging      : [c2e6dd53-6] Writing "Test 8"
2023-03-15T09:58:38.616+09:00 DEBUG 47520 --- [     parallel-2] org.springframework.web.HttpLogging      : [315ae850-8] Writing "Test 2"
2023-03-15T09:58:38.616+09:00 DEBUG 47520 --- [     parallel-5] org.springframework.web.HttpLogging      : [2261a8f5-5] Writing "Test 3"
2023-03-15T09:58:38.616+09:00  INFO 47520 --- [     parallel-2] reactor.Mono.DelayElement.6              : onComplete()
2023-03-15T09:58:38.616+09:00  INFO 47520 --- [     parallel-5] reactor.Mono.DelayElement.8              : onComplete()
2023-03-15T09:58:38.616+09:00  INFO 47520 --- [     parallel-6] reactor.Mono.DelayElement.9              : onComplete()
2023-03-15T09:58:38.616+09:00  INFO 47520 --- [     parallel-6] reactor.Mono.DelayElement.1              : onNext(Test 1)
2023-03-15T09:58:38.616+09:00 DEBUG 47520 --- [     parallel-6] org.springframework.web.HttpLogging      : [8b4fee12-2] Writing "Test 1"
2023-03-15T09:58:38.616+09:00  INFO 47520 --- [     parallel-6] reactor.Mono.DelayElement.1              : onComplete()

```

- Response에서 다시 Request 때 사용했던 netty thread 사용
```
2023-03-15T09:58:38.621+09:00 DEBUG 47520 --- [ctor-http-nio-5] o.s.w.s.adapter.HttpWebHandlerAdapter    : [ab53b10f-7] Completed 200 OK
2023-03-15T09:58:38.621+09:00 DEBUG 47520 --- [ctor-http-nio-3] o.s.w.s.adapter.HttpWebHandlerAdapter    : [3aef298b-4] Completed 200 OK
2023-03-15T09:58:38.621+09:00 DEBUG 47520 --- [ctor-http-nio-6] o.s.w.s.adapter.HttpWebHandlerAdapter    : [c0186e3a-9] Completed 200 OK
2023-03-15T09:58:38.621+09:00 DEBUG 47520 --- [tor-http-nio-10] o.s.w.s.adapter.HttpWebHandlerAdapter    : [2261a8f5-5] Completed 200 OK
2023-03-15T09:58:38.621+09:00 DEBUG 47520 --- [ctor-http-nio-2] o.s.w.s.adapter.HttpWebHandlerAdapter    : [45dcb2ff-3] Completed 200 OK
2023-03-15T09:58:38.621+09:00 DEBUG 47520 --- [tor-http-nio-11] o.s.w.s.adapter.HttpWebHandlerAdapter    : [8b4fee12-2] Completed 200 OK
2023-03-15T09:58:38.621+09:00 DEBUG 47520 --- [ctor-http-nio-4] o.s.w.s.adapter.HttpWebHandlerAdapter    : [c2e6dd53-6] Completed 200 OK
2023-03-15T09:58:38.621+09:00 DEBUG 47520 --- [ctor-http-nio-8] o.s.w.s.adapter.HttpWebHandlerAdapter    : [a53ae936-10] Completed 200 OK
2023-03-15T09:58:38.621+09:00 DEBUG 47520 --- [ctor-http-nio-7] o.s.w.s.adapter.HttpWebHandlerAdapter    : [541deff5-1] Completed 200 OK
2023-03-15T09:58:38.621+09:00 DEBUG 47520 --- [ctor-http-nio-9] o.s.w.s.adapter.HttpWebHandlerAdapter    : [315ae850-8] Completed 200 OK
```

### 그럼 Blocking Task 가 없어도 Request Handle 이후, Thread가 분리될까? > NO
- Blocking  Task 없으면 스레드 분리되지 않고 그대로 진행됨
```
2023-03-15T10:05:52.197+09:00 DEBUG 57214 --- [ctor-http-nio-2] o.s.w.s.adapter.HttpWebHandlerAdapter    : [be7e2fb4-8] HTTP GET "/test?num=1"
2023-03-15T10:05:52.197+09:00 DEBUG 57214 --- [ctor-http-nio-9] o.s.w.s.adapter.HttpWebHandlerAdapter    : [e24f127c-2] HTTP GET "/test?num=8"
2023-03-15T10:05:52.197+09:00 DEBUG 57214 --- [ctor-http-nio-7] o.s.w.s.adapter.HttpWebHandlerAdapter    : [23b8794b-6] HTTP GET "/test?num=7"
2023-03-15T10:05:52.197+09:00 DEBUG 57214 --- [ctor-http-nio-4] o.s.w.s.adapter.HttpWebHandlerAdapter    : [fcee49ef-1] HTTP GET "/test?num=0"
2023-03-15T10:05:52.197+09:00 DEBUG 57214 --- [ctor-http-nio-5] o.s.w.s.adapter.HttpWebHandlerAdapter    : [441f0e3a-10] HTTP GET "/test?num=4"
2023-03-15T10:05:52.197+09:00 DEBUG 57214 --- [ctor-http-nio-3] o.s.w.s.adapter.HttpWebHandlerAdapter    : [5add246d-7] HTTP GET "/test?num=6"
2023-03-15T10:05:52.197+09:00 DEBUG 57214 --- [tor-http-nio-11] o.s.w.s.adapter.HttpWebHandlerAdapter    : [bb1b0ebe-4] HTTP GET "/test?num=2"
2023-03-15T10:05:52.197+09:00 DEBUG 57214 --- [ctor-http-nio-8] o.s.w.s.adapter.HttpWebHandlerAdapter    : [0ca667dc-5] HTTP GET "/test?num=5"
2023-03-15T10:05:52.197+09:00 DEBUG 57214 --- [tor-http-nio-10] o.s.w.s.adapter.HttpWebHandlerAdapter    : [acdbc98a-9] HTTP GET "/test?num=9"
2023-03-15T10:05:52.197+09:00 DEBUG 57214 --- [ctor-http-nio-6] o.s.w.s.adapter.HttpWebHandlerAdapter    : [e80b74a5-3] HTTP GET "/test?num=3"
 
2023-03-15T10:05:52.272+09:00  INFO 57214 --- [ctor-http-nio-7] reactor.Mono.Supplier.8                  : | onSubscribe([Fuseable] MonoSupplier.MonoSupplierSubscription)
2023-03-15T10:05:52.272+09:00  INFO 57214 --- [ctor-http-nio-9] reactor.Mono.Supplier.6                  : | onSubscribe([Fuseable] MonoSupplier.MonoSupplierSubscription)
2023-03-15T10:05:52.272+09:00  INFO 57214 --- [ctor-http-nio-5] reactor.Mono.Supplier.9                  : | onSubscribe([Fuseable] MonoSupplier.MonoSupplierSubscription)
2023-03-15T10:05:52.272+09:00  INFO 57214 --- [tor-http-nio-10] reactor.Mono.Supplier.7                  : | onSubscribe([Fuseable] MonoSupplier.MonoSupplierSubscription)
2023-03-15T10:05:52.272+09:00  INFO 57214 --- [tor-http-nio-11] reactor.Mono.Supplier.2                  : | onSubscribe([Fuseable] MonoSupplier.MonoSupplierSubscription)
2023-03-15T10:05:52.272+09:00  INFO 57214 --- [ctor-http-nio-6] reactor.Mono.Supplier.3                  : | onSubscribe([Fuseable] MonoSupplier.MonoSupplierSubscription)
2023-03-15T10:05:52.272+09:00  INFO 57214 --- [ctor-http-nio-8] reactor.Mono.Supplier.5                  : | onSubscribe([Fuseable] MonoSupplier.MonoSupplierSubscription)
2023-03-15T10:05:52.272+09:00  INFO 57214 --- [ctor-http-nio-4] reactor.Mono.Supplier.1                  : | onSubscribe([Fuseable] MonoSupplier.MonoSupplierSubscription)
2023-03-15T10:05:52.272+09:00  INFO 57214 --- [ctor-http-nio-3] reactor.Mono.Supplier.10                 : | onSubscribe([Fuseable] MonoSupplier.MonoSupplierSubscription)
2023-03-15T10:05:52.272+09:00  INFO 57214 --- [ctor-http-nio-2] reactor.Mono.Supplier.4                  : | onSubscribe([Fuseable] MonoSupplier.MonoSupplierSubscription)
2023-03-15T10:05:52.274+09:00  INFO 57214 --- [ctor-http-nio-9] reactor.Mono.LogFuseable.19              : | onSubscribe([Fuseable] FluxPeekFuseable.PeekFuseableSubscriber)
2023-03-15T10:05:52.274+09:00  INFO 57214 --- [tor-http-nio-11] reactor.Mono.LogFuseable.11              : | onSubscribe([Fuseable] FluxPeekFuseable.PeekFuseableSubscriber)
2023-03-15T10:05:52.274+09:00  INFO 57214 --- [ctor-http-nio-6] reactor.Mono.LogFuseable.16              : | onSubscribe([Fuseable] FluxPeekFuseable.PeekFuseableSubscriber)
2023-03-15T10:05:52.274+09:00  INFO 57214 --- [ctor-http-nio-4] reactor.Mono.LogFuseable.13              : | onSubscribe([Fuseable] FluxPeekFuseable.PeekFuseableSubscriber)
2023-03-15T10:05:52.274+09:00  INFO 57214 --- [ctor-http-nio-2] reactor.Mono.LogFuseable.14              : | onSubscribe([Fuseable] FluxPeekFuseable.PeekFuseableSubscriber)
2023-03-15T10:05:52.274+09:00  INFO 57214 --- [ctor-http-nio-8] reactor.Mono.LogFuseable.18              : | onSubscribe([Fuseable] FluxPeekFuseable.PeekFuseableSubscriber)
2023-03-15T10:05:52.274+09:00  INFO 57214 --- [ctor-http-nio-7] reactor.Mono.LogFuseable.20              : | onSubscribe([Fuseable] FluxPeekFuseable.PeekFuseableSubscriber)
2023-03-15T10:05:52.274+09:00  INFO 57214 --- [ctor-http-nio-3] reactor.Mono.LogFuseable.12              : | onSubscribe([Fuseable] FluxPeekFuseable.PeekFuseableSubscriber)
2023-03-15T10:05:52.274+09:00  INFO 57214 --- [tor-http-nio-10] reactor.Mono.LogFuseable.17              : | onSubscribe([Fuseable] FluxPeekFuseable.PeekFuseableSubscriber)
2023-03-15T10:05:52.274+09:00  INFO 57214 --- [ctor-http-nio-5] reactor.Mono.LogFuseable.15              : | onSubscribe([Fuseable] FluxPeekFuseable.PeekFuseableSubscriber)
2023-03-15T10:05:52.274+09:00  INFO 57214 --- [ctor-http-nio-5] reactor.Mono.LogFuseable.15              : | request(unbounded)
2023-03-15T10:05:52.274+09:00  INFO 57214 --- [ctor-http-nio-2] reactor.Mono.LogFuseable.14              : | request(unbounded)
2023-03-15T10:05:52.274+09:00  INFO 57214 --- [tor-http-nio-11] reactor.Mono.LogFuseable.11              : | request(unbounded)
2023-03-15T10:05:52.274+09:00  INFO 57214 --- [ctor-http-nio-7] reactor.Mono.LogFuseable.20              : | request(unbounded)
2023-03-15T10:05:52.274+09:00  INFO 57214 --- [tor-http-nio-10] reactor.Mono.LogFuseable.17              : | request(unbounded)
2023-03-15T10:05:52.274+09:00  INFO 57214 --- [ctor-http-nio-3] reactor.Mono.LogFuseable.12              : | request(unbounded)
2023-03-15T10:05:52.274+09:00  INFO 57214 --- [ctor-http-nio-5] reactor.Mono.Supplier.9                  : | request(unbounded)
2023-03-15T10:05:52.274+09:00  INFO 57214 --- [ctor-http-nio-9] reactor.Mono.LogFuseable.19              : | request(unbounded)
2023-03-15T10:05:52.274+09:00  INFO 57214 --- [ctor-http-nio-2] reactor.Mono.Supplier.4                  : | request(unbounded)
2023-03-15T10:05:52.274+09:00  INFO 57214 --- [ctor-http-nio-4] reactor.Mono.LogFuseable.13              : | request(unbounded)
2023-03-15T10:05:52.274+09:00  INFO 57214 --- [ctor-http-nio-7] reactor.Mono.Supplier.8                  : | request(unbounded)
2023-03-15T10:05:52.274+09:00  INFO 57214 --- [tor-http-nio-10] reactor.Mono.Supplier.7                  : | request(unbounded)
2023-03-15T10:05:52.274+09:00  INFO 57214 --- [ctor-http-nio-3] reactor.Mono.Supplier.10                 : | request(unbounded)
2023-03-15T10:05:52.274+09:00  INFO 57214 --- [ctor-http-nio-6] reactor.Mono.LogFuseable.16              : | request(unbounded)
2023-03-15T10:05:52.274+09:00  INFO 57214 --- [ctor-http-nio-8] reactor.Mono.LogFuseable.18              : | request(unbounded)
2023-03-15T10:05:52.274+09:00  INFO 57214 --- [tor-http-nio-11] reactor.Mono.Supplier.2                  : | request(unbounded)
2023-03-15T10:05:52.274+09:00  INFO 57214 --- [ctor-http-nio-9] reactor.Mono.Supplier.6                  : | request(unbounded)
2023-03-15T10:05:52.274+09:00  INFO 57214 --- [ctor-http-nio-4] reactor.Mono.Supplier.1                  : | request(unbounded)
2023-03-15T10:05:52.275+09:00  INFO 57214 --- [ctor-http-nio-6] reactor.Mono.Supplier.3                  : | request(unbounded)
2023-03-15T10:05:52.275+09:00  INFO 57214 --- [ctor-http-nio-8] reactor.Mono.Supplier.5                  : | request(unbounded)
2023-03-15T10:05:52.275+09:00  INFO 57214 --- [ctor-http-nio-5] reactor.Mono.Supplier.9                  : | onNext(Test 4)
2023-03-15T10:05:52.275+09:00  INFO 57214 --- [tor-http-nio-11] reactor.Mono.Supplier.2                  : | onNext(Test 2)
2023-03-15T10:05:52.275+09:00  INFO 57214 --- [ctor-http-nio-3] reactor.Mono.Supplier.10                 : | onNext(Test 6)
2023-03-15T10:05:52.275+09:00  INFO 57214 --- [ctor-http-nio-2] reactor.Mono.Supplier.4                  : | onNext(Test 1)
2023-03-15T10:05:52.275+09:00  INFO 57214 --- [tor-http-nio-10] reactor.Mono.Supplier.7                  : | onNext(Test 9)
2023-03-15T10:05:52.275+09:00  INFO 57214 --- [tor-http-nio-11] reactor.Mono.LogFuseable.11              : | onNext(Test 2)
2023-03-15T10:05:52.275+09:00  INFO 57214 --- [ctor-http-nio-7] reactor.Mono.Supplier.8                  : | onNext(Test 7)
2023-03-15T10:05:52.275+09:00  INFO 57214 --- [ctor-http-nio-2] reactor.Mono.LogFuseable.14              : | onNext(Test 1)
2023-03-15T10:05:52.275+09:00  INFO 57214 --- [ctor-http-nio-6] reactor.Mono.Supplier.3                  : | onNext(Test 3)
2023-03-15T10:05:52.275+09:00  INFO 57214 --- [ctor-http-nio-9] reactor.Mono.Supplier.6                  : | onNext(Test 8)
2023-03-15T10:05:52.275+09:00  INFO 57214 --- [ctor-http-nio-8] reactor.Mono.Supplier.5                  : | onNext(Test 5)
2023-03-15T10:05:52.275+09:00  INFO 57214 --- [tor-http-nio-10] reactor.Mono.LogFuseable.17              : | onNext(Test 9)
2023-03-15T10:05:52.275+09:00  INFO 57214 --- [ctor-http-nio-7] reactor.Mono.LogFuseable.20              : | onNext(Test 7)
2023-03-15T10:05:52.275+09:00  INFO 57214 --- [ctor-http-nio-8] reactor.Mono.LogFuseable.18              : | onNext(Test 5)
2023-03-15T10:05:52.275+09:00  INFO 57214 --- [ctor-http-nio-6] reactor.Mono.LogFuseable.16              : | onNext(Test 3)
2023-03-15T10:05:52.275+09:00  INFO 57214 --- [ctor-http-nio-9] reactor.Mono.LogFuseable.19              : | onNext(Test 8)
2023-03-15T10:05:52.275+09:00  INFO 57214 --- [ctor-http-nio-4] reactor.Mono.Supplier.1                  : | onNext(Test 0)
2023-03-15T10:05:52.275+09:00  INFO 57214 --- [ctor-http-nio-5] reactor.Mono.LogFuseable.15              : | onNext(Test 4)
2023-03-15T10:05:52.275+09:00  INFO 57214 --- [ctor-http-nio-3] reactor.Mono.LogFuseable.12              : | onNext(Test 6)
2023-03-15T10:05:52.275+09:00  INFO 57214 --- [ctor-http-nio-4] reactor.Mono.LogFuseable.13              : | onNext(Test 0)
2023-03-15T10:05:52.283+09:00 DEBUG 57214 --- [ctor-http-nio-4] org.springframework.web.HttpLogging      : [fcee49ef-1] Writing "Test 0"
2023-03-15T10:05:52.283+09:00 DEBUG 57214 --- [ctor-http-nio-8] org.springframework.web.HttpLogging      : [0ca667dc-5] Writing "Test 5"
2023-03-15T10:05:52.283+09:00 DEBUG 57214 --- [tor-http-nio-10] org.springframework.web.HttpLogging      : [acdbc98a-9] Writing "Test 9"
2023-03-15T10:05:52.283+09:00 DEBUG 57214 --- [ctor-http-nio-7] org.springframework.web.HttpLogging      : [23b8794b-6] Writing "Test 7"
2023-03-15T10:05:52.283+09:00 DEBUG 57214 --- [ctor-http-nio-3] org.springframework.web.HttpLogging      : [5add246d-7] Writing "Test 6"
2023-03-15T10:05:52.283+09:00 DEBUG 57214 --- [tor-http-nio-11] org.springframework.web.HttpLogging      : [bb1b0ebe-4] Writing "Test 2"
2023-03-15T10:05:52.283+09:00 DEBUG 57214 --- [ctor-http-nio-6] org.springframework.web.HttpLogging      : [e80b74a5-3] Writing "Test 3"
2023-03-15T10:05:52.283+09:00 DEBUG 57214 --- [ctor-http-nio-9] org.springframework.web.HttpLogging      : [e24f127c-2] Writing "Test 8"
2023-03-15T10:05:52.283+09:00 DEBUG 57214 --- [ctor-http-nio-5] org.springframework.web.HttpLogging      : [441f0e3a-10] Writing "Test 4"
2023-03-15T10:05:52.283+09:00 DEBUG 57214 --- [ctor-http-nio-2] org.springframework.web.HttpLogging      : [be7e2fb4-8] Writing "Test 1"
2023-03-15T10:05:52.287+09:00  INFO 57214 --- [ctor-http-nio-2] reactor.Mono.Supplier.4                  : | onComplete()
2023-03-15T10:05:52.287+09:00  INFO 57214 --- [ctor-http-nio-7] reactor.Mono.Supplier.8                  : | onComplete()
2023-03-15T10:05:52.287+09:00  INFO 57214 --- [ctor-http-nio-5] reactor.Mono.Supplier.9                  : | onComplete()
2023-03-15T10:05:52.287+09:00  INFO 57214 --- [tor-http-nio-11] reactor.Mono.Supplier.2                  : | onComplete()
2023-03-15T10:05:52.287+09:00  INFO 57214 --- [ctor-http-nio-9] reactor.Mono.Supplier.6                  : | onComplete()
2023-03-15T10:05:52.287+09:00  INFO 57214 --- [ctor-http-nio-6] reactor.Mono.Supplier.3                  : | onComplete()
2023-03-15T10:05:52.287+09:00  INFO 57214 --- [ctor-http-nio-8] reactor.Mono.Supplier.5                  : | onComplete()
2023-03-15T10:05:52.288+09:00  INFO 57214 --- [ctor-http-nio-7] reactor.Mono.LogFuseable.20              : | onComplete()
2023-03-15T10:05:52.287+09:00  INFO 57214 --- [ctor-http-nio-4] reactor.Mono.Supplier.1                  : | onComplete()
2023-03-15T10:05:52.288+09:00  INFO 57214 --- [tor-http-nio-11] reactor.Mono.LogFuseable.11              : | onComplete()
2023-03-15T10:05:52.288+09:00  INFO 57214 --- [ctor-http-nio-6] reactor.Mono.LogFuseable.16              : | onComplete()
2023-03-15T10:05:52.287+09:00  INFO 57214 --- [tor-http-nio-10] reactor.Mono.Supplier.7                  : | onComplete()
2023-03-15T10:05:52.288+09:00  INFO 57214 --- [ctor-http-nio-8] reactor.Mono.LogFuseable.18              : | onComplete()
2023-03-15T10:05:52.287+09:00  INFO 57214 --- [ctor-http-nio-3] reactor.Mono.Supplier.10                 : | onComplete()
2023-03-15T10:05:52.288+09:00  INFO 57214 --- [ctor-http-nio-4] reactor.Mono.LogFuseable.13              : | onComplete()
2023-03-15T10:05:52.288+09:00  INFO 57214 --- [ctor-http-nio-2] reactor.Mono.LogFuseable.14              : | onComplete()
2023-03-15T10:05:52.288+09:00  INFO 57214 --- [ctor-http-nio-5] reactor.Mono.LogFuseable.15              : | onComplete()
2023-03-15T10:05:52.288+09:00  INFO 57214 --- [ctor-http-nio-9] reactor.Mono.LogFuseable.19              : | onComplete()
2023-03-15T10:05:52.288+09:00  INFO 57214 --- [tor-http-nio-10] reactor.Mono.LogFuseable.17              : | onComplete()
2023-03-15T10:05:52.288+09:00  INFO 57214 --- [ctor-http-nio-3] reactor.Mono.LogFuseable.12              : | onComplete()
2023-03-15T10:05:52.298+09:00 DEBUG 57214 --- [ctor-http-nio-6] o.s.w.s.adapter.HttpWebHandlerAdapter    : [e80b74a5-3] Completed 200 OK
2023-03-15T10:05:52.298+09:00 DEBUG 57214 --- [ctor-http-nio-8] o.s.w.s.adapter.HttpWebHandlerAdapter    : [0ca667dc-5] Completed 200 OK
2023-03-15T10:05:52.298+09:00 DEBUG 57214 --- [ctor-http-nio-4] o.s.w.s.adapter.HttpWebHandlerAdapter    : [fcee49ef-1] Completed 200 OK
2023-03-15T10:05:52.298+09:00 DEBUG 57214 --- [ctor-http-nio-2] o.s.w.s.adapter.HttpWebHandlerAdapter    : [be7e2fb4-8] Completed 200 OK
2023-03-15T10:05:52.298+09:00 DEBUG 57214 --- [tor-http-nio-11] o.s.w.s.adapter.HttpWebHandlerAdapter    : [bb1b0ebe-4] Completed 200 OK
2023-03-15T10:05:52.298+09:00 DEBUG 57214 --- [ctor-http-nio-7] o.s.w.s.adapter.HttpWebHandlerAdapter    : [23b8794b-6] Completed 200 OK
2023-03-15T10:05:52.298+09:00 DEBUG 57214 --- [ctor-http-nio-9] o.s.w.s.adapter.HttpWebHandlerAdapter    : [e24f127c-2] Completed 200 OK
2023-03-15T10:05:52.298+09:00 DEBUG 57214 --- [tor-http-nio-10] o.s.w.s.adapter.HttpWebHandlerAdapter    : [acdbc98a-9] Completed 200 OK
2023-03-15T10:05:52.298+09:00 DEBUG 57214 --- [ctor-http-nio-3] o.s.w.s.adapter.HttpWebHandlerAdapter    : [5add246d-7] Completed 200 OK
2023-03-15T10:05:52.298+09:00 DEBUG 57214 --- [ctor-http-nio-5] o.s.w.s.adapter.HttpWebHandlerAdapter    : [441f0e3a-10] Completed 200 OK
```
- https://github.com/ChangguHan/MVCvsWebflux/blob/main/webflux/src/main/java/com/example/webflux3/WebfluxApplication3.java

## 결론
- 웹플럭스가, Request, Response의 스레드풀을 별도로 관리하는것이 아니라 (생각해보면, 기계적으로 나눠주는게 비효율적일것 같긴함)
- Pub-Sub 구조에서 Blocking 작업을 만날때, 다른 스레드로 옮겨줌
