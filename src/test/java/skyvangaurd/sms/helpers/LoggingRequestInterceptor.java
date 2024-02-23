package skyvangaurd.sms.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
      throws IOException {
    // Log request details here
    logger.info("Request URI: " + request.getURI());
    logger.info("Request Method: " + request.getMethod());
    logger.info("Request Headers: " + request.getHeaders());
    // Proceed with the request
    return execution.execute(request, body);
  }
}
