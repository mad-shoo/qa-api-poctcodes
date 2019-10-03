package TestHelper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;


public class PostCodeHelper {
    private static HttpResponse response;
    private static HttpClient client = HttpClientBuilder.create().build();
    private final static Logger LOGGER = LoggerFactory.getLogger(PostCodeHelper.class);


    public static HttpResponse getRequest(String URI, String postCode, Boolean queryParameter) throws IOException, URISyntaxException {
        HttpGet request;
        if (Boolean.TRUE.equals(queryParameter)) {
            request = new HttpGet(new URIBuilder().setScheme("https")
                    .setHost("api.postcodes.io")
                    .setPath("/postcodes")
                    .setParameter("q", postCode).build());
        } else
            request = new HttpGet(URI + postCode);
        LOGGER.info(String.valueOf(request));
        response = client.execute(request);
        LOGGER.info(String.valueOf(response));
        return response;
    }

    public static HttpResponse postRequest(String URI, JSONObject json) throws IOException {
        HttpPost request = new HttpPost(URI);
        request.addHeader("content-type", "application/json");
        StringEntity userEntity = new StringEntity(json.toJSONString());
        request.setEntity(userEntity);
        response = client.execute(request);
        LOGGER.info(String.valueOf(response));
        return response;
    }
}
