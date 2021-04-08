# armeria #3447

This repository consists of two Spring Boot applications to reproduce
the [armeria#3447](https://github.com/line/armeria/issues/3447):

- `spring-boot-tomcat`: A pure Spring Boot application, so works well.
- `spring-boot-tomcat-armeria`: Integrated with the Armeria. The Armeria server accepts HTTP requests, and
  serves using a `TomcatService` which is connected with the embedded Tomcat in `spring-boot-web-starter`.

Both applications provide three APIs, `/foo`, `/bar` and `/baz`. The `/foo` API works well and
returns `Hello, World!` response. The `/bar` API throws a `RuntimeException`. So the exception is handled by
Spring Boot's default error handling mechanism (/error mapping
in [Error Handling](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-error-handling))
and returned `500 Internal Server Error` with body consists of `status`, `error`, etc. Lastly, the `/baz` API
throws a `CustomException`. The exception is handled by `SimpleControllerAdvice#handleControllerException` and
returned `500 Internal Server Error` with body consists of `errorStatusCode`, `errorMessage`.

Expected behaviors are described in the tests. Although both applications are almost same in business logic, the
`spring-boot-tomcat-armeria` application does not handle exceptions by the Spring Boot. So the response does not
have any content and tests are failed.

## License

```
MIT License

Copyright (c) 2021 Gihwan Kim

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
